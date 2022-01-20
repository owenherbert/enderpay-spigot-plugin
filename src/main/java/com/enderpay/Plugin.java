package com.enderpay;

import com.enderpay.api.EnderpayApi;
import com.enderpay.commands.*;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Plugin extends JavaPlugin {

    public File fileConfig;
    public YamlConfiguration config;

    @Override
    public void onEnable() {

        // Plugin startup logic

        this.saveDefaultConfig();

        fileConfig = new File(this.getDataFolder().getPath(), "config.yml");
        config = YamlConfiguration.loadConfiguration(fileConfig);

        // check api key and secret specified in the config
        String cfgApiKey = config.getString("api-key");
        String cfgApiSecret = config.getString("api-secret");

        if (cfgApiKey.length() != 25 || cfgApiSecret.length() != 25) {

            MessageBroadcaster.toConsole("You must enter your correct API details into the Enderpay plugin configuration file. Plugin will now disable.");

            Bukkit.getPluginManager().disablePlugin(this);

            return;
        }

        // setup permissions - Vault is required so the plugin will disable if it is not found
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {

            MessageBroadcaster.toConsole("The plugin requires the Vault plugin to be installed to function correctly. Please stop your server and install the Vault plugin.");

            Bukkit.getPluginManager().disablePlugin(this);

            return;

        }

        // register commands
        this.getCommand("enderpay-setup").setExecutor(new SetupCommand());
        this.getCommand("enderpay-sync").setExecutor(new SyncCommand());
        this.getCommand("enderpay-force").setExecutor(new ForceCommand());
        this.getCommand("enderpay-help").setExecutor(new HelpCommand());
        this.getCommand("buy").setExecutor(new BuyCommand());

        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        Enderpay.setPermissions(rsp.getProvider());

        // set scheduler
        this.getServer().getScheduler().cancelTasks(this);
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, Enderpay::checkForNewCommands, 0, 4800); // 4 minutes
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, Enderpay::uploadPlayers, 0, 4800); // 4 minutes

        Enderpay.setPlugin(this);

        // build the models and guis
        Enderpay.buildModelsAndGuis();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public File getFileConfig() {
        return fileConfig;
    }

    public void setFileConfig(File fileConfig) {
        this.fileConfig = fileConfig;
    }

    public void setConfig(YamlConfiguration config) {
        this.config = config;
    }
}
