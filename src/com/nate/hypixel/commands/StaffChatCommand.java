package com.nate.hypixel.commands;

import com.nate.hypixel.Core;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class StaffChatCommand extends Command {
    public StaffChatCommand() {
        super("staffchat", null, "sc");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (!player.hasPermission("fakenetwork.staff")) {
            player.sendMessage(new TextComponent(ChatColor.RED + "You do not have permission to use this command."));
            return;
        }

        if (args.length == 0) {
            player.sendMessage(new TextComponent(ChatColor.RED + "Usage: /staffchat <message>"));
            return;
        }

        String message = String.join(" ", args);
        for (ProxiedPlayer staffMember : Core.getInstance().getProxy().getPlayers()) {
            LuckPerms luckPerms = LuckPermsProvider.get();
            if (!(sender instanceof ProxiedPlayer)) {
                staffMember.sendMessage(new TextComponent(ChatColor.RED + "[SERVER] " + ": " + message));
                return;
            }
            if (staffMember.hasPermission("fakenetwork.staff")) {
                User user = luckPerms.getUserManager().getUser(player.getUniqueId());
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
