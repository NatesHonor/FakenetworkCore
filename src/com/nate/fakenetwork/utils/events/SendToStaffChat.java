package com.nate.fakenetwork.utils.events;

import com.nate.fakenetwork.Core;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SendToStaffChat {

    public void sendMessageToStaffChat(ProxiedPlayer player, String[] args) {
        String message = String.join(" ", args);
        for (ProxiedPlayer staffMember : Core.getInstance().getProxy().getPlayers()) {
            if (staffMember.hasPermission("fakenetwork.staff")) {
                User user = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId());
                String prefix = user.getCachedData().getMetaData().getPrefix();
                staffMember.sendMessage(
                        new TextComponent(
                                ChatColor.GREEN + "[Staff Chat] "
                                        + ChatColor.translateAlternateColorCodes('&', prefix) + player.getName() + ": "
                                        + ChatColor.GRAY + message));
            }
        }
    }

}
