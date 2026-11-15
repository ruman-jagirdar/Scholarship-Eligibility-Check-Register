import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/schemes")
public class AdminSchemesServlet extends HttpServlet {

    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        return session != null &&
               "ADMIN".equals(session.getAttribute("role"));
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
                         throws ServletException, IOException {

        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String sql = "SELECT scheme_id, name, academic_year, deadline FROM scheme ORDER BY scheme_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            StringBuilder json = new StringBuilder("[");
            boolean first = true;

            while (rs.next()) {

                if (!first) {
                    json.append(",");
                }

                json.append("{")
                    .append("\"id\":").append(rs.getInt("scheme_id")).append(",")
                    .append("\"name\":\"")
                    .append(escapeJson(rs.getString("name"))).append("\",")
                    .append("\"academicYear\":\"")
                    .append(escapeJson(rs.getString("academic_year"))).append("\",")
                    .append("\"deadline\":\"")
                    .append(rs.getDate("deadline")).append("\"")
                    .append("}");

                first = false;
            }

            json.append("]");

            response.getWriter().write(json.toString());

        } catch (Exception e) {
            throw new ServletException("Unable to fetch scholarship schemes.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
                          throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String name = request.getParameter("name");
        String academicYear = request.getParameter("academicYear");
        String deadline = request.getParameter("deadline");

        if (name == null || name.trim().isEmpty() ||
            academicYear == null ||
            !academicYear.matches("[0-9]{4}-[0-9]{4}") ||
            deadline == null || deadline.trim().isEmpty()) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(
                "{\"message\":\"Please enter valid scheme details.\"}"
            );
            return;
        }

        String sql = "INSERT INTO scheme (name, academic_year, deadline) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name.trim());
            ps.setString(2, academicYear);
            ps.setDate(3, java.sql.Date.valueOf(deadline));

            ps.executeUpdate();

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(
                "{\"message\":\"Scholarship scheme saved successfully.\"}"
            );

        } catch (SQLIntegrityConstraintViolationException e) {

            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write(
                "{\"message\":\"This scholarship already exists for that academic year.\"}"
            );

        } catch (Exception e) {
            throw new ServletException("Unable to save scholarship scheme.", e);
        }
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"");
    }

    @Override
protected void doDelete(HttpServletRequest request,
                        HttpServletResponse response)
                        throws ServletException, IOException {

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    if (!isAdmin(request)) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return;
    }

    String id = request.getParameter("id");

    if (id == null || !id.matches("[0-9]+")) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write(
            "{\"message\":\"Invalid scheme ID.\"}"
        );
        return;
    }

    String checkSql =
        "SELECT " +
        "(SELECT COUNT(*) FROM academic_record WHERE active_scholarship_id = ?) + " +
        "(SELECT COUNT(*) FROM eligibility_result WHERE scheme_id = ?) + " +
        "(SELECT COUNT(*) FROM registration WHERE scheme_id = ?) + " +
        "(SELECT COUNT(*) FROM rule_value WHERE scheme_id = ?) AS total";

    String deleteSql = "DELETE FROM scheme WHERE scheme_id = ?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement check = con.prepareStatement(checkSql)) {

        int schemeId = Integer.parseInt(id);

        for (int i = 1; i <= 4; i++) {
            check.setInt(i, schemeId);
        }

        try (ResultSet rs = check.executeQuery()) {
            rs.next();

            if (rs.getInt("total") > 0) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getWriter().write(
                    "{\"message\":\"Cannot remove this scheme because records are linked to it.\"}"
                );
                return;
            }
        }

        try (PreparedStatement delete = con.prepareStatement(deleteSql)) {
            delete.setInt(1, schemeId);

            int rows = delete.executeUpdate();

            if (rows == 0) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(
                    "{\"message\":\"Scheme not found.\"}"
                );
                return;
            }
        }

        response.getWriter().write(
            "{\"message\":\"Scholarship scheme removed successfully.\"}"
        );

    } catch (Exception e) {
        throw new ServletException("Unable to remove scholarship scheme.", e);
    }
}
}
