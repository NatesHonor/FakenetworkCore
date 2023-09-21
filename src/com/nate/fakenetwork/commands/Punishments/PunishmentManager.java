package com.nate.fakenetwork.commands.Punishments;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import com.nate.fakenetwork.Core;

public class PunishmentManager implements Listener {

    private String databaseURL = "jdbc:mysql://localhost:3306/fakenetwork";
    private String databaseUsername = "root";
    private String databasePassword = "";

    private Map<String, MuteInfo> mutedPlayers = new HashMap<>();

    public PunishmentManager() {
        Core.getInstance().getProxy().getPluginManager().registerListener(Core.getInstance(), this);
        createTables();
        loadMutedPlayers();
    }

    private void createTables() {
        try (Connection connection = DriverManager.getConnection(databaseURL, databaseUsername, databasePassword);
                Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS punishments_mutes ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "player_name VARCHAR(255),"
                    + "reason VARCHAR(255),"
                    + "mute_time TIMESTAMP,"
                    + "unmute_time TIMESTAMP"
                    + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void applyMute(String playerName, String reason, int durationInDays) {
        try (Connection connection = DriverManager.getConnection(databaseURL, databaseUsername, databasePassword)) {
            String insertQuery = "INSERT INTO punishments_mutes (player_name, reason, mute_time, unmute_time) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, playerName);
                preparedStatement.setString(2, reason);

                long currentTime = System.currentTimeMillis();
                long unmuteTime = currentTime + durationInDays * 24L * 60 * 60 * 1000;

                preparedStatement.setTimestamp(3, new java.sql.Timestamp(currentTime));
                preparedStatement.setTimestamp(4, new java.sql.Timestamp(unmuteTime));

                preparedStatement.executeUpdate();

                mutedPlayers.put(playerName, new MuteInfo(reason, unmuteTime));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadMutedPlayers() {
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

                        mutedPlayers.put(playerName, new MuteInfo(reason, unmuteTime));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onJoin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        String playerName = player.getName();
        if (isPlayerMuted(playerName)) {
            MuteInfo muteInfo = mutedPlayers.get(playerName);
            long remainingTime = muteInfo.unmuteTime - System.currentTimeMillis();

            if (!muteInfo.muteMessageSent) {
                String muteMessage = String.format(
                        "You have been muted for %s for %s. If your time has expired, please rejoin. You can also appeal at (sample appeal url)",
                        formatDuration(remainingTime),
                        muteInfo.reason);
                player.sendMessage(new TextComponent(muteMessage));
                muteInfo.muteMessageSent = true;
            }
        }
    }

    @EventHandler
    public void onChat(ChatEvent e) {
        ProxiedPlayer player = (ProxiedPlayer) e.getSender();
        String playerName = player.getName();
        if (isPlayerMuted(playerName)) {
            MuteInfo muteInfo = mutedPlayers.get(playerName);
            long remainingTime = muteInfo.unmuteTime - System.currentTimeMillis();

            if (!muteInfo.muteMessageSent) {
                String muteMessage = String.format(
                        "You have been muted for %s for %s. If your time has expired, please rejoin. You can also appeal at (sample appeal url)",
                        formatDuration(remainingTime),
                        muteInfo.reason);
                player.sendMessage(new TextComponent(muteMessage));
                muteInfo.muteMessageSent = true;
            }
            if (!e.getMessage().startsWith("/")) {
                e.setCancelled(true);
            }
        }
    }

    private boolean isPlayerMuted(String playerName) {
        MuteInfo muteInfo = mutedPlayers.get(playerName);
        if (muteInfo == null) {
            return false;
        }

        if (System.currentTimeMillis() >= muteInfo.unmuteTime) {
            mutedPlayers.remove(playerName);
            return false;
        }

        return true;
    }

    private String formatDuration(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        return String.format("%d days, %d hours, %d minutes", days, hours % 24, minutes % 60);
    }

    private static class MuteInfo {
        String reason;
        long unmuteTime;
        boolean muteMessageSent = false;

        MuteInfo(String reason, long unmuteTime) {
            this.reason = reason;
            this.unmuteTime = unmuteTime;
        }
    }
}
