
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class RuleTest {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/scholarship_register";
        String username = "root";
        String password = "";

        try {
            Connection con = DriverManager.getConnection(
                url, username, password
            );

            String query =
                "SELECT rule_code, limit_value FROM rule_value " +
                "WHERE scheme_id = 1 ORDER BY rule_code";

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);

            System.out.println("Scholarship Eligibility Rules:");

            while (rs.next()) {
                System.out.println(
                    rs.getString("rule_code") + " = " +
                    rs.getBigDecimal("limit_value")
                );
            }

            rs.close();
            st.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
