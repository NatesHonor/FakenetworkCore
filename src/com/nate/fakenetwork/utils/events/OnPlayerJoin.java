package com.nate.fakenetwork.utils.events;

import java.util.UUID;

import com.nate.fakenetwork.Core;
import com.nate.fakenetwork.utils.api.SendJoinRequests;
import com.nate.fakenetwork.utils.api.SendLevelRequests;
import com.nate.fakenetwork.utils.api.SendRankRequests;
import com.nate.fakenetwork.utils.storage.hashmaps.PlayerStartTimes;
import com.nate.fakenetwork.utils.storage.mysql.CreateTables;
import com.nate.fakenetwork.utils.storage.mysql.Levels;
import com.nate.fakenetwork.utils.storage.mysql.Playtime;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
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
        SendRankRequests.sendRankRequest(playerId, rank);
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
