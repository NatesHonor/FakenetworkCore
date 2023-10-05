package com.nate.fakenetwork.commands.Punishments.Warns;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.nate.fakenetwork.commands.Punishments.PunishmentManager;

public class Mutes {
    private String databaseURL = "jdbc:mysql://localhost:3306/fakenetwork";
    private String databaseUsername = "root";
    private String databasePassword = "";

    public boolean isPlayerUnmuted(String playerName) {
        try (Connection connection = DriverManager.getConnection(databaseURL, databaseUsername, databasePassword)) {
            String selectQuery = "SELECT unmuted FROM punishments_mutes WHERE player_name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setString(1, playerName);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String unmutedStatus = resultSet.getString("unmuted");
                        return "yes".equalsIgnoreCase(unmutedStatus);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setPlayerUnmuted(String playerName) {
        try (Connection connection = DriverManager.getConnection(databaseURL, databaseUsername, databasePassword)) {
            String updateQuery = "UPDATE punishments_mutes SET unmuted = ? WHERE player_name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setString(1, "yes");
                preparedStatement.setString(2, playerName);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadMutedPlayers() {
        try (Connection connection = DriverManager.getConnection(databaseURL, databaseUsername, databasePassword)) {
            String selectQuery = "SELECT player_name, reason, unmute_time FROM punishments_mutes WHERE unmute_time > ?";
            long currentTime = System.currentTimeMillis();
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setTimestamp(1, new java.sql.Timestamp(currentTime));

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String playerName = resultSet.getString("player_name");
                        String reason = resultSet.getString("reason");
                        long unmuteTime = resultSet.getTimestamp("unmute_time").getTime();
                        PunishmentManager punishmentManager = new PunishmentManager();
                        punishmentManager.putMap(playerName, reason, unmuteTime);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
