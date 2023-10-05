package com.nate.fakenetwork.utils.api;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import com.nate.fakenetwork.Core;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;

public class SendRankRequests {
    public static void sendRankRequest(UUID playerUUID, String rank) {
        Core core = Core.getInstance();
        try {
            String apiUrl = core.getPluginConfig().getString("api");
            if (apiUrl == null || apiUrl.isEmpty()) {
                core.getLogger().warning("API URL is not set in the configuration.");
                return;
            }

            URL url = new URL(apiUrl + "/player/rank");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            String apiKey = core.getPluginConfig().getString("api-key");
            if (apiKey == null || apiKey.isEmpty()) {
                core.getLogger().warning("API key is not set in the configuration.");
                return;
            }
            conn.setRequestProperty("X-API-Key", apiKey);

            conn.setDoOutput(true);

            User user = LuckPermsProvider.get().getUserManager().getUser(playerUUID);
            String currentRank = user == null ? "default" : user.getPrimaryGroup();

            String jsonInputString = "{\"playerUUID\": \"" + playerUUID.toString() + "\", \"currentRank\": \""
                    + currentRank + "\", \"newRank\": \"" + rank + "\"}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                core.getLogger().info("Player rank request sent successfully.");
            } else {
                core.getLogger().warning("Failed to send player rank request. Response code: " + responseCode);
            }

            conn.disconnect();
        } catch (IOException e) {
            core.getLogger().warning("Failed to send player rank request. Error: " + e.getMessage());
        }
    }
}
