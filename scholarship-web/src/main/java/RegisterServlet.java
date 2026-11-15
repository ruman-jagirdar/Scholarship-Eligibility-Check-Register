import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
                          throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain");

        String usn = request.getParameter("usn");
        String name = request.getParameter("fullName");
        String course = request.getParameter("course");
        String semester = request.getParameter("semester");
        String category = request.getParameter("category");
        String contact = request.getParameter("contact");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Hash the password before storing it
        String hashedPassword = BCrypt.hashpw(
            password, BCrypt.gensalt()
        );

        String studentSQL =
            "INSERT INTO student (usn, name, course, semester, category, contact) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        String userSQL =
            "INSERT INTO app_user (username, password_hash, role) " +
            "VALUES (?, ?, 'STUDENT')";

        try (Connection con = DBConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                try (PreparedStatement ps = con.prepareStatement(studentSQL)) {
                    ps.setString(1, usn);
                    ps.setString(2, name);
                    ps.setString(3, course);
                    ps.setInt(4, Integer.parseInt(semester));
                    ps.setString(5, category);
                    ps.setString(6, contact);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(userSQL)) {
                    ps.setString(1, username);
                    ps.setString(2, hashedPassword);
                    ps.executeUpdate();
                }

                con.commit();
                response.getWriter().write("Registration successful!");

            } catch (Exception e) {
                con.rollback();
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(
                    "Registration failed. USN or username may already exist."
                );

            } finally {
                con.setAutoCommit(true);
            }

        } catch (Exception e) {
            throw new ServletException("Registration failed.", e);
        }
    }
}
