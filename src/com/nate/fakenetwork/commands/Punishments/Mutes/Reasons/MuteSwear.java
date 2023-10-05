package com.nate.fakenetwork.commands.Punishments.Mutes.Reasons;

import com.nate.fakenetwork.commands.Punishments.Warns.Mutes;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class MuteSwear extends Command {

    public MuteSwear() {
        super("mswear");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            String playerName = player.getName();
            String reason = "Swearing";
            int durationInDays = 1;

            Mutes mutes = new Mutes();
            mutes.applyMute(playerName, reason, durationInDays);
            String message = "You have been muted for swearing for 1 day.";
            player.sendMessage(new TextComponent(message));
        } else {
            sender.sendMessage(new TextComponent("This command can only be executed by a player."));
        }
    }
}
