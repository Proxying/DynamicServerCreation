package com.nickdoran.bungee.dynamicservercreation;

import com.nickdoran.bungee.dynamicservercreation.commands.CommandDSC;
import com.nickdoran.bungee.dynamicservercreation.util.DSConfig;
import com.nickdoran.bungee.dynamicservercreation.util.DatabaseManager;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Created by Dr. Nick Doran on 9/17/2017.
 */
public final class DynamicServerCreation extends Plugin {

    private static DynamicServerCreation instance;
    private DSConfig dsConfig;
    private DatabaseManager databaseManager;

    private Map<String, DynamicServer> dynamicServers = new HashMap<>();

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        dsConfig = new DSConfig(this, "config.yml");

        //Load the config file, does all necessary work
        //To ensure it exist, etc.
        dsConfig.loadConfig();

        String host = dsConfig.getConfiguration().getString("database.mysql.host");
        String user = dsConfig.getConfiguration().getString("database.mysql.user");
        String password = dsConfig.getConfiguration().getString("database.mysql.password");
        String database = dsConfig.getConfiguration().getString("database.mysql.database");
        int port = dsConfig.getConfiguration().getInt("database.mysql.port");

        databaseManager = new DatabaseManager(host, user, password, database, port);

        try {
            //Open connection to database, not-async.
            //We want to make sure we grab the available server(s)
            //Before bungee allows players to join.
            databaseManager.openConnection();

            //Check to make sure our table exist.
            //If it doesn't, create it.
            databaseManager.checkTableIntegrity();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            super.onDisable();
        }

        databaseManager.getDynamicServers(result -> result.getDynamicServers().forEach(server -> {
            server.injectToProxy();
            addDynamicServer(server);
        }));

        PluginManager pluginManager = getProxy().getPluginManager();
        pluginManager.registerCommand(this, new CommandDSC("dsc"));

        getLogger().info("has started up successfully!");
    }

    @Override
    public void onDisable() {
        dsConfig.saveConfig();
        databaseManager.shutdown();
    }

    public static DynamicServerCreation getInstance() {
        return instance;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    /**
     * Checks if local cache has Dynamic Server.
     *
     * @param serverName Name of Dynamic Server {@link DynamicServer}
     * @return True if map contains key.
     */
    public boolean doesDynamicServerExist(String serverName) {
        return dynamicServers.containsKey(serverName);
    }

    /**
     * Adds the servers to a local cache.
     *
     * @param server {@link DynamicServer}
     */
    public void addDynamicServer(DynamicServer server) {
        if (!dynamicServers.containsKey(server.getServerName())) {
            dynamicServers.put(server.getServerName(), server);
            getLogger().log(Level.INFO, "Adding Dynamic Server {0} to local cache.", server.getServerName());
            getDatabaseManager().addServer(server);
        }
    }
}
