package com.nate.hypixel.commands;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.nate.hypixel.Core;
import com.nate.hypixel.GetRank;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class LinkCommand extends Command {
    String jdbcUrl = "jdbc:mysql://localhost:3306/link?useSSL=false&serverTimezone=UTC";
    String username = "root";
    String password = "";

    GetRank getRank = new GetRank(Core.getInstance());

    Connection connection = null;
    PreparedStatement statement = null;
    ResultSet resultSet = null;

    public LinkCommand(Core plugin) {
        super("link");
    }

    @SuppressWarnings("deprecation")
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /link <token>");
            return;
        }

        String token = args[0];

        try {
            String discordUserId = getDiscordUserIdFromToken(token);

            if (discordUserId != null) {
                String rank = getHypixelRank(sender.getName());
                String minecraftUsername = sender.getName();
                String playtime = getPlaytime(minecraftUsername);

                storeLinkInfo(discordUserId, minecraftUsername, rank, playtime);

                sender.sendMessage(ChatColor.GREEN + "Successfully linked your account to your Discord profile!");
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid token. Please try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sender.sendMessage(
                    ChatColor.RED + "An error occurred while linking your accounts. Please contact an administrator.");
        }
    }

    private String getDiscordUserIdFromToken(String token) throws SQLException {
        try {
            connection = DriverManager.getConnection(jdbcUrl, username, password);

            String sql = "SELECT discord_user_id FROM tokens WHERE token = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, token);

            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("discord_user_id");
            } else {
                return null;
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    private String getHypixelRank(String player) {
        return getRank.getRank(player);
    }

    private String getPlaytime(String minecraftUsername) {
        return "10 hours";
    }

    private void storeLinkInfo(String discordUserId, String minecraftUsername, String rank, String playtime)
            throws SQLException {
        try {
            connection = DriverManager.getConnection(jdbcUrl, username, password);

            String sql = "INSERT INTO account_links (discord_user_id, minecraft_username, `rank`, playtime) VALUES (?, ?, ?, ?)";
            statement = connection.prepareStatement(sql);
            statement.setString(1, discordUserId);
            statement.setString(2, minecraftUsername);
            statement.setString(3, rank);
            statement.setString(4, playtime);

            statement.executeUpdate();
        } finally {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }
}
