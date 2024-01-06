package com.nate.bungee.utils.Parties;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PartyManager {
    private List<Party> parties;

    public PartyManager() {
        parties = new ArrayList<>();
    }

    public void createParty(UUID leaderUUID) {
        Party party = new Party(leaderUUID);
        parties.add(party);
    }

    public Party getParty(UUID playerUUID) {
        for (Party party : parties) {
            if (party.containsPlayer(playerUUID)) {
                return party;
            }
        }
        return null;
    }

    public void removeParty(Party party) {
        parties.remove(party);
    }

    public class Party {
        private UUID leaderUUID;
        private List<UUID> members;

        public Party(UUID leaderUUID) {
            this.leaderUUID = leaderUUID;
            members = new ArrayList<>();
            members.add(leaderUUID);
        }

        public boolean containsPlayer(UUID playerUUID) {
            return members.contains(playerUUID);
        }

        public void addMember(UUID playerUUID) {
            members.add(playerUUID);
        }

        public void removeMember(UUID playerUUID) {
            members.remove(playerUUID);
        }

        public UUID getLeaderUUID() {
            return leaderUUID;
        }

        public List<UUID> getMembers() {
            return members;
        }
    }
}
