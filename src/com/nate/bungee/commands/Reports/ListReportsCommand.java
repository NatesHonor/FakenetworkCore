package com.nate.bungee.commands.Reports;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.nate.bungee.Core;

public class ListReportsCommand extends Command {
    Core core = Core.getInstance();

    public ListReportsCommand() {
        super("listreports", null, "reportslist");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "This command can only be used by players."));
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (!player.hasPermission("fakenetwork.reports")) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "You don't have permission to use this command."));
            return;
        }

        try {
            PreparedStatement statement = core.getConnection().prepareStatement(
                    "SELECT id, reporter_name, reported_name, reason FROM reports");
            ResultSet resultSet = statement.executeQuery();

            TextComponent reportsMessage = new TextComponent(ChatColor.YELLOW + "Reports:\n");

            boolean foundReports = false;

            while (resultSet.next()) {
                int reportId = resultSet.getInt("id");
                String reporterName = resultSet.getString("reporter_name");
                String reportedPlayer = resultSet.getString("reported_name");
                String reason = resultSet.getString("reason");

                TextComponent reportEntry = new TextComponent(
                        ChatColor.GRAY + "------------------------\n" +
                                ChatColor.YELLOW + "Report ID: " + ChatColor.GOLD + reportId + "\n" +
                                ChatColor.YELLOW + "Reporter: " + ChatColor.GOLD + reporterName + "\n" +
                                ChatColor.YELLOW + "Reported Player: " + ChatColor.GOLD + reportedPlayer + "\n" +
                                ChatColor.YELLOW + "Reason: " + ChatColor.GOLD + reason + "\n" +
                                ChatColor.GRAY + "------------------------\n");

                reportsMessage.addExtra(reportEntry);
                foundReports = true;
            }

            if (foundReports) {
                player.sendMessage(reportsMessage);
            } else {
                player.sendMessage(new TextComponent(ChatColor.YELLOW + "No reports found."));
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
