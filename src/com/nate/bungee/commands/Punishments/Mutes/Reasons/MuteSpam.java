package com.nate.bungee.commands.Punishments.Mutes.Reasons;

import com.nate.bungee.commands.Punishments.SQLStatements.Mutes;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class MuteSpam extends Command {

    public MuteSpam() {
        super("mspam");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer && sender.hasPermission("fakenetwork.staff")) {
            if (args.length == 0) {
                sender.sendMessage(new TextComponent("Please specify a player you want to mute."));
                return;
            }

            String targetPlayerName = args[0];
            Mutes mutes = new Mutes();

            if (mutes.isPlayerMuted(targetPlayerName)) {
                sender.sendMessage(new TextComponent("This player is already muted."));
                return;
            }

            int offenseCount = mutes.getOffenseCount(targetPlayerName, "Spamming");
            String reason = "Spamming" + (offenseCount > 0 ? " (" + (offenseCount + 1) + ")" : "");
            int durationInHours = (offenseCount + 1) * 12;

            mutes.applyMute(targetPlayerName, reason, durationInHours);

            ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(targetPlayerName);
            if (targetPlayer != null) {
                targetPlayer.sendMessage(
                        new TextComponent("You have been muted for " + reason + " for " + durationInHours + " hour(s)."));
                if (offenseCount > 0) {
                    targetPlayer.sendMessage(new TextComponent("Since this is offense # " + (offenseCount + 1)
                            + ", your mute has been extended by " + offenseCount * 12 + " hour(s)."));
                }
            }

            for (ProxiedPlayer staffMember : ProxyServer.getInstance().getPlayers()) {
                if (staffMember.hasPermission("fakenetwork.staff")) {
                    staffMember.sendMessage(new TextComponent(
                            "§b[Mutes] §e" + targetPlayerName + " §ahas just been muted for §e"
                                    + formatDurationForDisplay(durationInHours * 60L * 60 * 1000) + " §afor §e"
                                    + reason));
                }
            }

            sender.sendMessage(
                    new TextComponent("Player '" + targetPlayerName + "' has been muted for " + reason + " for "
                            + durationInHours + " hour(s)."));
        } else {
            sender.sendMessage(new TextComponent("This command can only be executed by a player."));
        }
    }

    private String formatDurationForDisplay(long durationMillis) {
        if (durationMillis <= 0) {
            return "instantly";
        }

        long seconds = durationMillis / 1000 % 60;
        long minutes = durationMillis / (1000 * 60) % 60;
        long hours = durationMillis / (1000 * 60 * 60) % 24;
        long days = durationMillis / (1000 * 60 * 60 * 24);

        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append(" day").append(days > 1 ? "s" : "").append(", ");
        }
        if (hours > 0) {
            sb.append(hours).append(" hour").append(hours > 1 ? "s" : "").append(", ");
        }
        if (minutes > 0) {
            sb.append(minutes).append(" minute").append(minutes > 1 ? "s" : "").append(", ");
        }
        if (seconds > 0) {
            sb.append(seconds).append(" second").append(seconds > 1 ? "s" : "");
        }
        String formattedDuration = sb.toString();
        if (formattedDuration.endsWith(", ")) {
            formattedDuration = formattedDuration.substring(0, formattedDuration.length() - 2);
        }
        return formattedDuration;
    }
}
