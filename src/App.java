import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        String url = "jdbc:mysql://localhost:3306/hospital"; // Change 'testdb' to your database name
        String user = "root"; // Change to your MySQL username
        String password = "Atin@2025"; // Change to your MySQL password

        try(Connection connection=DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected to the database");
            System.out.println(connection);
        } catch (Exception e) {
            System.out.println("Connection failed: "+e.getMessage());
        }

    }
}
