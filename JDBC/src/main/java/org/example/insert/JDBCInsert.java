package org.example.insert;

import org.example.entity.Student;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JDBCInsert {

    static final String DB_URL = "jdbc:postgresql://localhost:5432/test_db";

    static final String USER = "postgres";

    static final String PASSWORD = "8801243";


    public static void main(String[] args) {

        Connection connection = null;

        Student student = new Student("Ivanov","Ivan",5.5);

        try {
            connection = DriverManager.getConnection(DB_URL,USER, PASSWORD);

            PreparedStatement statement = connection.prepareStatement("insert into students(name, surename, avg_grade) values(?,?,?)");

            statement.setString(1, student.getName());
            statement.setString(2, student.getSurname());
            statement.setDouble(3,student.getAvgGrade());

            statement.executeUpdate();

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
