package com.nate.hypixel;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.nate.hypixel.commands.LevelsCommand;
import com.nate.hypixel.commands.LinkCommand;
import com.nate.hypixel.commands.StaffChatCommand;
import com.nate.hypixel.commands.Guilds.GuildCommandExecutor;
import com.nate.hypixel.commands.Parties.PartyCommandExecutor;
import com.nate.hypixel.commands.Reports.ListReportsCommand;
import com.nate.hypixel.commands.Reports.ReportCommand;
import com.nate.hypixel.utils.events.OnPlayerJoin;
import com.nate.hypixel.utils.events.OnPlayerLeave;
import com.nate.hypixel.utils.events.OnServerConnect;
import com.nate.hypixel.utils.events.StaffChatEventListener;
import com.nate.hypixel.utils.storage.mysql.CreateTables;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Core extends Plugin implements Listener {
    private Connection connection;
    private static Core instance;

    @Override
    public void onEnable() {
        instance = this;
        connection = setupDatabase();

        CreateTables createTables = new CreateTables();
        createTables.createLevelsTable();

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

        ReportCommand reportCommand = new ReportCommand();
        ListReportsCommand listReportsCommand = new ListReportsCommand();
        GuildCommandExecutor guildCommandExecutor = new GuildCommandExecutor();
        PartyCommandExecutor partyCommandExecutor = new PartyCommandExecutor();
        LevelsCommand.LevelSetCommand levelSetCommand = new LevelsCommand().new LevelSetCommand();
        LevelsCommand.LevelExpCommand levelExpCommand = new LevelsCommand().new LevelExpCommand();

        getProxy().getPluginManager().registerListener(this, this);
        getProxy().getPluginManager().registerListener(this, new OnPlayerJoin());
        getProxy().getPluginManager().registerListener(this, new OnPlayerLeave());
        getProxy().getPluginManager().registerListener(this, new OnServerConnect());
        getProxy().getPluginManager().registerListener(this, new StaffChatEventListener());
        getProxy().getPluginManager().registerCommand(this, new LinkCommand(this));
        getProxy().getPluginManager().registerCommand(this, new StaffChatCommand());
        getProxy().getPluginManager().registerCommand(this, levelSetCommand);
        getProxy().getPluginManager().registerCommand(this, levelExpCommand);
        getProxy().getPluginManager().registerCommand(this, guildCommandExecutor);
        getProxy().getPluginManager().registerCommand(this, reportCommand);
        getProxy().getPluginManager().registerCommand(this, listReportsCommand);
        getProxy().getPluginManager().registerCommand(this, partyCommandExecutor);
    }

    public static Core getInstance() {
        return instance;
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
