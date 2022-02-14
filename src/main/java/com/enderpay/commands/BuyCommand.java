package com.enderpay.commands;

import com.cryptomorin.xseries.XSound;
import com.enderpay.Enderpay;
import com.enderpay.Plugin;
import com.enderpay.config.Config;
import com.enderpay.gui.HomeGui;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public class BuyCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // check if models have been loaded from the API
        if (sender instanceof HumanEntity) {

            Player player = (Player) sender;

            if (Enderpay.isLoaded()) {

                HomeGui homeGui = new HomeGui();
                homeGui.openInventory((HumanEntity) sender);

                // check and play sound if supported
                if (XSound.ENTITY_EXPERIENCE_ORB_PICKUP.isSupported()) {
                    XSound.play(player, XSound.ENTITY_EXPERIENCE_ORB_PICKUP.toString());
                }

                try {

                    // try launch firework if enabled in config

                    if (Config.getBuyCommandFireworkEnabled()) {

                        // above players head
                        Location fireworkLocation = player.getEyeLocation().add(0, 10, 0);

                        Firework firework = (Firework) player.getWorld()
                                .spawnEntity(fireworkLocation, EntityType.FIREWORK);

                        FireworkEffect fireworkEffect = FireworkEffect.builder()
                                .trail(true)
                                .flicker(true)
                                .withColor(Color.PURPLE)
                                .with(FireworkEffect.Type.BALL)
                                .build();

                        FireworkMeta fireworkMeta = firework.getFireworkMeta();

                        fireworkMeta.setPower(4);
                        fireworkMeta.addEffect(fireworkEffect);

                        firework.setFireworkMeta(fireworkMeta);
                        firework.detonate();

                    }

                } catch (Exception e) {

                }

            } else {

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
