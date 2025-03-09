package org.example.insert;

import org.example.entity.Student;

import java.sql.*;

public class JDBCInsert_v3 {

    static final String DB_URL = "jdbc:postgresql://localhost:5432/test_db";

    static final String USER = "postgres";

    static final String PASSWORD = "8801243";


    public static void main(String[] args) {

        Connection connection = null;

        Student student = new Student("Sidorov","Kirill",9.5);

        try {
            connection = DriverManager.getConnection(DB_URL,USER, PASSWORD);

            PreparedStatement statement = connection.prepareStatement(
                    "insert into students(name, surename, avg_grade) values(?,?,?)"
                        ,Statement.RETURN_GENERATED_KEYS
                        );

            statement.setString(1, student.getName());
            statement.setString(2, student.getSurname());
            statement.setDouble(3,student.getAvgGrade());

            int uffectedRows = statement.executeUpdate();

            if (uffectedRows == 0) {
                throw new SQLException("Failed to insert student");
            }

            ResultSet generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next()) {
               student.setId(generatedKeys.getLong(1));
            }
            else
                throw new SQLException("Failed to create id");

            System.out.println(student);

            generatedKeys.close();

            statement.close();

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