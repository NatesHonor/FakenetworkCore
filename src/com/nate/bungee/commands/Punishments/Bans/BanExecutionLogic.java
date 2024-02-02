package com.nate.bungee.commands.Punishments.Bans;

import com.nate.bungee.Core;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

public class BanExecutionLogic {

    public void executeBan(String uuid, String name, String reason, String bannedBy) {
        try (Connection conn = Core.getInstance().getConnection()) {
            int previousBansCount = getPreviousBansCount(conn, uuid, reason);
            String duration = calculateDuration(reason, previousBansCount);
            insertBanRecord(conn, uuid, name, reason, bannedBy, duration);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String calculateDuration(String baseDuration, int previousBansCount) {
        long durationMillis = parseDurationToMillis(baseDuration);
        long totalDurationMillis = durationMillis * (previousBansCount + 1);
        return formatDurationFromMillis(totalDurationMillis);
    }

    private long parseDurationToMillis(String duration) {
        String[] parts = duration.split(" ");
        long time = Long.parseLong(parts[0]);
        String unit = parts[1].toLowerCase();

        switch (unit) {
            case "days":
                return TimeUnit.DAYS.toMillis(time);
            case "hours":
                return TimeUnit.HOURS.toMillis(time);
            case "minutes":
                return TimeUnit.MINUTES.toMillis(time);
            default:
                throw new IllegalArgumentException("Unsupported time unit: " + unit);
        }
    }

    private String formatDurationFromMillis(long millis) {
        if (TimeUnit.MILLISECONDS.toDays(millis) > 0) {
            return TimeUnit.MILLISECONDS.toDays(millis) + " days";
        } else if (TimeUnit.MILLISECONDS.toHours(millis) > 0) {
            return TimeUnit.MILLISECONDS.toHours(millis) + " hours";
        } else {
            return TimeUnit.MILLISECONDS.toMinutes(millis) + " minutes";
        }
    }

    private Timestamp calculateUnbanTime(String duration) {
        long durationMillis = parseDurationToMillis(duration);
        long unbanTimeMillis = System.currentTimeMillis() + durationMillis;
        return new Timestamp(unbanTimeMillis);
    }

    private int getPreviousBansCount(Connection conn, String uuid, String reason) throws SQLException {
        String query = "SELECT COUNT(*) FROM punishments_bans WHERE uuid = ? AND reason = ? AND is_active = 1";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, uuid);
            stmt.setString(2, reason);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    private void insertBanRecord(Connection conn, String uuid, String name, String reason, String bannedBy,
            String duration) throws SQLException {
        String query = "INSERT INTO punishments_bans (uuid, name, reason, banned_by, ban_time, unban_time, is_active) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, uuid);
            stmt.setString(2, name);
            stmt.setString(3, reason);
            stmt.setString(4, bannedBy);
            stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            stmt.setTimestamp(6, calculateUnbanTime(duration));
            stmt.setBoolean(7, true);
            stmt.executeUpdate();
        }
    }
}
