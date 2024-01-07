package com.nate.bungee.utils.events;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.nate.bungee.Core;
import com.nate.bungee.utils.api.SendJoinRequests;
import com.nate.bungee.utils.api.SendLevelRequests;
import com.nate.bungee.utils.api.SendRankRequests;
import com.nate.bungee.utils.storage.hashmaps.PlayerStartTimes;
import com.nate.bungee.utils.storage.mysql.CreateTables;
import com.nate.bungee.utils.storage.mysql.Levels;
import com.nate.bungee.utils.storage.mysql.Playtime;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class OnPlayerJoin implements Listener {
    private Set<ProxiedPlayer> firstTimeConnect = new HashSet<>();

    @EventHandler
    public void postLoginEvent(PostLoginEvent event) {
        firstTimeConnect.add(event.getPlayer());
    }

    @EventHandler
    public void onServerConnect(ServerConnectedEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if (firstTimeConnect.remove(player) && player.hasPermission("fakenetwork.staff.join")) {
            String playerName = player.getName();
            LuckPerms luckPerms = LuckPermsProvider.get();
            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            String prefix = user.getCachedData().getMetaData().getPrefix();
            String serverName = event.getServer().getInfo().getName();
            TextComponent message = new TextComponent(ChatColor.AQUA + "(Staff) "
                    + ChatColor.translateAlternateColorCodes('&', prefix) + playerName
                    + " has connected to " + serverName);
            for (ProxiedPlayer staffPlayer : Core.getInstance().getProxy().getPlayers()) {
                if (staffPlayer.hasPermission("fakenetwork.staff.join")) {
                    staffPlayer.sendMessage(message);
                }
            }
        }
    }
    
    
    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if (player.hasPermission("fakenetwork.staff.join")) {
            String playerName = player.getName();
            LuckPerms luckPerms = LuckPermsProvider.get();
            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            String prefix = user.getCachedData().getMetaData().getPrefix();
            TextComponent message = new TextComponent(ChatColor.AQUA + "(Staff) "
                    + ChatColor.translateAlternateColorCodes('&', prefix) + playerName
                    + " has disconnected from the network.");
            for (ProxiedPlayer staffPlayer : Core.getInstance().getProxy().getPlayers()) {
                if (staffPlayer.hasPermission("fakenetwork.staff.join")) {
                    staffPlayer.sendMessage(message);
                }
            }
        }
    }

    @EventHandler
    public void registerAPIStuff(PostLoginEvent event) {
        String playerName = event.getPlayer().getName();
        UUID playerId = event.getPlayer().getUniqueId();
        PlayerStartTimes playerStartTimes = new PlayerStartTimes();
        Playtime playtime = new Playtime();
        Levels levels = new Levels();
        CreateTables createTables = new CreateTables();
        createTables.addPlayerToLevelsTable(playerId, playerName, 1, 1);

        if (!playerStartTimes.containsKey(playerId)) {
            long totalPlaytimeMillis = playtime.getTotalPlaytime(playerName);
            playerStartTimes.put(playerId, System.currentTimeMillis() - totalPlaytimeMillis);
        }

        SendJoinRequests sendJoinRequests = new SendJoinRequests();
        SendLevelRequests sendLevelRequests = new SendLevelRequests();

        sendJoinRequests.sendJoinRequest(playerName);

        int level = levels.getLevel(playerId);
        String rank = getLuckPermsRank(playerName);
        sendLevelRequests.sendLevelRequest(playerName, level);
        Core.getInstance().getLogger().info(playerId + ":" + playerName + "Rank:" + rank);
        SendRankRequests.sendRankRequest(playerName, rank);
    }

    private String getLuckPermsRank(String playerName) {
        UUID playerUUID = Core.getInstance().getProxy().getPlayer(playerName).getUniqueId();
        User user = LuckPermsProvider.get().getUserManager().getUser(playerUUID);

        if (user == null) {
            return "Default";
        }

        return user.getPrimaryGroup();
    }

}
