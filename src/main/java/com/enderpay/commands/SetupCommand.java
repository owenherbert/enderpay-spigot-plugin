package com.enderpay.commands;

import com.enderpay.Enderpay;
import com.enderpay.Plugin;
import com.enderpay.config.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.IOException;

public class SetupCommand implements CommandExecutor {

    public static int EXPECTED_ARGUMENTS = 2;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length != EXPECTED_ARGUMENTS) return false;

        String apiKey = args[0];
        String apiSecret = args[1];

        Plugin plugin = Enderpay.getPlugin();
        plugin.getConfig().set(Config.API_KEY, apiKey);
        plugin.getConfig().set(Config.API_SECRET, apiSecret);
        try {
            plugin.getConfig().save(plugin.getFileConfig());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }
}
