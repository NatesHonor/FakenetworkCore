package com.nate.fakenetwork.utils.events;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import com.nate.fakenetwork.Core;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;

public class OnServerConnect implements Listener {

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if (player.hasPermission("fakenetwork.staff.join")) {
            String playerName = player.getName();

            LuckPerms luckPerms = LuckPermsProvider.get();
            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            String prefix = user.getCachedData().getMetaData().getPrefix();

            String serverName = event.getTarget().getName();
            TextComponent message = new TextComponent(ChatColor.AQUA + "(Staff) "
                    + ChatColor.translateAlternateColorCodes('&', prefix) + playerName + " has connected to "
                    + serverName);
            for (ProxiedPlayer staffPlayer : Core.getInstance().getProxy().getPlayers()) {
                if (staffPlayer.hasPermission("fakenetwork.staff.join")) {
                    staffPlayer.sendMessage(message);
                }
            }
        }
    }
}
