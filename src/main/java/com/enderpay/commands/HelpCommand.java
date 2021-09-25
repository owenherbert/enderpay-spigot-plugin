package com.enderpay.commands;

import com.enderpay.MessageBroadcaster;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;

            player.sendMessage("");
            player.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Enderpay");
            player.sendMessage("");
            player.sendMessage(ChatColor.LIGHT_PURPLE + "/buy " + ChatColor.GRAY + "Open server buy menu.");
            player.sendMessage(ChatColor.LIGHT_PURPLE + "/enderpay-setup <api-key> <api-secret> " + ChatColor.GRAY + "Setup API credentials.");
            player.sendMessage(ChatColor.LIGHT_PURPLE + "/enderpay-force " + ChatColor.GRAY + "Check for new commands.");
            player.sendMessage(ChatColor.LIGHT_PURPLE + "/enderpay-sync " + ChatColor.GRAY + "Sync the latest data.");
            player.sendMessage("");
        } else {
            MessageBroadcaster.toConsole("This command can only be executed by a player!");
        }

        return true;
    }
}
