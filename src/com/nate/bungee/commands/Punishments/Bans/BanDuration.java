package com.nate.bungee.commands.Punishments.Bans;

public class BanDuration {
    public static String getDuration(String reason) {
        if (reason.equalsIgnoreCase("fly")) {
            return "1 day";
        } else if (reason.equalsIgnoreCase("cheating")) {
            return "30 days";
        } else {
            return "Permanent";
        }
    }
}
