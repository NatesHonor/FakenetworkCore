package com.nate.bungee.commands.Reports;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.nate.bungee.Core;

public class AcceptReportCommand extends Command {
    Core core = Core.getInstance();

    public AcceptReportCommand() {
        super("acceptreport");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "This command can only be used by players."));
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (!player.hasPermission("fakenetwork.reports.manage")) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "You don't have permission to use this command."));
            return;
        }

        if (args.length != 1) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /acceptreport <report_id>"));
            return;
        }

        int reportId;
        try {
            reportId = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Invalid report ID."));
            return;
        }

        try {
            PreparedStatement selectStatement = core.getConnection().prepareStatement(
                    "SELECT * FROM reports WHERE id = ?");
            selectStatement.setInt(1, reportId);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                String reporterName = resultSet.getString("reporter_name");
                String reportedName = resultSet.getString("reported_name");
                String reason = resultSet.getString("reason");

                PreparedStatement insertStatement = core.getConnection().prepareStatement(
                        "INSERT INTO accepted_reports (reporter_name, reported_name, reason) VALUES (?, ?, ?)");
                insertStatement.setString(1, reporterName);
                insertStatement.setString(2, reportedName);
                insertStatement.setString(3, reason);
                insertStatement.executeUpdate();
                insertStatement.close();

                PreparedStatement deleteStatement = core.getConnection().prepareStatement(
                        "DELETE FROM reports WHERE id = ?");
                deleteStatement.setInt(1, reportId);
                deleteStatement.executeUpdate();
                deleteStatement.close();

                sender.sendMessage(new TextComponent(
                        ChatColor.GREEN + "Report ID " + reportId + " has been accepted and removed."));
            } else {
                sender.sendMessage(new TextComponent(ChatColor.RED + "Report not found."));
            }

            resultSet.close();
            selectStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            sender.sendMessage(new TextComponent(ChatColor.RED + "An error occurred while accepting the report."));
        }
    }
}
