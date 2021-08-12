package com.enderpay.commands;

import com.enderpay.Enderpay;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

public class BuyCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // check if models have been loaded from the API
        if (sender instanceof HumanEntity) {

            if (Enderpay.isLoaded()) {
                Enderpay.getHomeGui().openInventory((HumanEntity) sender);
            } else {

                Player player = (Player) sender;

                player.sendMessage("");
                player.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Enderpay");
                player.sendMessage("");
                player.sendMessage(ChatColor.GRAY + "The buy menu cannot be opened because the server has");
                player.sendMessage(ChatColor.GRAY + "not loaded data from Enderpay. Please try again soon.");
                player.sendMessage("");
            }
        }

        return true;
    }
}
