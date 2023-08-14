package com.nate.hypixel.commands;

import java.util.UUID;

import com.nate.hypixel.Core;
import com.nate.hypixel.utils.storage.mysql.Levels;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class LevelsCommand {

    private static final int MAX_LEVEL = 500;
    private static final double EXP_BASE = 100.0;

    Levels levels = new Levels();

    Core core = Core.getInstance();

    private int calculateExpRequired(int level) {
        if (level > MAX_LEVEL) {
            return -1;
        }
        return (int) (EXP_BASE * Math.pow(2.0, level / 100.0));
    }

    public class LevelSetCommand extends Command {
        public LevelSetCommand() {
            super("levelset", null, "setlevel");
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            if (args.length != 2) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /levelset <player> <level>"));
                return;
            }

            String playerName = args[0];
            int level;
            try {
                level = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "Invalid level. Please enter a valid number."));
                return;
            }

            int expRequired = calculateExpRequired(level);
            if (expRequired == -1) {
                sender.sendMessage(new TextComponent(
                        ChatColor.RED + "Level cannot exceed the maximum level of " + MAX_LEVEL + "."));
                return;
            }

            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
            if (player == null) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "Player " + playerName + " not found."));
                return;
            }

            levels.savePlayerLevel(player.getUniqueId(), level, 0);

            sender.sendMessage(new TextComponent(
                    ChatColor.GREEN + "Successfully set " + playerName + "'s level to " + level + "."));
        }
    }

    public class LevelExpCommand extends Command {
        public LevelExpCommand() {
            super("levelexp", null, "addexp");
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            if (args.length != 2) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /levelexp <player> <exp>"));
                return;
            }

            String playerName = args[0];
            int exp;
            try {
                exp = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "Invalid EXP. Please enter a valid number."));
                return;
            }

            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
            if (player == null) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "Player " + playerName + " not found."));
                return;
            }

            UUID playerId = player.getUniqueId();
            int currentLevel = levels.getLevel(playerId);
            int currentExp = levels.getPlayerExp(playerId);

            int expRequired = calculateExpRequired(currentLevel);
            if (expRequired == -1) {
                sender.sendMessage(new TextComponent(
                        ChatColor.RED + "Level cannot exceed the maximum level of " + MAX_LEVEL + "."));
                return;
            }

            currentExp += exp;
            while (currentExp >= expRequired) {
                currentExp -= expRequired;
                currentLevel++;
                expRequired = calculateExpRequired(currentLevel);
                if (expRequired == -1) {
                    break;
                }
            }

            levels.savePlayerLevel(playerId, currentLevel, currentExp);

            sender.sendMessage(
                    new TextComponent(ChatColor.GREEN + "Successfully added " + exp + " EXP to " + playerName + "."));
        }
    }

}
