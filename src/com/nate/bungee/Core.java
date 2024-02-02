package com.nate.bungee;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.nate.bungee.commands.Punishments.SQLStatements.DatabaseManager;
import com.nate.bungee.utils.Functions.RegisteringCommands;
import com.nate.bungee.utils.Functions.RegisteringEvents;
import com.nate.bungee.utils.storage.mysql.CreateTables;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Core extends Plugin implements Listener {
    private Connection connection;
    private static Core instance;
    public DatabaseManager databaseManager;
    public DataSource dataSource;
    public static Core getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        dataSource = setupDataSource();
        databaseManager = new DatabaseManager(dataSource);
        connection = setupDatabase();
        CreateTables createTables = new CreateTables();
        createTables.createAllTables();
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
        RegisteringEvents registeringEvents = new RegisteringEvents();
        registeringEvents.registerAllEvents();
        RegisteringCommands registeringCommands = new RegisteringCommands();
        registeringCommands.registerAllCommands();
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

    private DataSource setupDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/fakenetwork");
        config.setUsername("root");
        config.setPassword("");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
        return new HikariDataSource(config);
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
