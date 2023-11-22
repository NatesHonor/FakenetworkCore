package com.nate.nbungeestaff;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.nate.nbungeestaff.commands.CrossLink.LinkCommand;
import com.nate.nbungeestaff.commands.Debugging.DebugHashmap;
import com.nate.nbungeestaff.commands.Levels.LevelsCommand;
import com.nate.nbungeestaff.commands.Parties.PartyCommandExecutor;
import com.nate.nbungeestaff.commands.Punishments.PunishmentManager;
import com.nate.nbungeestaff.commands.Punishments.Mutes.MuteManager;
import com.nate.nbungeestaff.commands.Punishments.Mutes.Reasons.MuteSwear;
import com.nate.nbungeestaff.commands.Punishments.SQLStatements.Warns;
import com.nate.nbungeestaff.commands.Reports.AcceptReportCommand;
import com.nate.nbungeestaff.commands.Reports.DenyReportCommand;
import com.nate.nbungeestaff.commands.Reports.ListReportsCommand;
import com.nate.nbungeestaff.commands.Reports.ReportCommand;
import com.nate.nbungeestaff.commands.StaffChat.StaffChatCommand;
import com.nate.nbungeestaff.utils.events.OnPlayerJoin;
import com.nate.nbungeestaff.utils.events.OnPlayerLeave;
import com.nate.nbungeestaff.utils.events.OnServerConnect;
import com.nate.nbungeestaff.utils.events.StaffChatEventListener;
import com.nate.nbungeestaff.utils.events.SwearWordListener;
import com.nate.nbungeestaff.utils.storage.mysql.CreateTables;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Core extends Plugin implements Listener {
    private Connection connection;
    private static Core instance;

    public static Core getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        connection = setupDatabase();
        CreateTables createTables = new CreateTables();
        Warns warns = new Warns();
        warns.createWarnsTable();
        createTables.createLevelsTable();
        createTables.createReportsTable();
        createTables.createAcceptedReportsTable();
        createTables.createDeniedReportsTable();
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdir();
            }
            File configFile = new File(getDataFolder(), "config.yml");
            if (!configFile.exists()) {
                configFile.createNewFile();
                Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
                config.set("api-key", "your_api_key_here");
                config.set("api", "https://api.site.com");
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, configFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        OnPlayerJoin onPlayerJoin = new OnPlayerJoin();
        ReportCommand reportCommand = new ReportCommand();
        OnPlayerLeave onPlayerLeave = new OnPlayerLeave();
        OnServerConnect onServerConnect = new OnServerConnect();
        PunishmentManager punishmentManager = new PunishmentManager();
        SwearWordListener swearWordListener = new SwearWordListener();
        ListReportsCommand listReportsCommand = new ListReportsCommand();
        PartyCommandExecutor partyCommandExecutor = new PartyCommandExecutor();
        StaffChatEventListener staffChatEventListener = new StaffChatEventListener();
        LevelsCommand.LevelSetCommand levelSetCommand = new LevelsCommand().new LevelSetCommand();
        LevelsCommand.LevelExpCommand levelExpCommand = new LevelsCommand().new LevelExpCommand();
        DebugHashmap debugHashmap = new DebugHashmap(this, punishmentManager);

        getProxy().getPluginManager().registerListener(this, this);
        getProxy().getPluginManager().registerListener(this, onPlayerJoin);
        getProxy().getPluginManager().registerListener(this, onPlayerLeave);
        getProxy().getPluginManager().registerListener(this, onServerConnect);
        getProxy().getPluginManager().registerListener(this, swearWordListener);
        getProxy().getPluginManager().registerListener(this, punishmentManager);
        getProxy().getPluginManager().registerListener(this, staffChatEventListener);

        getProxy().getPluginManager().registerCommand(this, new LinkCommand(this));
        getProxy().getPluginManager().registerCommand(this, new StaffChatCommand());
        getProxy().getPluginManager().registerCommand(this, new AcceptReportCommand());
        getProxy().getPluginManager().registerCommand(this, new DenyReportCommand());
        getProxy().getPluginManager().registerCommand(this, levelSetCommand);
        getProxy().getPluginManager().registerCommand(this, levelExpCommand);
        getProxy().getPluginManager().registerCommand(this, reportCommand);
        getProxy().getPluginManager().registerCommand(this, listReportsCommand);
        getProxy().getPluginManager().registerCommand(this, partyCommandExecutor);
        getProxy().getPluginManager().registerCommand(this, new MuteManager());
        getProxy().getPluginManager().registerCommand(this, new MuteSwear());
        getProxy().getPluginManager().registerCommand(this, debugHashmap);
    }

    @Override
    public void onDisable() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Configuration getPluginConfig() throws IOException {
        File configFile = new File(getDataFolder(), "config.yml");
        Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        return config;
    }

    private Connection setupDatabase() {
        String url = "jdbc:mysql://localhost:3306/fakenetwork?useSSL=false&serverTimezone=UTC";
        String username = "root";
        String password = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    public Connection getConnection() {
        return connection;
    }
}
