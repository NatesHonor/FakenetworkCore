package com.nate.fakenetwork.utils.api;

import java.util.ArrayList;
import java.util.List;

import com.nate.fakenetwork.Core;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

public class Endpoint {
    private List<ProxiedPlayer> staffMembers;
    private Core core;

    public Endpoint(Core coreInstance, int port) {
        this.core = coreInstance;
        this.staffMembers = getStaffWithPermission("fakenetwork.reports");

        Spark.port(port);
        setupRoutes();
    }

    private void setupRoutes() {
        Spark.post("/report", "application/json", new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                if (request.requestMethod().equals("POST")) {
                    String reported = request.queryParams("reported");
                    String reason = request.queryParams("reason");

                    String reportedUserPrefix = getLuckPermsPrefix(reported);
                    if (reportedUserPrefix.isEmpty()) {
                        System.out.println("User " + reported + " not found in LuckPerms.");
                        response.status(200);
                        return "Report received successfully!";
                    } else {
                        TextComponent reportMessage = new TextComponent(ChatColor.translateAlternateColorCodes('&',
                                "&4&lReport | &cWatchdog &7reported " +
                                        reportedUserPrefix + reported + ChatColor.RED
                                        + " for &4" + reason + "in &6server"));

                        for (ProxiedPlayer staff : staffMembers) {
                            staff.sendMessage(reportMessage);
                        }

                        response.status(200);
                        return "Report received successfully!";
                    }
                } else {
                    response.status(405);
                    return "Method not allowed";
                }
            }
        });
    }

    public void startAPI() {
        Spark.awaitInitialization();
    }

    public void stopAPI() {
        Spark.stop();
    }

    private List<ProxiedPlayer> getStaffWithPermission(String permission) {
        List<ProxiedPlayer> staffMembers = new ArrayList<>();
        for (ProxiedPlayer player : core.getProxy().getPlayers()) {
            if (player.hasPermission(permission)) {
                staffMembers.add(player);
            }
        }
        return staffMembers;
    }

    private String getLuckPermsPrefix(String username) {
        User user = LuckPermsProvider.get().getUserManager().getUser(username);
        if (user != null) {
            String prefix = user.getCachedData().getMetaData().getPrefix();
            if (prefix != null) {
                return ChatColor.translateAlternateColorCodes('&', prefix);
            }
        }
        return "";
    }
}
