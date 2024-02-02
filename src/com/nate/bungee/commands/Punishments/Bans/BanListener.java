package com.nate.bungee.commands.Punishments.Bans;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class BanListener implements Listener {

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        ProxyServer.getInstance().getLogger().info("Received a plugin message on channel: " + event.getTag());

        if (!event.getTag().equals("BungeeCord")) {
            ProxyServer.getInstance().getLogger().info("The message is not for BungeeCord. Ignoring.");
            return;
        }

        DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
        try {
            String subchannel = in.readUTF();
            ProxyServer.getInstance().getLogger().info("Received subchannel: " + subchannel);
            if (subchannel.equals("ExecuteBan")) {
                String executorUuid = in.readUTF();
                String reason = in.readUTF();
                String targetUuid = in.readUTF();
                boolean isSilent = in.readBoolean();
                ProxyServer.getInstance().getLogger().info("Ban details: Executor UUID: " + executorUuid
                        + ", Target UUID: " + targetUuid + ", Reason: " + reason + ", Silent: " + isSilent);

                ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(targetUuid);
                if (targetPlayer != null) {
                    ProxyServer.getInstance().getLogger().info("Ban for player received: " + targetPlayer.getName());

                    String banDuration = BanDuration.getDuration(reason);
                    String kickMessage = "You have been banned from this server!\n" +
                            "Reason: " + reason + "\n" +
                            "Duration: " + banDuration + "\n" +
                            "Appeal at appeal.fakenetwork.net";
                    targetPlayer.disconnect(new TextComponent(kickMessage));
                } else {
                    ProxyServer.getInstance().getLogger()
                            .warning("Target player with UUID: " + targetUuid + " not found or not online.");
                }
            } else {
                ProxyServer.getInstance().getLogger().info("Subchannel " + subchannel + " is not handled.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
