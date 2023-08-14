package com.nate.hypixel.utils.events;

import java.util.UUID;

import com.nate.hypixel.Core;
import com.nate.hypixel.GetRank;
import com.nate.hypixel.utils.api.SendJoinRequests;
import com.nate.hypixel.utils.api.SendLevelRequests;
import com.nate.hypixel.utils.api.SendRankRequests;
import com.nate.hypixel.utils.storage.hashmaps.PlayerStartTimes;
import com.nate.hypixel.utils.storage.mysql.CreateTables;
import com.nate.hypixel.utils.storage.mysql.Levels;
import com.nate.hypixel.utils.storage.mysql.Playtime;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class OnPlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        String playerName = event.getPlayer().getName();
        UUID playerId = event.getPlayer().getUniqueId();
        PlayerStartTimes playerStartTimes = new PlayerStartTimes();
        Playtime playtime = new Playtime();
        Levels levels = new Levels();
        CreateTables createTables = new CreateTables();
        GetRank getRank = new GetRank(Core.getInstance());
        createTables.addPlayerToLevelsTable(playerId, playerName, 1, 1);

        if (!playerStartTimes.containsKey(playerId)) {
            long totalPlaytimeMillis = playtime.getTotalPlaytime(playerName);
            playerStartTimes.put(playerId, System.currentTimeMillis() - totalPlaytimeMillis);
        }

        SendJoinRequests sendJoinRequests = new SendJoinRequests();
        SendLevelRequests sendLevelRequests = new SendLevelRequests();

        sendJoinRequests.sendJoinRequest(playerName);

        int level = levels.getLevel(playerId);
        String rank = getRank.getRank(playerName);
        sendLevelRequests.sendLevelRequest(playerName, level);
        SendRankRequests.sendRankRequest(playerName, rank);

        if (player.hasPermission("fakenetwork.staff.join")) {
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
