package com.enderpay;

import com.enderpay.commands.BuyCommand;
import com.enderpay.commands.SetupCommand;
import org.bukkit.configuration.file.YamlConfiguration;
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

        // register commands
        this.getCommand("setup").setExecutor(new SetupCommand());
        this.getCommand("buy").setExecutor(new BuyCommand());

        // set scheduler
        this.getServer().getScheduler().cancelTasks(this);
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, Enderpay::checkForNewCommands, 0, 4800); // 4 minutes

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
