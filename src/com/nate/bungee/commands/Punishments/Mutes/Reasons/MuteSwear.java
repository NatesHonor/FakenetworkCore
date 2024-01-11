package com.nate.bungee.commands.Punishments.Mutes.Reasons;

import com.nate.bungee.commands.Punishments.SQLStatements.Mutes;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class MuteSwear extends Command {

    public MuteSwear() {
        super("mswear");
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

            String reason = "Swearing";
            int offenseCount = mutes.getOffenseCount(targetPlayerName, reason);
            int durationInDays = offenseCount + 1; 

            mutes.applyMute(targetPlayerName, reason, durationInDays);

            ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(targetPlayerName);
            if (targetPlayer != null) {
                targetPlayer.sendMessage(
                        new TextComponent("You have been muted for swearing for " + durationInDays + " day(s)."));
                if (offenseCount > 0) {
                    targetPlayer.sendMessage(new TextComponent("Since this is offense # " + (offenseCount + 1)
                            + "your mute has been extended by " + offenseCount + " day(s)."));
                }
            }

            sender.sendMessage(
                    new TextComponent("Player '" + targetPlayerName + "' has been muted for swearing for "
                            + durationInDays + " day(s)."));
        } else {
            sender.sendMessage(new TextComponent("This command can only be executed by a player."));
        }
    }
}
