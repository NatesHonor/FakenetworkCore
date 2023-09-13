package com.nate.fakenetwork.commands.Reports;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.plugin.Command;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.nate.fakenetwork.Core;

public class ReportCommand extends Command {
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

    String reason = String.join(" ", args);
    reason = reason.replaceFirst(args[0], "").trim();

    List<ProxiedPlayer> staffMembers = getStaffWithPermission("fakenetwork.reports");

    User reportingUser = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId());
    User reportedUser = LuckPermsProvider.get().getUserManager().getUser(reportedPlayer.getUniqueId());

    String reportingUserPrefix = "";
    String reportedUserPrefix = "";

    if (reportingUser != null) {
        for (Node node : reportingUser.getNodes()) {
            if (node.getKey().startsWith("prefix.")) {
                reportingUserPrefix = ChatColor.translateAlternateColorCodes('&', String.valueOf(node.getValue()));
                break;
            }
        }
    }

    if (reportedUser != null) {
        for (Node node : reportedUser.getNodes()) {
            if (node.getKey().startsWith("prefix.")) {
                reportedUserPrefix = ChatColor.translateAlternateColorCodes('&', String.valueOf(node.getValue()));
                break;
            }
        }
    }

    TextComponent reportMessage = new TextComponent(ChatColor.translateAlternateColorCodes('&',
            "&4&lReport | " + reportingUserPrefix + player.getName() + " &7reported " +
                    reportedUserPrefix + reportedPlayer.getName() + ChatColor.RED + " for " + reason +
                    " in &6server"));

    handledReports.put(reportedPlayer.getUniqueId(), player.getUniqueId());

    for (ProxiedPlayer staff : staffMembers) {
        TextComponent clickableReport = new TextComponent(reportMessage);
        TextComponent teleportMessage = new TextComponent(ChatColor.GREEN + " [Click to Join]");
        teleportMessage.setClickEvent(new ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                "/server " + reportedPlayer.getServer().getInfo().getName()));

        TextComponent hoverContent = new TextComponent(ChatColor.YELLOW + "Click to join server");

        HoverEvent hoverEvent = new HoverEvent(
            HoverEvent.Action.SHOW_TEXT,
            new Text[] { new Text(hoverContent.toPlainText()) }
        );

        teleportMessage.setHoverEvent(hoverEvent);

        clickableReport.addExtra(teleportMessage);

        staff.sendMessage(clickableReport);
    }

    storeReport(player.getName(), reportedPlayer.getName(), reason);

    TextComponent successMessage = new TextComponent(ChatColor.GREEN + "Thank you for reporting " +
            ChatColor.RESET + reportedPlayer.getName() + ChatColor.GREEN
            + ". Staff members have been notified.");
    sender.sendMessage(successMessage);
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
}
