package com.nate.bungee.commands.Redirect;

import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import com.nate.bungee.utils.events.OnServerStop;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class HubCommand extends Command {

    private OnServerStop onServerStop;

    public HubCommand(OnServerStop onServerStop) {
        super("hub");
        this.onServerStop = onServerStop;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Only players can use this command!"));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        onServerStop.redirectPlayerToHub(player);
    }
}
