import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/users")
public class AdminUsersServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
                         throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null ||
            !"ADMIN".equals(session.getAttribute("role"))) {

            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String sql =
            "SELECT user_id, username, role FROM app_user ORDER BY user_id";

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
                    .append("\"id\":").append(rs.getInt("user_id")).append(",")
                    .append("\"username\":\"")
                    .append(escapeJson(rs.getString("username"))).append("\",")
                    .append("\"role\":\"")
                    .append(rs.getString("role")).append("\"")
                    .append("}");

                first = false;
            }

            json.append("]");

            response.getWriter().write(json.toString());

        } catch (Exception e) {
            throw new ServletException("Unable to fetch users.", e);
        }
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"");
    }
}

