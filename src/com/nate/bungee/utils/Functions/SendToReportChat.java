package com.nate.bungee.utils.Functions;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class SendToReportChat {

    public static TextComponent createReportMessage(String reportedPlayer, String reason) {
        String reportedUserPrefix = "";
        String message = ChatColor.translateAlternateColorCodes('&',
                "&4&lReport | &cWatchdog &7reported " +
                        reportedUserPrefix + reportedPlayer + ChatColor.RED
                        + " for &4" + reason + "in &6server");
        return new TextComponent(message);
    }
}
