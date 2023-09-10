package com.nate.fakenetwork.utils.api;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.nate.fakenetwork.Core;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Endpoint extends AbstractHandler {
    List<ProxiedPlayer> staffMembers = getStaffWithPermission("fakenetwork.reports");
    Core core = Core.getInstance();

    private Server server;

    public Endpoint(int port) {
        this.server = new Server(port);
        this.server.setHandler(this);
    }

    public void startAPI() throws Exception {
        server.start();
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

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        if ("/report".equals(target) && baseRequest.getMethod().equals("POST")) {
            String reported = request.getParameter("reported");
            String reason = request.getParameter("reason");

            String reportedUserPrefix = getLuckPermsPrefix(reported);

            TextComponent reportMessage = new TextComponent(ChatColor.translateAlternateColorCodes('&',
                    "&4&lReport | &cWatchdog &7reported " +
                            reportedUserPrefix + reported + ChatColor.RED
                            + " for &4" + reason + "in &6server"));

            for (ProxiedPlayer staff : staffMembers) {
                staff.sendMessage(reportMessage);
            }

            // Send a response
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Report received successfully!");
            baseRequest.setHandled(true);
        }
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

    public void stopAPI() throws Exception {
        server.stop();
    }
}
