package com.nate.fakenetwork.utils.events;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import org.yaml.snakeyaml.Yaml;

import com.nate.fakenetwork.Core;
import com.nate.fakenetwork.utils.Functions.LpMethods;

import java.io.*;
import java.util.*;

public class SwearWordListener implements Listener {
    Core core = Core.getInstance();
    private static Set<String> swearWords = new HashSet<>();

    public SwearWordListener() {
        loadSwearWords();
    }

    @SuppressWarnings("unchecked")
    private void loadSwearWords() {
        File dataFolder = core.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        File configFile = new File(dataFolder, "swearwords.yml");

        if (!configFile.exists()) {
            saveDefaultSwearWords(configFile);
        }

        try (InputStream is = new FileInputStream(configFile)) {
            Yaml yaml = new Yaml();
            Map<String, Object> yamlData = yaml.load(is);

            if (yamlData != null && yamlData.containsKey("swearWords")) {
                swearWords = new HashSet<>((List<String>) yamlData.get("swearWords"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveDefaultSwearWords(File configFile) {
        Set<String> defaultSwearWords = new HashSet<>(Arrays.asList(
                "swear1",
                "swear2",
                "swear3"));

        try (OutputStream os = new FileOutputStream(configFile)) {
            Yaml yaml = new Yaml();
            Map<String, Object> yamlData = new HashMap<>();
            yamlData.put("swearWords", new ArrayList<>(defaultSwearWords));
            yaml.dump(yamlData, new OutputStreamWriter(os));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(ChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        String message = event.getMessage();
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();

        if (containsSwearWord(message)) {
            notifyStaff(player.getName(), message);
        }
    }

    private boolean containsSwearWord(String message) {
        for (String word : swearWords) {
            if (message.toLowerCase().contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    private void notifyStaff(String playerName, String message) {
        String prefix = LpMethods.getPrefix(playerName);

        TextComponent notification = new TextComponent("§eFakeNetwork §f| ");
        notification.setItalic(true);

        TextComponent hoverText = new TextComponent("Mute " + playerName);
        hoverText.setColor(ChatColor.YELLOW);
        hoverText.setItalic(true);

        HoverEvent hoverEvent = new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new BaseComponent[] { hoverText });

        notification.setHoverEvent(hoverEvent);

        ClickEvent clickEvent = new ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                "/mute " + playerName);

        notification.setClickEvent(clickEvent);

        TextComponent messageText = new TextComponent(
                prefix + playerName + ChatColor.translateAlternateColorCodes('&', "&c might have just sworn: "));
        messageText.setColor(ChatColor.WHITE);
        messageText.addExtra(message);

        notification.addExtra(messageText);

        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (player.hasPermission("fakenetwork.staff")) {
                player.sendMessage(notification);
            }
        }
    }

}