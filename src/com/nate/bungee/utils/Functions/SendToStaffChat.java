package com.nate.bungee.utils.Functions;

import com.nate.bungee.Core;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SendToStaffChat {

    public void sendMessageToStaffChat(ProxiedPlayer player, String[] args) {
        String message = String.join(" ", args);
        User user = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId());
        String prefix = user.getCachedData().getMetaData().getPrefix();

        BaseComponent[] messageComponents = new BaseComponent[] {
                new TextComponent(ChatColor.GREEN + "[Staff Chat] "),
                new TextComponent(ChatColor.translateAlternateColorCodes('&', prefix)),
                new TextComponent(player.getName() + ": "),
                new TextComponent(ChatColor.GRAY + message)
        };

        for (ProxiedPlayer staffMember : Core.getInstance().getProxy().getPlayers()) {
            if (staffMember.hasPermission("fakenetwork.staff")) {
                staffMember.sendMessage(messageComponents);
            }
        }
    }
}
