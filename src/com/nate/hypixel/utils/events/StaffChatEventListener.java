package com.nate.hypixel.utils.events;

import com.nate.hypixel.Core;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class StaffChatEventListener implements Listener {

    @EventHandler
    public void onChat(ChatEvent event) {
        if (event.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) event.getSender();
            String message = event.getMessage();

            if (message.startsWith("#") && player.hasPermission("fakenetwork.staff")) {
                message = message.substring(1);

                for (ProxiedPlayer staffMember : Core.getInstance().getProxy().getPlayers()) {
                    if (staffMember.hasPermission("fakenetwork.staff")
                            || staffMember.hasPermission("fakenetwork.staff.chat.code")) {
                        staffMember.sendMessage(new TextComponent(
                                ChatColor.GREEN + "[Staff Chat] " + player.getName() + ": " + message));
                    }
                }
                event.setCancelled(true);
            } else {
                event.setCancelled(false);
            }
        }
    }
}
