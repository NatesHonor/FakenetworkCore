package com.nate.bungee.utils.storage.hashmaps;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerStartTimes {
    private final Map<UUID, Long> playerStartTimes = new HashMap<>();

    public void put(UUID playerId, long startTime) {
        playerStartTimes.put(playerId, startTime);
    }

    public Long get(UUID playerId) {
        return playerStartTimes.get(playerId);
    }

    public boolean containsKey(UUID playerId) {
        return playerStartTimes.containsKey(playerId);
    }

    public void remove(UUID playerId) {
        playerStartTimes.remove(playerId);
    }
}
