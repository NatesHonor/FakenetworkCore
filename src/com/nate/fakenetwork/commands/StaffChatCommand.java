package com.nate.fakenetwork.commands;

import com.nate.fakenetwork.Core;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class StaffChatCommand extends Command {
    public StaffChatCommand() {
        super("staffchat", null, "sc", "staff");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (!player.hasPermission("fakenetwork.staff")) {
            player.sendMessage(new TextComponent(ChatColor.RED + "You do not have permission to use this command."));
            return;
        }

        if (args.length == 0) {
            listStaffMembers(player);
            return;
        }

        sendToStaffChat(player, args);
    }

    private void listStaffMembers(ProxiedPlayer player) {
        player.sendMessage(new TextComponent(ChatColor.GREEN + "Active Staff Members:"));
        for (ProxiedPlayer staffMember : Core.getInstance().getProxy().getPlayers()) {
            if (staffMember.hasPermission("fakenetwork.staff")) {
                User user = LuckPermsProvider.get().getUserManager().getUser(staffMember.getUniqueId());
                String prefix = user.getCachedData().getMetaData().getPrefix();
                player.sendMessage(
                        new TextComponent(ChatColor.translateAlternateColorCodes('&', prefix) + staffMember.getName()));
            }
        }
    }

    private void sendToStaffChat(ProxiedPlayer player, String[] args) {
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
