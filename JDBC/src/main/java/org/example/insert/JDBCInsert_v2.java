package org.example.insert;

import org.example.entity.Student;

import java.sql.*;

public class JDBCInsert_v2 {

    static final String DB_URL = "jdbc:postgresql://localhost:5432/test_db";

    static final String USER = "postgres";

    static final String PASSWORD = "8801243";


    public static void main(String[] args) {

        Connection connection = null;

        Student student = new Student("Leo","Farrel",8.4);

        try {
            connection = DriverManager.getConnection(DB_URL,USER, PASSWORD);

            Statement statement = connection.createStatement();

            String sqlQuery = "INSERT INTO students(name, surename,avg_grade) VALUES" + "('"
                    + student.getName() + "','"
                    + student.getSurname() + "','"
                    + student.getAvgGrade() + "')";

            //insert into students(name, surename, avg_grade) values("Leo","Farrel",8.4)

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
