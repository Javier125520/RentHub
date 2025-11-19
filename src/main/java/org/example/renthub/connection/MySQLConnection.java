package org.example.renthub.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {
    private static Connection connection;

    private static final String URL= "jdbc:mysql://localhost:3306/renthub";
    private static final String USER= "root";
    private static final String PASSWORD= "";

    private MySQLConnection() {}

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return connection;
    }
}
