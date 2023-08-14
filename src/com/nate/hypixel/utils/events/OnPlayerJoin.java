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

import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class OnPlayerJoin implements Listener {

    @EventHandler
    public void registerAPIStuff(PostLoginEvent event) {
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
    }
}
