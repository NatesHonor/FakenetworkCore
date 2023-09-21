package com.nate.fakenetwork.commands.Punishments.SQLStatements;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Warns {
    private String databaseURL = "jdbc:mysql://localhost:3306/fakenetwork";
    private String databaseUsername = "root";
    private String databasePassword = "";

    public void createWarnsTable() {
        try (Connection connection = DriverManager.getConnection(databaseURL, databaseUsername, databasePassword);
                Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS punishments_warns ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "player_name VARCHAR(255),"
                    + "reason VARCHAR(255),"
                    + "timestamp TIMESTAMP"
                    + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
