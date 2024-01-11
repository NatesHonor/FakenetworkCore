package com.nate.bungee.commands.Punishments.SQLStatements;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.nate.bungee.commands.Punishments.PunishmentManager;
import com.nate.bungee.utils.Functions.DatabaseConfig;
import com.zaxxer.hikari.HikariDataSource;

public class Mutes {
    private static HikariDataSource dataSource;
    static PunishmentManager punishmentManager = new PunishmentManager();

    public Mutes() {
        Mutes.dataSource = DatabaseConfig.getDataSource();
    }

    public void applyMute(String playerName, String reason, int durationInDays) {
        try (Connection connection = dataSource.getConnection()) {
            String insertQuery = "INSERT INTO punishments_mutes (player_name, reason, mute_time, unmute_time) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, playerName);
                preparedStatement.setString(2, reason);

                long currentTime = System.currentTimeMillis();
                long unmuteTime = currentTime + durationInDays * 24L * 60 * 60 * 1000;

                preparedStatement.setTimestamp(3, new java.sql.Timestamp(currentTime));
                preparedStatement.setTimestamp(4, new java.sql.Timestamp(unmuteTime));

                preparedStatement.executeUpdate();

                punishmentManager.putMap(playerName, reason, unmuteTime);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isPlayerMuted(String playerName) {
        try (Connection connection = Mutes.dataSource.getConnection()) {
            String selectQuery = "SELECT 1 FROM punishments_mutes WHERE player_name = ? AND unmute_time > ? AND (unmuted IS NULL OR unmuted = 'no')";
            long currentTime = System.currentTimeMillis();
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setString(1, playerName);
                preparedStatement.setTimestamp(2, new java.sql.Timestamp(currentTime));
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setPlayerUnmuted(String playerName) {
        try (Connection connection = dataSource.getConnection()) {
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

    public void loadMutedPlayers(PunishmentManager punishmentManager) {
        try (Connection connection = dataSource.getConnection()) {
            String selectQuery = "SELECT player_name, reason, unmute_time FROM punishments_mutes WHERE unmute_time > ?";
            long currentTime = System.currentTimeMillis();
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setTimestamp(1, new java.sql.Timestamp(currentTime));

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String playerName = resultSet.getString("player_name");
                        String reason = resultSet.getString("reason");
                        long unmuteTime = resultSet.getTimestamp("unmute_time").getTime();

                        punishmentManager.putMap(playerName, reason, unmuteTime);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getOffenseCount(String playerName, String reason) {
        int count = 0;
        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT COUNT(*) FROM punishments_mutes WHERE player_name = ? AND reason = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, playerName);
                preparedStatement.setString(2, reason);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        count = resultSet.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
}
