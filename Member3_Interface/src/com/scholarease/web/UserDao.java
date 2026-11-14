package com.scholarease.web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Reads login users from the MySQL "users" table (see sql/users_table.sql).
 * TODO (integration): replace getConnection() with Member 1's JDBC connection
 * class so the whole project uses one connection setup.
 */
public class UserDao {

    // Same database settings as Member 1's DBConnectionTest.java.
    private static final String URL =
            "jdbc:mysql://localhost:3306/scholarship_register?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    /** A logged-in user: what we keep in the session. */
    public static final class AppUser {
        public final String username;
        public final String role;   // STUDENT, CLERK or ADMIN
        public final String usn;    // only for students, otherwise null

        AppUser(String username, String role, String usn) {
            this.username = username;
            this.role = role;
            this.usn = usn;
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
    }

    /** Returns the user if username and password are correct and the account is active, else null. */
    public AppUser authenticate(String username, String password) throws SQLException {
        String sql = "SELECT username, password_hash, role, usn FROM users "
                   + "WHERE username = ? AND active = 1";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);          // PreparedStatement: no SQL injection
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && PasswordUtil.verify(password, rs.getString("password_hash"))) {
                    return new AppUser(rs.getString("username"),
                                       rs.getString("role"),
                                       rs.getString("usn"));
                }
            }
        }
        return null;
    }
}
