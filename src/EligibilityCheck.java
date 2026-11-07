
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class EligibilityCheck {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/scholarship_register";
        String username = "root";
        String password = "";

        try (Connection con =
                 DriverManager.getConnection(url, username, password)) {

            Map<String, Double> rules = new HashMap<>();

            String ruleQuery =
                "SELECT rule_code, limit_value FROM rule_value " +
                "WHERE scheme_id = 1";

            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(ruleQuery)) {

                while (rs.next()) {
                    rules.put(
                        rs.getString("rule_code"),
                        rs.getDouble("limit_value")
                    );
                }
            }

            String studentQuery = """
                SELECT s.usn, s.name, a.marks_pct,
                       a.attendance_pct, a.family_income,
                       a.active_scholarship_id, sc.deadline
                FROM student s
                JOIN academic_record a ON s.usn = a.usn
                JOIN scheme sc ON sc.scheme_id = 1
                WHERE a.academic_year = sc.academic_year
                ORDER BY s.usn
                """;

            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(studentQuery)) {

                while (rs.next()) {
                    List<String> reasons = new ArrayList<>();

                    double marks = rs.getDouble("marks_pct");
                    double income = rs.getDouble("family_income");
                    double attendance = rs.getDouble("attendance_pct");

                    if (marks < rules.get("R1")) {
                        reasons.add("R1");
                    }

                    if (income > rules.get("R2")) {
                        reasons.add("R2");
                    }

                    if (attendance < rules.get("R3")) {
                        reasons.add("R3");
                    }

                    if (rs.getObject("active_scholarship_id") != null) {
                        reasons.add("R4");
                    }

                    LocalDate deadline =
                        rs.getDate("deadline").toLocalDate();

                    if (LocalDate.now().isAfter(deadline)) {
                        reasons.add("R5");
                    }

                    String status = reasons.isEmpty()
                        ? "ELIGIBLE"
                        : "NOT_ELIGIBLE";

                    con.setAutoCommit(false);

                    try {
                        int resultId;

                        String findResult =
                            "SELECT result_id FROM eligibility_result " +
                            "WHERE usn = ? AND scheme_id = 1 " +
                            "ORDER BY result_id DESC LIMIT 1";

                        try (PreparedStatement find =
                                 con.prepareStatement(findResult)) {

                            find.setString(1, rs.getString("usn"));

                            try (ResultSet old = find.executeQuery()) {
                                if (old.next()) {
                                    resultId = old.getInt("result_id");

                                    String update =
                                        "UPDATE eligibility_result " +
                                        "SET status = ?, " +
                                        "checked_on = CURRENT_TIMESTAMP " +
                                        "WHERE result_id = ?";

                                    try (PreparedStatement upd =
                                             con.prepareStatement(update)) {
                                        upd.setString(1, status);
                                        upd.setInt(2, resultId);
                                        upd.executeUpdate();
                                    }
                                } else {
                                    String insert =
                                        "INSERT INTO eligibility_result " +
                                        "(usn, scheme_id, status) " +
                                        "VALUES (?, 1, ?)";

                                    try (PreparedStatement ins =
                                             con.prepareStatement(
                                                 insert,
                                                 Statement.RETURN_GENERATED_KEYS)) {
                                        ins.setString(1, rs.getString("usn"));
                                        ins.setString(2, status);
                                        ins.executeUpdate();

                                        try (ResultSet keys =
                                                 ins.getGeneratedKeys()) {
                                            if (!keys.next()) {
                                                throw new SQLException(
                                                    "Could not get result ID."
                                                );
                                            }
                                            resultId = keys.getInt(1);
                                        }
                                    }
                                }
                            }
                        }

                        String deleteReasons =
                            "DELETE FROM result_reason WHERE result_id = ?";

                        try (PreparedStatement del =
                                 con.prepareStatement(deleteReasons)) {
                            del.setInt(1, resultId);
                            del.executeUpdate();
                        }

                        String insertReason =
                            "INSERT INTO result_reason " +
                            "(result_id, rule_code) VALUES (?, ?)";

                        try (PreparedStatement insReason =
                                 con.prepareStatement(insertReason)) {
                            for (String reason : reasons) {
                                insReason.setInt(1, resultId);
                                insReason.setString(2, reason);
                                insReason.executeUpdate();
                            }
                        }

                        String auditQuery =
                            "INSERT INTO audit_log " +
                            "(user_id, action, details) " +
                            "VALUES (NULL, ?, ?)";

                        try (PreparedStatement audit =
                                 con.prepareStatement(auditQuery)) {
                            audit.setString(1, "ELIGIBILITY_CHECKED");
                            audit.setString(
                                2,
                                rs.getString("usn") +
                                " eligibility checked. Status: " + status
                            );
                            audit.executeUpdate();
                        }

                        con.commit();

                        System.out.println(
                            rs.getString("usn") + " - " +
                            rs.getString("name") + " - " + status +
                            (reasons.isEmpty()
                                ? ""
                                : " - Reasons: " +
                                  String.join(", ", reasons))
                        );

                        System.out.println(
                            "Result saved successfully. Result ID: " +
                            resultId
                        );

                        System.out.println("Audit log saved successfully.");

                    } catch (Exception e) {
                        con.rollback();
                        throw e;
                    } finally {
                        con.setAutoCommit(true);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
