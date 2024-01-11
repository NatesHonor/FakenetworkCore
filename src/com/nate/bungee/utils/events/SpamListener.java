package com.nate.bungee.utils.events;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SpamListener implements Listener {
    private final Map<UUID, List<MessageRecord>> playerMessages = new ConcurrentHashMap<>();

    private static final int MAX_SIMILAR_MESSAGES = 3;
    private static final long MESSAGE_EXPIRY_TIME_MS = 60 * 1000;

    @EventHandler
    public void onPlayerChat(net.md_5.bungee.api.event.ChatEvent event) {
        if (event.isCommand() || !(event.getSender() instanceof ProxiedPlayer)) {
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        String message = event.getMessage();
        List<MessageRecord> messages = playerMessages.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>());
        pruneOldMessages(messages);
        messages.add(new MessageRecord(message, System.currentTimeMillis()));
        if (isSpam(messages)) {
            notifyStaff(player, messages);
            messages.clear();
        }
    }

    private void pruneOldMessages(List<MessageRecord> messages) {
        messages.removeIf(m -> System.currentTimeMillis() - m.timestamp > MESSAGE_EXPIRY_TIME_MS);
    }

    private boolean isSpam(List<MessageRecord> messages) {
        if (messages.size() < MAX_SIMILAR_MESSAGES) {
            return false;
        }

        String lastMessage = messages.get(messages.size() - 1).message;
        return messages.stream()
                .filter(m -> m.message.equalsIgnoreCase(lastMessage))
                .count() >= MAX_SIMILAR_MESSAGES;
    }

    private void notifyStaff(ProxiedPlayer player, List<MessageRecord> messages) {
        String lastMessage = messages.get(messages.size() - 1).message;

        TextComponent notification = new TextComponent(player.getName() + " might be spamming: ");
        notification.setColor(ChatColor.YELLOW);

        TextComponent spamContent = new TextComponent(
                lastMessage.length() > 50 ? lastMessage.substring(0, 47) + "..." : lastMessage);
        spamContent.setColor(ChatColor.WHITE);

        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text("Click to mute " + player.getName()));

        ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mute " + player.getName());

        notification.setHoverEvent(hoverEvent);
        notification.setClickEvent(clickEvent);
        notification.addExtra(spamContent);

        for (ProxiedPlayer staff : ProxyServer.getInstance().getPlayers()) {
            if (staff.hasPermission("fakenetwork.staff")) {
                staff.sendMessage(notification);
            }
        }
    }

    private static class MessageRecord {
        final String message;
        final long timestamp;

        MessageRecord(String message, long timestamp) {
            this.message = message;
            this.timestamp = timestamp;
        }
    }
}
