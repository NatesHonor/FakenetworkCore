package com.nate.nbungeestaff.commands.Parties;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class PartyCommandExecutor extends Command implements TabExecutor {
    public PartyCommandExecutor() {
        super("p", null, "party");
    }

    private final List<String> subCommands = Arrays.asList("help", "invite", "leave", "list", "promote", "demote",
            "transfer", "home", "remove", "warp", "accept", "disband", "mute", "settings", "challenge", "poll",
            "kickoffline", "private", "pchat", "stream");

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);
        } else if (args[0].equalsIgnoreCase("invite") || args[0].equalsIgnoreCase("i")) {
            if (args.length < 2) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /party invite <player>"));
                return;
            }
            String invitedPlayerName = args[1];

        } else if (args[0].equalsIgnoreCase("leave")) {

        } else if (args[0].equalsIgnoreCase("list")) {

        } else if (args[0].equalsIgnoreCase("promote")) {
            if (args.length < 2) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /party promote <player>"));
                return;
            }
            String playerToPromote = args[1];

        } else if (args[0].equalsIgnoreCase("demote")) {
            if (args.length < 2) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /party demote <player>"));
                return;
            }
            String playerToDemote = args[1];

        } else if (args[0].equalsIgnoreCase("transfer")) {
            if (args.length < 2) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /party transfer <player>"));
                return;
            }
            String newLeader = args[1];

        } else if (args[0].equalsIgnoreCase("home")) {

        } else if (args[0].equalsIgnoreCase("remove")) {
            if (args.length < 2) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /party remove <player>"));
                return;
            }
            String playerToRemove = args[1];

        } else if (args[0].equalsIgnoreCase("warp")) {

        } else if (args[0].equalsIgnoreCase("accept")) {
            if (args.length < 2) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /party accept <player>"));
                return;
            }
            String inviter = args[1];

        } else if (args[0].equalsIgnoreCase("disband")) {

        } else if (args[0].equalsIgnoreCase("mute")) {

        } else if (args[0].equalsIgnoreCase("settings")) {
            if (args.length < 3) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /party settings <setting> <value>"));
                return;
            }
            String setting = args[1];
            String value = args[2];

        } else if (args[0].equalsIgnoreCase("challenge")) {
            if (args.length < 2) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /party challenge <player>"));
                return;
            }
            String playerToChallenge = args[1];

        } else if (args[0].equalsIgnoreCase("poll")) {
            if (args.length < 2) {
                sender.sendMessage(
                        new TextComponent(ChatColor.RED + "Usage: /party poll <Question/Answer/Answer/Answer/Answer>"));
                return;
            }
            String pollData = args[1];
            String[] pollOptions = pollData.split("/");
            if (pollOptions.length < 2 || pollOptions.length > 5) {
                sender.sendMessage(new TextComponent(
                        ChatColor.RED + "Invalid poll format. Use /party poll <Question/Answer/Answer/Answer/Answer>"));
                return;
            }

        } else if (args[0].equalsIgnoreCase("kickoffline") || args[0].equalsIgnoreCase("kickoff")) {

        } else if (args[0].equalsIgnoreCase("private")) {

        } else if (args[0].equalsIgnoreCase("pchat")) {
            if (!(sender instanceof ProxiedPlayer)) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "This command can only be executed by a player."));
                return;
            }
            ProxiedPlayer player = (ProxiedPlayer) sender;
            if (args.length < 2) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /pchat <player>"));
                return;
            }
            String targetPlayerName = args[1];

        } else if (args[0].equalsIgnoreCase("stream")) {
            if (!(sender instanceof ProxiedPlayer)) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "This command can only be executed by a player."));
                return;
            }
            ProxiedPlayer player = (ProxiedPlayer) sender;

        } else {
            sender.sendMessage(
                    new TextComponent(ChatColor.RED + "Unknown subcommand. Use /party help for a list of commands."));
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(new TextComponent(ChatColor.YELLOW + "Party Commands:"));
        for (String subCmd : subCommands) {
            sender.sendMessage(new TextComponent(
                    ChatColor.YELLOW + "/party " + subCmd + " - " + ChatColor.AQUA + getDescription(subCmd)));
        }
    }

    private String getDescription(String subCmd) {
        switch (subCmd) {
            case "help":
                return "Prints a list of party commands";
            case "invite":
            case "i":
                return "Invites the player to your party, creating one in the process if one is not already formed";
            case "leave":
                return "Leaves the current party";
            case "list":
                return "Lists the members of your party";
            case "promote":
                return "Promotes a party member to either party moderator or party leader";
            case "demote":
                return "Demotes a party moderator to a party member";
            case "transfer":
                return "Transfers the party to another player";
            case "home":
                return "Warps your party to your house";
            case "remove":
                return "Removes the player from your party";
            case "warp":
                return "Teleports the members of your party to the server you're in - This only works in pre-game lobbies and server lobbies";
            case "accept":
                return "Accepts a party invite from the player";
            case "disband":
                return "Disbands the party";
            case "mute":
                return "Mutes your party chat so only party leader, party moderator, and staff members can use it";
            case "settings":
                return "Modifies settings for your party";
            case "challenge":
                return "Challenges a party to a Cops and Crims duel";
            case "poll":
                return "Set up a party poll to let party members vote on. You can set up 4 answers at maximum using \"/\" to separate them. (MVP++ and above exclusive)";
            case "kickoffline":
                return "Kicks offline players from the party";
            case "private":
                return "Creates a private game for only the players in your party (YT, MVP++ and staff exclusive)";
            case "pchat":
                return "Sends a message in party chat";
            case "stream":
                return "Allows the party leader to experience an interactive streaming party where people can freely join. (MVP++ and YT exclusive)";
            default:
                return "Unknown";
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            for (String subCmd : subCommands) {
                if (subCmd.startsWith(args[0].toLowerCase())) {
                    completions.add(subCmd);
                }
            }
            return completions;
        }
        return Arrays.asList();
    }
}
