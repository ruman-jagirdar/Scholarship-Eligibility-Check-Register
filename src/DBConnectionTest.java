
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBConnectionTest {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/scholarship_register";
        String username = "root";
        String password = "";

        try {
            Connection con = DriverManager.getConnection(
                url, username, password
            );

            System.out.println("Database connected successfully!");

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(
                "SELECT usn, name FROM student"
            );

            while (rs.next()) {
                System.out.println(
                    rs.getString("usn") + " - " +
                    rs.getString("name")
                );
            }

            rs.close();
            st.close();
            con.close();

        } catch (Exception e) {
            System.out.println("Connection failed!");
            e.printStackTrace();
        }
    }
}

