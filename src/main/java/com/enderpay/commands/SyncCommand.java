package com.enderpay.commands;

import com.enderpay.Enderpay;
import com.enderpay.MessageBroadcaster;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SyncCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Enderpay.buildModelsAndGuis();

        if (sender instanceof Player) {
            Player player = (Player) sender;

            player.sendMessage("");
            player.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Enderpay");
            player.sendMessage("");
            player.sendMessage(ChatColor.GRAY + "The plugin has been successfully synced.");
            player.sendMessage("");
        } else {
            MessageBroadcaster.toConsole("Plugin has been synced!");
        }

        return true;
    }
}
