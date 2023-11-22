package com.nate.nbungeestaff.commands.Debugging;

import com.nate.nbungeestaff.commands.Punishments.PunishmentManager;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

public class DebugHashmap extends Command {

    private final PunishmentManager punishmentManager;

    public DebugHashmap(Plugin plugin, PunishmentManager punishmentManager) {
        super("debug-hashmap");
        this.punishmentManager = punishmentManager;
        plugin.getProxy().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(new TextComponent("Debugging Muted Players HashMap:"));

        for (String playerName : punishmentManager.getMutedPlayers().keySet()) {
            PunishmentManager.MuteInfo muteInfo = punishmentManager.getMutedPlayers().get(playerName);
            sender.sendMessage(new TextComponent(
                    playerName + " - Reason: " + muteInfo.reason + " - Unmute Time: " + muteInfo.unmuteTime));
        }
    }
}
