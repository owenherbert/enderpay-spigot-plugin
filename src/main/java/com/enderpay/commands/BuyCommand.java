package com.enderpay.commands;

import com.cryptomorin.xseries.XSound;
import com.enderpay.Enderpay;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
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
                Enderpay.getHomeGui().openInventory((HumanEntity) sender);
                XSound.play(player, "ORB_PICKUP");

                // launch firework if enabled in config
                boolean spawnFirework = Enderpay.getPlugin().getConfig().getBoolean("buy-command-firework");

                if (spawnFirework) {

                    Firework firework = (Firework) player.getWorld()
                            .spawnEntity(player.getEyeLocation(), EntityType.FIREWORK);

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
