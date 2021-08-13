package com.enderpay.commands;

import com.enderpay.Enderpay;
import com.enderpay.MessageBroadcaster;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ForceCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Enderpay.checkForNewCommands();

        if (sender instanceof Player) {
            Player player = (Player) sender;

            player.sendMessage("");
            player.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Enderpay");
            player.sendMessage("");
            player.sendMessage(ChatColor.GRAY + "Command queue has been successfully retrieved.");
            player.sendMessage("");
        } else {
            MessageBroadcaster.toConsole("Checking for new commands!");
        }

        return true;
    }
}
