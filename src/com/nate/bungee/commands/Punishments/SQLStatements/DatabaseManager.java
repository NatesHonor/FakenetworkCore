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
        String query = "SELECT mute_time, unmute_time FROM punishments_mutes WHERE player_name = ? AND unmuted = 'no' ORDER BY unmute_time DESC LIMIT 1";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, playerName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String muteTime = rs.getString("mute_time");
                    String unmuteTime = rs.getString("unmute_time");
                    return muteTime + " to " + unmuteTime;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getBanDuration(String playerName) {
        String query = "SELECT ban_time, unban_time FROM bans WHERE player_name = ? AND unbanned = 'no' ORDER BY unban_time DESC LIMIT 1";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, playerName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String banTime = rs.getString("ban_time");
                    String unbanTime = rs.getString("unban_time");
                    return formatDuration(banTime, unbanTime);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String formatDuration(String startTime, String endTime) {
        SimpleDateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
        try {
            Date startDate = sqlFormat.parse(startTime);
            Date endDate = sqlFormat.parse(endTime);
            String formattedStartTime = displayFormat.format(startDate);
            String formattedEndTime = displayFormat.format(endDate);
            long durationMillis = endDate.getTime() - startDate.getTime();
            String formattedDuration = formatMillis(durationMillis);
            return "Ban Duration: " + formattedStartTime + " to " + formattedEndTime + " (" + formattedDuration + ")";
        } catch (ParseException e) {
            e.printStackTrace();
            return "Invalid date format";
        }
    }

    private String formatMillis(long millis) {
        long seconds = millis / 1000 % 60;
        long minutes = millis / (1000 * 60) % 60;
        long hours = millis / (1000 * 60 * 60) % 24;
        long days = millis / (1000 * 60 * 60 * 24);

        return (days > 0 ? days + " days, " : "") +
                (hours > 0 ? hours + " hours, " : "") +
                (minutes > 0 ? minutes + " minutes, " : "") +
                (seconds > 0 ? seconds + " seconds" : "").replaceAll(", $", "");
    }

}
