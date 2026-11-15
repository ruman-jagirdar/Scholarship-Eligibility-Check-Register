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

import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
                          throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        if (username == null || password == null || role == null ||
            username.trim().isEmpty() || password.isEmpty() || role.isEmpty()) {

            response.sendRedirect("login.html?error=missing");
            return;
        }

        String sql =
            "SELECT user_id, username, password_hash, role " +
            "FROM app_user WHERE username = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username.trim());

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    String storedHash = rs.getString("password_hash");
                    String storedRole = rs.getString("role");

                    if (BCrypt.checkpw(password, storedHash) &&
                        storedRole.equalsIgnoreCase(role)) {

                        HttpSession session = request.getSession();
                        session.setAttribute("userId", rs.getInt("user_id"));
                        session.setAttribute("username", rs.getString("username"));
                        session.setAttribute("role", storedRole);

                        if (storedRole.equals("STUDENT")) {
                            response.sendRedirect("dashboard.html");
                        } else if (storedRole.equals("CLERK")) {
                            response.sendRedirect("clerk.html");
                        } else if (storedRole.equals("ADMIN")) {
                            response.sendRedirect("admin.html");
                        }

                    } else {
                        response.sendRedirect("login.html?error=invalid");
                    }

                } else {
                    response.sendRedirect("login.html?error=invalid");
                }
            }

        } catch (Exception e) {
            throw new ServletException("Login failed.", e);
        }
    }
}

