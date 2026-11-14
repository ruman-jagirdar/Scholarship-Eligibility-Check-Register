package com.scholarease.web;

import java.io.IOException;
import java.util.Set;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Runs before every request (SRS NFR-01: session required for every screen,
 * role checked on the server for every request).
 *
 *  - Public pages: home, login, register and their CSS/JS.
 *  - /admin*     : ADMIN only.
 *  - /clerk*     : CLERK or ADMIN.
 *  - /dashboard* : STUDENT only.
 *  - anything else that needs login: any logged-in user.
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/", "/index.html", "/login.html", "/register.html",
            "/login", "/logout",
            "/style.css", "/login.css", "/register.css", "/script.js",
            "/login.js", "/register.js", "/favicon.ico");

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getRequestURI().substring(request.getContextPath().length());

        if (PUBLIC_PATHS.contains(path)) {
            chain.doFilter(req, res);
            return;
        }

        HttpSession session = request.getSession(false);
        String role = (session == null) ? null : (String) session.getAttribute("role");

        // Not logged in: go to login.
        if (role == null) {
            response.sendRedirect(request.getContextPath() + "/login.html?error=login");
            return;
        }

        if (!isAllowed(path, role)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "You do not have access to this page.");
            return;
        }

        // Pages with user data must not be kept in the browser after logout.
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        chain.doFilter(req, res);
    }

    private boolean isAllowed(String path, String role) {
        if (path.startsWith("/admin")) {
            return role.equals("ADMIN");
        }
        if (path.startsWith("/clerk")) {
            return role.equals("CLERK") || role.equals("ADMIN");
        }
        if (path.startsWith("/dashboard")) {
            return role.equals("STUDENT");
        }
        return true; // other pages only need a valid login
    }
}
