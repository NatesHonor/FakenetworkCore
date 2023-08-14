package com.nate.hypixel.commands.Guilds;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuildCommandExecutor extends Command implements TabExecutor {
    private final List<String> subCommands = Arrays.asList(
            "help", "create", "oc", "members", "member", "promote", "invite", "accept", "demote",
            "transfer", "disband", "rename", "notifications", "kick", "leave", "info", "settings",
            "join", "shop", "party", "tag", "member", "chat", "motd", "online", "tagcolor");

    public GuildCommandExecutor() {
        super("guild", null, "g");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(new TextComponent(ChatColor.YELLOW + "Guild Commands:"));
            for (String subCmd : subCommands) {
                sender.sendMessage(new TextComponent(
                        ChatColor.YELLOW + "/guild " + subCmd + " - " + ChatColor.AQUA + getDescription(subCmd)));
            }
            return;
        }

        sender.sendMessage(new TextComponent("Unknown command. Use /guild help for a list of commands."));
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> tabComplete = new ArrayList<>();

        if (args.length == 1) {
            for (String subCmd : subCommands) {
                if (subCmd.startsWith(args[0].toLowerCase())) {
                    tabComplete.add(subCmd);
                }
            }
        }

        return tabComplete;
    }

    private String getDescription(String subCmd) {
        switch (subCmd.toLowerCase()) {
            case "help":
                return "Lists all Guild commands";
            case "create":
                return "Creates a Guild with the specified name. (Only VIP+ can create guilds)";
            case "oc":
                return "Access the officer chat (Must have access to the officer chat to do this)";
            case "members":
                return "Lists the players in your Guild";
            case "member":
                return "Displays information about the Guild member";
            case "promote":
                return "Promotes the player to the rank 1 priority above.";
            case "invite":
                return "Invites the player to your Guild";
            case "accept":
                return "Accepts a Guild invitation";
            case "demote":
                return "Demotes the player to the rank 1 priority lower.";
            case "transfer":
                return "Transfer ownership of the Guild to another player. (Note that the player must have VIP+ or higher)";
            case "disband":
                return "Disbands the Guild";
            case "rename":
                return "Renames the Guild";
            case "notifications":
                return "Toggle Guild join/leave notifications";
            case "kick":
                return "Kicks the player from your Guild";
            case "leave":
                return "Leaves your current Guild";
            case "info":
                return "Prints information about your Guild";
            case "settings":
                return "Modify settings for your Guild";
            case "join":
                return "Request to join the specified Guild";
            case "shop":
                return "Open the Guild Shop (Note that this feature is outdated, and now everyone has the Guild Features for free.)";
            case "party":
                return "Forms a party from your online Guild Members";
            case "tag":
                return "Sets the Guild [TAG]";
            case "chat":
                return "Switches to Guild chat";
            case "motd":
                return "Modifies the MOTD for the Guild";
            case "online":
                return "List all online Guild members";
            case "tagcolor":
                return "Sets the Guild tag color";
            default:
                return "Unknown command";
        }
    }
}
