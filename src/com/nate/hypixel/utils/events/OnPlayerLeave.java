package com.nate.hypixel.utils.events;

import java.util.UUID;

import com.nate.hypixel.Core;
import com.nate.hypixel.utils.api.SendLeaveRequests;
import com.nate.hypixel.utils.api.SendPlaytimeRequests;
import com.nate.hypixel.utils.storage.hashmaps.PlayerStartTimes;
import com.nate.hypixel.utils.storage.mysql.Playtime;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class OnPlayerLeave implements Listener {
    @EventHandler
    public void onPlayerLeave(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        Playtime playtime = new Playtime();
        SendLeaveRequests sendLeaveRequests = new SendLeaveRequests();
        PlayerStartTimes playerStartTimes = new PlayerStartTimes();

        String playerName = event.getPlayer().getName();

        sendLeaveRequests.sendLeaveRequest(playerName);

        UUID playerId = event.getPlayer().getUniqueId();
        if (playerStartTimes.containsKey(playerId)) {
            long startTime = playerStartTimes.get(playerId);
            long playtimeMillis = System.currentTimeMillis() - startTime;
            playtime.updateTotalPlaytime(playerName, playtimeMillis);

            SendPlaytimeRequests.updatePlaytime(playerName, playtimeMillis);
            playerStartTimes.remove(playerId);
        }
        if (player.hasPermission("fakenetwork.staff.leave")) {
            LuckPerms luckPerms = LuckPermsProvider.get();

            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            String prefix = user.getCachedData().getMetaData().getPrefix();
            String serverName = player.getServer() != null ? player.getServer().getInfo().getName() : "Unknown";
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
