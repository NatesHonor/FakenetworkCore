package com.nate.bungee.commands.Punishments.Mutes;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import com.nate.bungee.commands.Punishments.SQLStatements.Mutes;

import net.md_5.bungee.api.ChatColor;

public class Unmute extends Command {

    public Unmute() {
        super("unmute", "fakenetwork.moderator");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission("fakenetwork.moderator")) {
            if (args.length != 1) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /unmute <player>"));
                return;
            }

            String playerName = args[0];
            Mutes mutes = new Mutes();

            if (!mutes.isPlayerMuted(playerName)) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "This player is not currently muted."));
                return;
            }

            mutes.setPlayerUnmuted(playerName);
            sender.sendMessage(new TextComponent(ChatColor.GREEN + "Player '" + playerName + "' has been unmuted."));
        }
    }
}
