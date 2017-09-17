package com.nickdoran.bungee.dynamicservercreation.util;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * Created by Dr. Nick Doran on 9/17/2017.
 */
public final class DSConfig {

    private Plugin plugin;
    private File dataFolder;
    private File configFile;
    private Configuration configuration;

    /**
     * @param plugin     {@link net.md_5.bungee.api.plugin.Plugin}
     * @param configName The absolute name of the config file, must include extension.
     */
    public DSConfig(Plugin plugin, String configName) {
        this.plugin = plugin;
        this.dataFolder = getPlugin().getDataFolder();
        this.configFile = new File(getDataFolder(), configName);
    }

    /**
     * Checks for dataFolder, creates if doesn't exist.
     * Checks for config.yml, creates if doesn't exist.
     */
    public void loadConfig() {
        try {
            if (!getDataFolder().exists()) {
                boolean ok = getPlugin().getDataFolder().mkdirs();
                //If ! ok, the folder `DynamicServerCreation` failed to be created.
                //Therefore, our config.yml wasn't generated and we cannot get database credentials.
                if (!ok) {
                    //Alert console of the severe.
                    getPlugin().getLogger().severe("Unable to create `DynamicServerCreation` folder inside `Plugins`!");
                    //Disable the plugin.
                    getPlugin().onDisable();
                    return;
                }
            }
            //Check if `config.yml` exist.
            if (!configFile.exists()) {
                //If it does, print to console we've found `config.yml`.
                getPlugin().getLogger().info("The `config.yml` was not found, creating it now!");
                //Copy the defaults inside of `config.yml`.
                try (InputStream in = getPlugin().getResourceAsStream("config.yml")) {
                    Files.copy(in, configFile.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                getPlugin().getLogger().info("Found `config.yml`, loading the configuration..");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Request Configuration from the Configuration Provider.
        try {
            this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(getConfigFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Plugin getPlugin() {
        return plugin;
    }

    private File getDataFolder() {
        return dataFolder;
    }

    private File getConfigFile() {
        return configFile;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * Saves the config.yml.
     */
    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(getConfiguration(), configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        getPlugin().getLogger().info("Configuration has been saved.");
    }
}
