package com.nate.bungee.commands.StaffChat;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.nate.bungee.Core;
import com.nate.bungee.utils.Functions.SendToStaffChat;

public class StaffChatCommand extends Command {
    private final Map<String, Integer> groupPriorities = new HashMap<>();
    Core core = Core.getInstance();

    public StaffChatCommand() {
        super("staffchat", null, "sc", "staff");

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
        if (!player.hasPermission("nbungee.staff")) {
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
            try {
                if (core.getPluginConfig().getBoolean("") == true) {
                    String staffRole = getStaffRole(staffMember);
                    String prefix = ChatColor.translateAlternateColorCodes('&', "&7[" + staffRole + "&7]");
                    BaseComponent[] components = TextComponent
                            .fromLegacyText(prefix + staffMember.getName());
                    player.sendMessage(components);
                } else {
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        User user = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId());
        String prefix = user.getCachedData().getMetaData().getPrefix();
        for (Map.Entry<String, Integer> entry : groupPriorities.entrySet()) {
            if (entry.getValue() == priority) {
                return entry.getKey();
            }
        }

        return prefix;
    }
}
