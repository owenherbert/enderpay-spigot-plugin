package com.enderpay.gui;

import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.enderpay.Enderpay;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DonatorsGui extends BaseGui implements Listener {

    private final int backSlotIndex;
    private final String playerUsername;

    public DonatorsGui(String playerUsername) {

        Enderpay.getPlugin().getServer().getPluginManager().registerEvents(this, Enderpay.getPlugin());

        this.backSlotIndex = 17;
        this.playerUsername = playerUsername;

        super.inventory = Bukkit.createInventory(null, 18, Enderpay.getStore().getName() + " » Top Donators");

        fillItems(18, playerUsername);
    }

    private void fillItems(int totalSlots, String playerUsername) {

        // add player skulls to the GUI
        for (int i = 0; i < 3; i++) {

            ItemStack itemStack = XMaterial.PLAYER_HEAD.parseItem();
            final ItemMeta meta = itemStack.getItemMeta();

            boolean isNonExistant = true;

            switch (i) {
                case 0:

                    // check that there is a donator and that it is not N/A
                    if (!Enderpay.getFirstPlaceDonatorUsername().equals(Enderpay.DEFAULT_PLACEHOLDER)) {

                        Float amount = Float.parseFloat(Enderpay.getFirstPlaceDonatorAmount()) * Enderpay.getPlayerStoreCurrency(playerUsername).getRate();

                        meta.setDisplayName(
                                ChatColor.WHITE + Enderpay.getFirstPlaceDonatorUsername() + ChatColor.GRAY + " (" +
                                        ChatColor.LIGHT_PURPLE + Enderpay.getBaseCurrency().getSymbol() + String.format("%.2f", amount) + " " + Enderpay.getPlayerStoreCurrency(playerUsername).getIso4217() + ChatColor.GRAY + ")");

                        isNonExistant = false;
                        break;
                    }
                case 1:

                    // check that there is a donator and that it is not N/A
                    if (!Enderpay.getSecondPlaceDonatorUsername().equals(Enderpay.DEFAULT_PLACEHOLDER)) {

                        Float amount = Float.parseFloat(Enderpay.getSecondPlaceDonatorAmount()) * Enderpay.getPlayerStoreCurrency(playerUsername).getRate();

                        meta.setDisplayName(
                                ChatColor.WHITE + Enderpay.getSecondPlaceDonatorUsername() + ChatColor.GRAY + " (" +
                                        ChatColor.LIGHT_PURPLE + Enderpay.getBaseCurrency().getSymbol() + String.format("%.2f", amount) + " " + Enderpay.getPlayerStoreCurrency(playerUsername).getIso4217() + ChatColor.GRAY + ")");

                        isNonExistant = false;
                        break;
                    }
                case 2:

                    // check that there is a donator and that it is not N/A
                    if (!Enderpay.getThirdPlaceDonatorUsername().equals(Enderpay.DEFAULT_PLACEHOLDER)) {

                        Float amount = Float.parseFloat(Enderpay.getThirdPlaceDonatorAmount()) * Enderpay.getPlayerStoreCurrency(playerUsername).getRate();

                        meta.setDisplayName(
                                ChatColor.WHITE + Enderpay.getThirdPlaceDonatorUsername() + ChatColor.GRAY + " (" +
                                        ChatColor.LIGHT_PURPLE + Enderpay.getBaseCurrency().getSymbol() + String.format("%.2f", amount) + " " + Enderpay.getPlayerStoreCurrency(playerUsername).getIso4217() + ChatColor.GRAY + ")");

                        isNonExistant = false;
                        break;
                    }
            }

            if (!isNonExistant) {
                itemStack.setItemMeta(meta);
                super.inventory.setItem(i, itemStack);
            }
        }

        // add glass panes to the GUI
        for (int i = 0; i < 8; i++) {

            int itemIndex = totalSlots - i - 1 - 1; // convert to index by removing one and leave space for pages item

            super.inventory.setItem(itemIndex, makeGlassGuiItem());

        }

        // add back menu item to the GUI
        super.inventory.setItem(backSlotIndex, makeBackGuiItem());
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {

        if (event.getInventory().equals(inventory)) {
            event.setCancelled(true);

            final ItemStack clickedItem = event.getCurrentItem();

            // verify current item is not null
            if (clickedItem == null) return;

            int slotIndex = event.getRawSlot();

            final Player player = (Player) event.getWhoClicked();

            XSound.play(player, "CHICKEN_EGG_POP");

            if (slotIndex == backSlotIndex) {
                HomeGui homeGui = new HomeGui();
                homeGui.openInventory(player);
            }
        }
    }

    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory().equals(inventory)) {
            e.setCancelled(true);
        }
    }
}
