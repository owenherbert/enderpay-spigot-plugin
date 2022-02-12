package com.enderpay.commands;

import com.enderpay.Enderpay;
import com.enderpay.MessageBroadcaster;
import com.enderpay.Plugin;
import com.enderpay.config.Config;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetupCommand implements CommandExecutor {

    public static int EXPECTED_ARGUMENTS = 2;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length != EXPECTED_ARGUMENTS) return false;

        String apiKey = args[0];
        String apiSecret = args[1];

        try {

            Plugin plugin = Enderpay.getPlugin();
            plugin.getConfig().set(Config.API_KEY, apiKey);
            plugin.getConfig().set(Config.API_SECRET, apiSecret);

            plugin.getConfig().save(plugin.getFileConfig());

            Enderpay.buildModelsAndGuis();

            if (sender instanceof Player) {
                Player player = (Player) sender;

                player.sendMessage("");
                player.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Enderpay");
                player.sendMessage("");
                player.sendMessage(ChatColor.GRAY + "The plugin has been successfully set up. You can see");
                player.sendMessage(ChatColor.GRAY + "how your menu looks with the command " + ChatColor.LIGHT_PURPLE + "/buy" + ChatColor.GRAY + ". If you make");
                player.sendMessage(ChatColor.GRAY + "any changes online, be sure to sync the plugin with the");
                player.sendMessage(ChatColor.GRAY + "command " + ChatColor.LIGHT_PURPLE + "/enderpay-sync" + ChatColor.GRAY + ".");
                player.sendMessage("");
            } else {
                MessageBroadcaster.toConsole("Plugin has been setup!");
            }
        } catch (Exception exception) {
            MessageBroadcaster.toConsole("An error occurred while trying to setup Enderpay.");
        }

        return true;
    }
}
