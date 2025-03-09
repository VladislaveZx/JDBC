package org.example.update;

import java.sql.*;
import java.util.Scanner;

public class JDBCUpdate_v2 {

    static final String DB_URL = "jdbc:postgresql://localhost:5432/test_db";

    static final String USER = "postgres";

    static final String PASSWORD = "8801243";


    public static void main(String[] args) {

        Connection connection = null;



        try {
            connection = DriverManager.getConnection(DB_URL,USER, PASSWORD);

            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter student name: ");
            String enteredName = scanner.nextLine();

            PreparedStatement statement = connection.prepareStatement("UPDATE students SET avg_grade = 7.5 WHERE name = ?");
            statement.setString(1, enteredName);
            statement.executeUpdate();
            connection.close();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
