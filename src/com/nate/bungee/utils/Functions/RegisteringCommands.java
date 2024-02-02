package com.nate.bungee.utils.Functions;

import com.nate.bungee.Core;
import com.nate.bungee.commands.CrossLink.LinkCommand;
import com.nate.bungee.commands.Debugging.DebugHashmap;
import com.nate.bungee.commands.Levels.LevelsCommand;
import com.nate.bungee.commands.Parties.PartyCommandExecutor;
import com.nate.bungee.commands.Punishments.Duartions;
import com.nate.bungee.commands.Punishments.PunishmentManager;
import com.nate.bungee.commands.Punishments.Bans.BanCommand;
import com.nate.bungee.commands.Punishments.Mutes.MuteManager;
import com.nate.bungee.commands.Punishments.Mutes.Unmute;
import com.nate.bungee.commands.Punishments.Mutes.Reasons.MuteDisrespect;
import com.nate.bungee.commands.Punishments.Mutes.Reasons.MuteHarassment;
import com.nate.bungee.commands.Punishments.Mutes.Reasons.MuteSpam;
import com.nate.bungee.commands.Punishments.Mutes.Reasons.MuteSwear;
import com.nate.bungee.commands.Redirect.HubCommand;
import com.nate.bungee.commands.Reports.AcceptReportCommand;
import com.nate.bungee.commands.Reports.DenyReportCommand;
import com.nate.bungee.commands.Reports.ListReportsCommand;
import com.nate.bungee.commands.Reports.ReportCommand;
import com.nate.bungee.commands.StaffChat.StaffChatCommand;
import com.nate.bungee.utils.events.OnServerStop;

public class RegisteringCommands {

    public void registerAllCommands() {

        ReportCommand reportCommand = new ReportCommand();
        PunishmentManager punishmentManager = new PunishmentManager();

        ListReportsCommand listReportsCommand = new ListReportsCommand();
        PartyCommandExecutor partyCommandExecutor = new PartyCommandExecutor();
        LevelsCommand.LevelSetCommand levelSetCommand = new LevelsCommand().new LevelSetCommand();
        LevelsCommand.LevelExpCommand levelExpCommand = new LevelsCommand().new LevelExpCommand();
        MuteDisrespect muteDisrespect = new MuteDisrespect();
        MuteHarassment muteHarassment = new MuteHarassment();
        DebugHashmap debugHashmap = new DebugHashmap(Core.getInstance(), punishmentManager);
        OnServerStop onServerStop = new OnServerStop();
        HubCommand hubCommand = new HubCommand(onServerStop);
        Unmute unmute = new Unmute();
        Duartions durations = new Duartions(Core.getInstance().databaseManager);

        Core.getInstance().getProxy().getPluginManager().registerCommand(Core.getInstance(), hubCommand);
        Core.getInstance().getProxy().getPluginManager().registerCommand(Core.getInstance(),
                new LinkCommand(Core.getInstance()));
        Core.getInstance().getProxy().getPluginManager().registerCommand(Core.getInstance(), new StaffChatCommand());
        Core.getInstance().getProxy().getPluginManager().registerCommand(Core.getInstance(), new AcceptReportCommand());
        Core.getInstance().getProxy().getPluginManager().registerCommand(Core.getInstance(), new DenyReportCommand());
        Core.getInstance().getProxy().getPluginManager().registerCommand(Core.getInstance(), levelSetCommand);
        Core.getInstance().getProxy().getPluginManager().registerCommand(Core.getInstance(), levelExpCommand);
        Core.getInstance().getProxy().getPluginManager().registerCommand(Core.getInstance(), reportCommand);
        Core.getInstance().getProxy().getPluginManager().registerCommand(Core.getInstance(), listReportsCommand);
        Core.getInstance().getProxy().getPluginManager().registerCommand(Core.getInstance(), partyCommandExecutor);
        Core.getInstance().getProxy().getPluginManager().registerCommand(Core.getInstance(), new MuteManager());
        Core.getInstance().getProxy().getPluginManager().registerCommand(Core.getInstance(), new MuteSwear());
        Core.getInstance().getProxy().getPluginManager().registerCommand(Core.getInstance(), debugHashmap);
        Core.getInstance().getProxy().getPluginManager().registerCommand(Core.getInstance(), unmute);
        Core.getInstance().getProxy().getPluginManager().registerCommand(Core.getInstance(),
                new BanCommand(Core.getInstance()));
        Core.getInstance().getProxy().getPluginManager().registerCommand(Core.getInstance(), new MuteSpam());
        Core.getInstance().getProxy().getPluginManager().registerCommand(Core.getInstance(), durations);
        Core.getInstance().getProxy().getPluginManager().registerCommand(Core.getInstance(), muteDisrespect);
        Core.getInstance().getProxy().getPluginManager().registerCommand(Core.getInstance(), muteHarassment);
    }
}
