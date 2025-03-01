package HospitalManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Patient {
    private Connection connection;
    private Scanner sc;

    public Patient(Connection connection, Scanner sc) {
        this.connection = connection;
        this.sc = sc;
    }

    public void addPatient() {
        System.out.print("Enter Patient Name: ");
        sc.nextLine();  // Consume any leftover newline
        String name = sc.nextLine(); // Read full name

        System.out.print("Enter Patient's Age: ");
        int age = sc.nextInt();

        System.out.print("Enter Patient's Gender: ");
        sc.nextLine(); // Consume newline left by nextInt()
        String gender = sc.nextLine();

        System.out.println("Enter Patient's Phone Number: ");
        long pnumber=sc.nextLong();
        sc.nextLine();

        System.out.println("Enter Email of Patient: ");
        String email=sc.nextLine();

        String query = "INSERT INTO patients(name, age, gender, pnumber, email) VALUES(?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, age);
            preparedStatement.setString(3, gender);
            preparedStatement.setLong(4,pnumber);
            preparedStatement.setString(5, email);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("✅ Patient data added successfully!");
            } else {
                System.out.println("❌ Failed to add patient data.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Database error while adding patient.");
            e.printStackTrace();
        }
    }

    public void viewPatient() {
        String query = "SELECT * FROM patients";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            System.out.println("+--------------+----------------------------+--------+------------+---------------------+--------------------------------------+");
            System.out.println("| Patient ID   |         Patient Name       |   Age  |   Gender   |   Phone Number      |               Email                  ");
            System.out.println("+--------------+----------------------------+--------+------------+---------------------+--------------------------------------+");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String gender = resultSet.getString("gender");
                Long pnumber=resultSet.getLong("pnumber");
                String email = resultSet.getString("email");

                System.out.printf("| %-12s | %-26s | %-6s | %-10s | %-19s | %-40s|\n", id, name, age, gender,pnumber,email);
            }

            System.out.println("+--------------+----------------------------+--------+------------+---------------------+--------------------------------------+");

        } catch (SQLException e) {
            System.out.println("❌ Error fetching patient data.");
            e.printStackTrace();
        }
    }

    public boolean getPatientByID(int id) {
        String query = "SELECT * FROM patients WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // Returns true if a record is found
            }
        } catch (SQLException e) {
            System.out.println("❌ Error fetching patient by ID.");
            e.printStackTrace();
        }
        return false;
    }
}
