package com.nate.fakenetwork.utils.api;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.nate.fakenetwork.Core;

public class SendPlaytimeRequests {
    public static void updatePlaytime(String playerName, long playtimeMillis) {
        Core core = Core.getInstance();
        try {
            Object apiUrlObj = core.getPluginConfig().get("api");
            if (apiUrlObj instanceof String) {
                String apiUrl = (String) apiUrlObj;
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                String payload = String.format("{\"playerName\": \"%s\", \"playtimeMillis\": %d}", playerName,
                        playtimeMillis);

                connection.getOutputStream().write(payload.getBytes());

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    System.out.println("Playtime updated for player: " + playerName);
                } else {
                    System.out.println("Failed to update playtime for player: " + playerName);
                }

                connection.disconnect();
            } else {
                System.out.println("Invalid API URL configuration.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
