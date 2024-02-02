package com.nate.bungee.commands.Punishments.Bans;

import com.nate.bungee.Core;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.event.EventHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BanChecker extends Command implements Listener {

    public BanChecker() {
        super("isbanned", "fakenetwork.staff");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(new TextComponent("Usage: /isbanned <player>"));
            return;
        }

        String playerName = args[0];
        try (Connection conn = Core.getInstance().getConnection()) {
            String query = "SELECT reason, unban_time FROM punishments_bans WHERE name = ? AND is_active = 1";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, playerName);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String reason = rs.getString("reason");
                        Timestamp unbanTime = rs.getTimestamp("unban_time");
                        long duration = unbanTime.getTime() - System.currentTimeMillis();
                        String formattedDuration = formatDuration(duration);
                        sender.sendMessage(new TextComponent(playerName + " is currently banned. Reason: "
                                + reason + ". Time left: " + formattedDuration));
                    } else {
                        sender.sendMessage(new TextComponent(playerName + " is not currently banned."));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(new TextComponent("Error checking ban status."));
        }
    }

    @EventHandler
    public void onLogin(LoginEvent event) throws SQLException {
        UUID playerUUID = event.getConnection().getUniqueId();
    
        try (Connection conn = Core.getInstance().getConnection()) {
            String query = "SELECT reason, unban_time FROM punishments_bans WHERE uuid = ? AND is_active = 1";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, playerUUID.toString());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Timestamp unbanTime = rs.getTimestamp("unban_time");
                        if (unbanTime != null && unbanTime.toInstant().isBefore(Instant.now())) {
                            setBanInactive(conn, playerUUID.toString());
                        } else {
                            String reason = rs.getString("reason");
                            String banMessage;
                            if (unbanTime != null) {
                                long duration = unbanTime.getTime() - System.currentTimeMillis();
                                String formattedDuration = formatDuration(duration);
                                banMessage = "You are banned from this server. Reason: " + reason +
                                        ". Time left: " + formattedDuration;
                            } else {
                                banMessage = "You are banned from this server. Reason: " + reason;
                            }
                            event.setCancelled(true);
                            event.setCancelReason(new TextComponent(banMessage));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }    

    private void setBanInactive(Connection conn, String uuid) throws Exception {
        String updateQuery = "UPDATE punishments_bans SET is_active = 0 WHERE uuid = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setString(1, uuid);
            stmt.executeUpdate();
        }
    }

    private String formatDuration(long millis) {
        if (TimeUnit.MILLISECONDS.toDays(millis) > 0) {
            return TimeUnit.MILLISECONDS.toDays(millis) + "d";
        } else if (TimeUnit.MILLISECONDS.toHours(millis) > 0) {
            return TimeUnit.MILLISECONDS.toHours(millis) + "h";
        } else if (TimeUnit.MILLISECONDS.toMinutes(millis) > 0) {
            return TimeUnit.MILLISECONDS.toMinutes(millis) + "m";
        } else {
            return "Less than a minute";
        }
    }
}
