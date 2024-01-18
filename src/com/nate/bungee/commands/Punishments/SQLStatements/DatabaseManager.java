package com.nate.bungee.commands.Punishments.SQLStatements;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.sql.DataSource;

public class DatabaseManager {
    private DataSource dataSource;

    public DatabaseManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getMuteDuration(String playerName) {
        String query = "SELECT mute_time, unmute_time, reason FROM punishments_mutes WHERE player_name = ? AND unmuted = 'no' ORDER BY unmute_time DESC LIMIT 1";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, playerName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String muteTime = rs.getString("mute_time");
                    String unmuteTime = rs.getString("unmute_time");
                    String reason = rs.getString("reason");
                    SimpleDateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date muteDate = sqlFormat.parse(muteTime);
                    Date unmuteDate = sqlFormat.parse(unmuteTime);
                    long durationMillis = unmuteDate.getTime() - System.currentTimeMillis();
                    if (durationMillis < 0) {
                        return "§a§lNot currently muted";
                    }
                    String formattedDuration = formatMillis(durationMillis);
                    SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
                    return String.format(
                            "§c§lYou were muted on §e§l%s §c§lfor the reason: §e§l%s§c§l. It expires in §e§l%s",
                            displayFormat.format(muteDate),
                            reason,
                            formattedDuration);
                }
            }
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getBanDuration(String playerName) {
        String query = "SELECT ban_time, unban_time, reason FROM bans WHERE name = ? AND unbanned = 'no' ORDER BY unban_time DESC LIMIT 1";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, playerName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String banTime = rs.getString("ban_time");
                    String unbanTime = rs.getString("unban_time");
                    String reason = rs.getString("reason");
                    SimpleDateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date banDate = sqlFormat.parse(banTime);
                    Date unbanDate = sqlFormat.parse(unbanTime);
                    long durationMillis = unbanDate.getTime() - System.currentTimeMillis();
                    if (durationMillis < 0) {
                        return "§a§lNot currently banned";
                    }
                    String formattedDuration = formatMillis(durationMillis);
                    SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
                    return String.format(
                            "§c§lYou were banned on §e§l%s §c§lfor the reason: §e§l%s§c§l. It expires in §e§l%s",
                            displayFormat.format(banDate),
                            reason,
                            formattedDuration);
                }
            }
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String formatMillis(long millis) {
        long seconds = millis / 1000 % 60;
        long minutes = millis / (1000 * 60) % 60;
        long hours = millis / (1000 * 60 * 60) % 24;
        long days = millis / (1000 * 60 * 60 * 24);
    
        return (days > 0 ? "§e§l" + days + " days, " : "") +
                (hours > 0 ? "§e§l" + hours + " hours, " : "") +
                (minutes > 0 ? "§e§l" + minutes + " minutes, " : "") +
                (seconds > 0 ? "§e§l" + seconds + " seconds" : "").replaceAll(", $", "");
    }

}
