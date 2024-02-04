package com.nate.bungee.utils.Functions;

import com.nate.bungee.Core;
import com.nate.bungee.commands.Punishments.PunishmentManager;
import com.nate.bungee.commands.Punishments.Bans.BanChecker;
import com.nate.bungee.commands.Punishments.Bans.BanListener;
import com.nate.bungee.commands.Punishments.Mutes.MuteManager;
import com.nate.bungee.utils.events.OnPlayerJoin;
import com.nate.bungee.utils.events.OnPlayerLeave;
import com.nate.bungee.utils.events.OnServerStop;
import com.nate.bungee.utils.events.SpamListener;
import com.nate.bungee.utils.events.StaffChatEventListener;
import com.nate.bungee.utils.events.SwearWordListener;

import net.md_5.bungee.api.plugin.Listener;

public class RegisteringEvents implements Listener {

    public void registerAllEvents() {

        OnPlayerJoin onPlayerJoin = new OnPlayerJoin();
        OnPlayerLeave onPlayerLeave = new OnPlayerLeave();
        PunishmentManager punishmentManager = new PunishmentManager();
        SwearWordListener swearWordListener = new SwearWordListener();
        StaffChatEventListener staffChatEventListener = new StaffChatEventListener();
        OnServerStop onServerStop = new OnServerStop();
        MuteManager.ChatListener muteManagerChatListener = new MuteManager.ChatListener();
        BanListener banListener = new BanListener();
        BanChecker banChecker = new BanChecker();

        Core.getInstance().getProxy().getPluginManager().registerListener(Core.getInstance(), banChecker);
        Core.getInstance().getProxy().getPluginManager().registerListener(Core.getInstance(), banListener);
        Core.getInstance().getProxy().getPluginManager().registerListener(Core.getInstance(), Core.getInstance());
        Core.getInstance().getProxy().getPluginManager().registerListener(Core.getInstance(), onPlayerJoin);
        Core.getInstance().getProxy().getPluginManager().registerListener(Core.getInstance(), onPlayerLeave);
        Core.getInstance().getProxy().getPluginManager().registerListener(Core.getInstance(), swearWordListener);
        Core.getInstance().getProxy().getPluginManager().registerListener(Core.getInstance(), punishmentManager);
        Core.getInstance().getProxy().getPluginManager().registerListener(Core.getInstance(), staffChatEventListener);
        Core.getInstance().getProxy().getPluginManager().registerListener(Core.getInstance(), onServerStop);
        Core.getInstance().getProxy().getPluginManager().registerListener(Core.getInstance(), muteManagerChatListener);
        Core.getInstance().getProxy().getPluginManager().registerListener(Core.getInstance(), new SpamListener());
        Core.getInstance().getProxy().getPluginManager().registerListener(Core.getInstance(), new BanListener());
    }
}
