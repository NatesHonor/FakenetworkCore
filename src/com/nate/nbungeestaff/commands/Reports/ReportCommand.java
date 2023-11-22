package com.nate.nbungeestaff.commands.Reports;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.api.chat.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.nate.nbungeestaff.Core;

public class ReportCommand extends Command implements Listener {
    private final Core core = Core.getInstance();
    private final Map<UUID, Long> reportCooldowns = new HashMap<>();
    private final Map<UUID, UUID> handledReports = new HashMap<>();

    public ReportCommand() {
        super("report", null, "reportplayer");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "This command can only be used by players."));
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (args.length < 2) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /report <player> <reason>"));
            return;
        }

        ProxiedPlayer reportedPlayer = core.getProxy().getPlayer(args[0]);
        if (reportedPlayer == null) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Player not found."));
            return;
        }

        if (!canReport(player)) {
            sender.sendMessage(
                    new TextComponent(ChatColor.RED + "You can only use the report command every 30 seconds."));
            return;
        }

        String reason = String.join(" ", args);
        reason = reason.replaceFirst(args[0], "").trim();

        List<ProxiedPlayer> staffMembers = getStaffWithPermission("fakenetwork.reports");

        User reportingUser = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId());
        User reportedUser = LuckPermsProvider.get().getUserManager().getUser(reportedPlayer.getUniqueId());

        String reportingUserPrefix = "";
        String reportedUserPrefix = "";

        if (reportingUser != null) {
            String prefixNode = reportingUser.getCachedData().getMetaData().getPrefix();
            if (prefixNode != null) {
                reportingUserPrefix = ChatColor.translateAlternateColorCodes('&', prefixNode);
            }
        }

        if (reportedUser != null) {
            String prefixNode = reportedUser.getCachedData().getMetaData().getPrefix();
            if (prefixNode != null) {
                reportedUserPrefix = ChatColor.translateAlternateColorCodes('&', prefixNode);
            }
        }

        TextComponent reportMessage = new TextComponent(ChatColor.translateAlternateColorCodes('&',
                "&4&lReport | " + reportingUserPrefix + player.getName() + " &7reported " +
                        reportedUserPrefix + reportedPlayer.getName() + ChatColor.RED + " for " + reason +
                        " in &6" + reportedPlayer.getServer().getInfo().getName()));

        if (handledReports.containsKey(reportedPlayer.getUniqueId())) {
            UUID staffHandling = handledReports.get(reportedPlayer.getUniqueId());
            if (staffHandling.equals(player.getUniqueId())) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "You are already handling this report."));
            } else {
                sender.sendMessage(
                        new TextComponent(ChatColor.RED + "A staff member is already handling this report."));
            }
        } else {
            handledReports.put(reportedPlayer.getUniqueId(), player.getUniqueId());

            for (ProxiedPlayer staff : staffMembers) {
                staff.sendMessage(reportMessage);
            }

            storeReport(player.getName(), reportedPlayer.getName(), reason);

            TextComponent successMessage = new TextComponent(ChatColor.GREEN + "Thank you for reporting " +
                    ChatColor.RESET + reportedPlayer.getName() + ChatColor.GREEN
                    + ". Staff members have been notified.");
            sender.sendMessage(successMessage);
        }
    }

    private boolean canReport(ProxiedPlayer player) {
        UUID playerUUID = player.getUniqueId();
        if (reportCooldowns.containsKey(playerUUID)) {
            long lastReportTime = reportCooldowns.get(playerUUID);
            long currentTime = System.currentTimeMillis();
            long cooldownMillis = TimeUnit.SECONDS.toMillis(30);

            if (currentTime - lastReportTime < cooldownMillis) {
                return false;
            }
        }
        reportCooldowns.put(playerUUID, System.currentTimeMillis());
        return true;
    }

    private List<ProxiedPlayer> getStaffWithPermission(String permission) {
        List<ProxiedPlayer> staffMembers = new ArrayList<>();
        for (ProxiedPlayer player : core.getProxy().getPlayers()) {
            if (player.hasPermission(permission)) {
                staffMembers.add(player);
            }
        }
        return staffMembers;
    }

    private void storeReport(String reporterName, String reportedName, String reason) {
        try {
            PreparedStatement statement = core.getConnection().prepareStatement(
                    "INSERT INTO reports (reporter_name, reported_name, reason) VALUES (?, ?, ?)");
            statement.setString(1, reporterName);
            statement.setString(2, reportedName);
            statement.setString(3, reason);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (handledReports.containsKey(playerId)) {
            UUID reportedPlayerId = handledReports.get(playerId);

            ProxiedPlayer reportedPlayer = core.getProxy().getPlayer(reportedPlayerId);

            if (reportedPlayer != null) {
                event.setTarget(reportedPlayer.getServer().getInfo());
            }

            handledReports.remove(playerId);
        }
    }
}
