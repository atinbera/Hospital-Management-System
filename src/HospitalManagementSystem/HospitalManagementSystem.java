package HospitalManagementSystem;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class HospitalManagementSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "Atin@2025";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Scanner sc = new Scanner(System.in);
        try {
            Connection connection = DriverManager.getConnection(url, username, password);

            Patient patient = new Patient(connection, sc);
            Doctor doctor = new Doctor(connection);

            while (true) {
                System.out.println("--------------------------HOSPITAL MANAGEMENT SYSTEM--------------------- ");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patients");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");
                int point = sc.nextInt();

                switch (point) {
                    case 1:
                        patient.addPatient();
                        break;
                    case 2:
                        patient.viewPatient();
                        System.out.println();
                        break;
                    case 3:
                        doctor.viewDoctors();
                        System.out.println();
                        break;
                    case 4:
                        bookAppointment(patient, doctor, connection, sc);
                        System.out.println();
                        break;
                    case 5:
                        return;
                    default:
                        System.out.println("Enter Valid Choice");
                        break;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner sc) {
        System.out.print("Enter Patient ID: ");
        int patientid = sc.nextInt();
        System.out.print("Enter Doctor ID: ");
        int doctorid = sc.nextInt();
        sc.nextLine(); // Consume newline left by nextInt()

        System.out.print("Enter Appointment Date (DD-MM-YYYY): ");
        String dateInput = sc.nextLine();
        String date = convertDateFormat(dateInput);

        if (date == null) {
            System.out.println("❌ Invalid date format. Please use DD-MM-YYYY.");
            return;
        }

        if (patient.getPatientByID(patientid) && doctor.getDoctorByID(doctorid)) {
            if (checkDoctorAvailability(doctorid, date, connection)) {
                String query = "INSERT INTO appointments (patient_id, doctor_id, date) VALUES (?, ?, ?)";

                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setInt(1, patientid);
                    preparedStatement.setInt(2, doctorid);
                    preparedStatement.setString(3, date);

                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("✅ Appointment Booked Successfully!");
                    } else {
                        System.out.println("❌ Failed to Book Appointment.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("❌ Doctor is not available on this date.");
            }
        } else {
            System.out.println("❌ Either Patient or Doctor is not present.");
        }
    }

    public static boolean checkDoctorAvailability(int doctor_id, String date, Connection connection) {
        //  FIX: Changed 'appointment_date' → 'date' (Make sure MySQL table uses 'date' column)
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND date = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, doctor_id);
            preparedStatement.setString(2, date);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count == 0; // Return true if no appointments found
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //  Added function to convert date format from 'DD-MM-YYYY' to 'YYYY-MM-DD'
    private static String convertDateFormat(String inputDate) {
        try {
            SimpleDateFormat fromFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd");
            return toFormat.format(fromFormat.parse(inputDate));
        } catch (ParseException e) {
            return null; // Return null if date parsing fails
        }
    }
}
