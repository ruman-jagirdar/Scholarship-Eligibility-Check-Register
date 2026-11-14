package com.scholarease.web;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * GET /session-info  -  tells the page who is logged in, e.g.
 *   {"username":"student1","role":"STUDENT","usn":"01FE24BCA001"}
 * The data comes from the server session, so it cannot be faked in the browser.
 * AuthFilter already blocks this URL when nobody is logged in.
 */
@WebServlet("/session-info")
public class SessionInfoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("role") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.print("{\"username\":\"" + json(session.getAttribute("username")) + "\","
                + "\"role\":\"" + json(session.getAttribute("role")) + "\","
                + "\"usn\":\"" + json(session.getAttribute("usn")) + "\"}");
    }

    /** Escapes text so it is safe inside a JSON string. */
    private static String json(Object value) {
        if (value == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : value.toString().toCharArray()) {
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n");  break;
                case '\r': sb.append("\\r");  break;
                case '\t': sb.append("\\t");  break;
                default:
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
            }
        }
        return sb.toString();
    }
}
