package org.example.delete;

import java.sql.*;

public class JDBCDelete {

    static final String DB_URL = "jdbc:postgresql://localhost:5432/test_db";

    static final String USER = "postgres";

    static final String PASSWORD = "8801243";


    public static void main(String[] args) {

        Connection connection = null;



        try {
            connection = DriverManager.getConnection(DB_URL,USER, PASSWORD);

            PreparedStatement statement = connection.prepareStatement(
                    "delete from students Where name=?"
            );
            statement.setString(1, "Ivanov");

            int deletedRows = statement.executeUpdate();

            System.out.println("Deleted " + deletedRows + " rows");

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
