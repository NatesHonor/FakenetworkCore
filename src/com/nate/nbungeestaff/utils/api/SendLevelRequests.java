package com.nate.nbungeestaff.utils.api;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.nate.nbungeestaff.Core;

public class SendLevelRequests {
    private static final String LEVEL_API_URL = "http://localhost:3000/player/level";

    public void sendLevelRequest(String playerName, int level) {
        Core core = Core.getInstance();
        new Thread(() -> {
            try {
                URL url = new URL(LEVEL_API_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");

                String apiKey = core.getPluginConfig().getString("api-key");
                conn.setRequestProperty("X-API-Key", apiKey);

                conn.setDoOutput(true);

                String jsonInputString = "{\"playerName\": \"" + playerName + "\", \"level\": " + level + "}";

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    core.getLogger().info("Player level request sent successfully.");
                } else {
                    core.getLogger().warning("Failed to send player level request. Response code: " + responseCode);
                }

                conn.disconnect();
            } catch (IOException e) {
                core.getLogger().warning("Failed to send player level request. Error: " + e.getMessage());
            }
        }).start();
    }
}
