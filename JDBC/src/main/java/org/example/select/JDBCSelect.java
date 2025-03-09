package org.example.select;

import org.example.entity.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCSelect {

    static final String DB_URL = "jdbc:postgresql://localhost:5432/test_db";

    static final String USER = "postgres";

    static final String PASSWORD = "8801243";


    public static void main(String[] args) {

        Connection connection = null;



        try {
            connection = DriverManager.getConnection(DB_URL,USER, PASSWORD);

            PreparedStatement statement = connection.prepareStatement("select * from students WHERE avg_grade > ?");
            statement.setDouble(1, 4.0);

            ResultSet resultSet = statement.executeQuery();

            List<Student> students = new ArrayList<>();

            while (resultSet.next()) {
                Student student = new Student();
                student.setId(resultSet.getLong("id"));
                student.setName(resultSet.getString("name"));
                student.setSurname(resultSet.getString("surename"));
                student.setAvgGrade(resultSet.getDouble("avg_grade"));
                students.add(student);
            }

            System.out.println(students);

            statement.close();

            resultSet.close();

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
