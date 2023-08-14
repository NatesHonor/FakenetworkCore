package com.nate.hypixel.commands.Reports;

import com.nate.hypixel.Core;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
                    "SELECT reporter_uuid, reported_player, reason FROM reports");
            ResultSet resultSet = statement.executeQuery();

            TextComponent reportsMessage = new TextComponent(ChatColor.YELLOW + "Reports:\n");
            while (resultSet.next()) {
                String reporterUUID = resultSet.getString("reporter_uuid");
                String reportedPlayer = resultSet.getString("reported_player");
                String reason = resultSet.getString("reason");

                TextComponent reportEntry = new TextComponent(
                        ChatColor.YELLOW + "- Reporter: " + ChatColor.GOLD + reporterUUID + "\n" +
                                ChatColor.YELLOW + "  Reported Player: " + ChatColor.GOLD + reportedPlayer + "\n" +
                                ChatColor.YELLOW + "  Reason: " + ChatColor.GOLD + reason + "\n");

                reportsMessage.addExtra(reportEntry);
            }

            player.sendMessage(reportsMessage);

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
