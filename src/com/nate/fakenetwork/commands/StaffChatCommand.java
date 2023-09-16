package com.nate.fakenetwork.commands;

import com.nate.fakenetwork.Core;
import com.nate.fakenetwork.utils.events.SendToStaffChat;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StaffChatCommand extends Command {
    private final Map<String, Integer> groupPriorities = new HashMap<>();

    public StaffChatCommand() {
        super("staffchat", null, "sc", "staff");

        // Assign priorities to groups
        groupPriorities.put("admin", 6);
        groupPriorities.put("gamemaster", 5);
        groupPriorities.put("mod", 4);
        groupPriorities.put("builder", 3);
        groupPriorities.put("contractors", 2);
        groupPriorities.put("helper", 1);
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
        SendToStaffChat sendToStaffChat = new SendToStaffChat();
        sendToStaffChat.sendMessageToStaffChat(player, args);
    }

    private void listStaffMembers(ProxiedPlayer player) {
        List<ProxiedPlayer> staffMembers = Core.getInstance().getProxy().getPlayers()
                .stream()
                .filter(staffMember -> staffMember.hasPermission("fakenetwork.staff"))
                .sorted(Comparator.comparingInt(this::getStaffMemberPriority))
                .collect(Collectors.toList());

        if (staffMembers.isEmpty()) {
            player.sendMessage(new TextComponent(ChatColor.GREEN + "No active staff members online."));
            return;
        }

        player.sendMessage(new TextComponent(ChatColor.GREEN + "Active Staff Members:"));
        for (ProxiedPlayer staffMember : staffMembers) {
            String staffRole = getStaffRole(staffMember);
            String prefix = ChatColor.translateAlternateColorCodes('&', "&7[" + staffRole + "&7]");
            BaseComponent[] components = TextComponent
                    .fromLegacyText(prefix + staffMember.getName());
            player.sendMessage(components);
        }
    }

    private int getStaffMemberPriority(ProxiedPlayer player) {
        User user = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId());
        if (user != null) {
            List<String> groups = user.getNodes().stream()
                    .filter(node -> node.getType() == net.luckperms.api.node.NodeType.INHERITANCE)
                    .map(node -> node.getKey().toLowerCase())
                    .collect(Collectors.toList());

            int maxPriority = 0;
            for (String group : groups) {
                Integer priority = groupPriorities.get(group);
                if (priority != null && priority > maxPriority) {
                    maxPriority = priority;
                }
            }

            return maxPriority;
        }
        return 0;
    }

    private String getStaffRole(ProxiedPlayer player) {
        int priority = getStaffMemberPriority(player);

        for (Map.Entry<String, Integer> entry : groupPriorities.entrySet()) {
            if (entry.getValue() == priority) {
                return entry.getKey();
            }
        }

        return "Unknown";
    }
}
