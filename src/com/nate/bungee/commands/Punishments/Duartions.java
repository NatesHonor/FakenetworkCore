package com.nate.bungee.commands.Punishments;

import com.nate.bungee.commands.Punishments.SQLStatements.DatabaseManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class Duartions extends Command {

    private DatabaseManager databaseManager;

    public Duartions() {
        super("duration", "fakenetwork.duration", "punishmenttime", "ptime");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(new TextComponent("§cUsage: /duration <player>"));
            return;
        }
        String playerName = args[0];
        displayPunishmentDurations(sender, playerName);
    }

    private void displayPunishmentDurations(CommandSender sender, String playerName) {
        String muteDuration = databaseManager.getMuteDuration(playerName);
        String banDuration = databaseManager.getBanDuration(playerName);
        sender.sendMessage(new TextComponent("§aPunishment Time for " + playerName + ":"));
        sender.sendMessage(
                new TextComponent("§cMute: " + (muteDuration == null ? "Not currently muted" : muteDuration)));
        sender.sendMessage(new TextComponent("§cBan: " + (banDuration == null ? "Not currently banned" : banDuration)));
    }
}