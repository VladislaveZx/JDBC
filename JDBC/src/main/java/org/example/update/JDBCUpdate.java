package org.example.update;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class JDBCUpdate {

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

            Statement statement = connection.createStatement();

            String sqlQuery = "UPDATE students SET avg_grade = 7.5 " + "WHERE name = '" + enteredName + "'";

            //sql injection: UPDATE students SET avg_grade = 7.5 WHERE name = '' OR '1'='1'\

            statement.executeUpdate(sqlQuery);

            statement.close();
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
