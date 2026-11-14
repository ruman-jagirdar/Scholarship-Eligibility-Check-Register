package com.scholarease.web;

import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/** POST /login  -  checks credentials, starts a session, sends the user to the right page. */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String username = trim(request.getParameter("username"));
        String password = request.getParameter("password");

        if (username.isEmpty() || password == null || password.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/login.html?error=missing");
            return;
        }

        try {
            UserDao.AppUser user = userDao.authenticate(username, password);

            // The "Login As" choice must match the role stored for this account.
            String chosenRole = trim(request.getParameter("role"));
            if (user != null && !user.role.equalsIgnoreCase(chosenRole)) {
                user = null;                     // same message as a wrong password
            }

            if (user == null) {
                // TODO: write a failed-login entry to the audit table (Member 1's audit code)
                response.sendRedirect(request.getContextPath() + "/login.html?error=invalid");
                return;
            }

            // Stop session-fixation: drop any old session, then create a new one.
            HttpSession old = request.getSession(false);
            if (old != null) old.invalidate();
            HttpSession session = request.getSession(true);
            session.setAttribute("username", user.username);
            session.setAttribute("role", user.role);
            session.setAttribute("usn", user.usn);
            session.setMaxInactiveInterval(30 * 60); // 30 minutes

            // TODO: write a successful-login entry to the audit table (SRS: login is audited)
            response.sendRedirect(request.getContextPath() + homePageFor(user.role));

        } catch (SQLException e) {
            log("Login database error", e);
            response.sendRedirect(request.getContextPath() + "/login.html?error=server");
        }
    }

    /** A direct visit to /login in the browser just shows the login page. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/login.html");
    }

    private static String homePageFor(String role) {
        switch (role) {
            case "ADMIN": return "/admin.html";
            case "CLERK": return "/clerk.html";
            default:      return "/dashboard.html";
        }
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
