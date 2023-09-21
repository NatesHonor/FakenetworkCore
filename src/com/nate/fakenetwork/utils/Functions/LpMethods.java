package com.nate.fakenetwork.utils.Functions;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ChatColor;

public class LpMethods {
    static LuckPerms luckPerms = LuckPermsProvider.get();

    public static String getPrefix(String username) {
        User user = luckPerms.getUserManager().getUser(username);
        if (user != null) {
            String prefix = ChatColor.translateAlternateColorCodes('&', user.getCachedData().getMetaData().getPrefix());
            if (prefix != null) {
                return prefix;
            }
        }
        return "&7";
    }
}
