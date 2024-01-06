package com.nate.bungee.utils.events;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.event.EventHandler;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OnServerStop implements Listener {

    private int lobbyCounter = 0;

    @EventHandler
    public void onServerStop(ServerKickEvent event) {
        ProxiedPlayer player = event.getPlayer();
        BaseComponent[] kickReasonComponents = event.getKickReasonComponent();
        String kickReason = BaseComponent.toLegacyText(kickReasonComponents);

        if (isServerStopReason(kickReason)) {
            redirectPlayer(player, true);
            event.setCancelled(true);
        }
    }

    public void redirectPlayerToHub(ProxiedPlayer player) {
        redirectPlayer(player, false);
    }

    private boolean isServerStopReason(String reason) {
        String lowerCaseReason = reason.toLowerCase();
        return lowerCaseReason.equals("could not connect to target server, you have been moved to a fallback server.");
    }

    private void redirectPlayer(ProxiedPlayer player, boolean showMessage) {
        List<String> lobbies = Arrays.asList("lobby1", "lobby2", "lobby3");
        List<String> availableLobbies = lobbies.stream()
                .filter(lobbyName -> isLobbyAvailable(lobbyName, player))
                .collect(Collectors.toList());
        if (availableLobbies.isEmpty()) {
            player.disconnect(new TextComponent("No open lobbies available, please reconnect"));
            return;
        }

        String lobby = availableLobbies.get(lobbyCounter % availableLobbies.size());
        lobbyCounter++;

        player.connect(ProxyServer.getInstance().getServerInfo(lobby));
        if (showMessage) {
            player.sendMessage(new TextComponent(
                    "§c§lThe server you were in has gone down and you have been redirected to one of our hubs!"));
        }
    }

    private boolean isLobbyAvailable(String lobbyName, ProxiedPlayer player) {
        ServerInfo lobbyServer = ProxyServer.getInstance().getServerInfo(lobbyName);
        return lobbyServer != null && lobbyServer.canAccess(player);
    }
}
