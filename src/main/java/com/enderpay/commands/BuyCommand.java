package com.enderpay.commands;

import com.enderpay.Enderpay;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;

public class BuyCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // check if models have been loaded from the API
        if (Enderpay.isLoaded() && sender instanceof HumanEntity) {

            Enderpay.getHomeGui().openInventory((HumanEntity) sender);

        }

        return true;
    }
}
