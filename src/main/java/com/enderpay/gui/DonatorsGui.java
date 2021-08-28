package com.enderpay.gui;

import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.enderpay.Enderpay;
import com.enderpay.model.Category;
import com.enderpay.utils.UuidConverter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class DonatorsGui extends BaseGui implements Listener {

    private final int backSlotIndex;

    public DonatorsGui() {

        Enderpay.getPlugin().getServer().getPluginManager().registerEvents(this, Enderpay.getPlugin());

        this.backSlotIndex = 8;

        inventory = Bukkit.createInventory(null, 9, Enderpay.getStore().getName() + " » Top Donators");

        fillItems(9);
    }

    private void fillItems(int totalSlots) {

        // add player skulls to the GUI
        for (int i = 0; i < 3; i++) {

            ItemStack itemStack = XMaterial.PLAYER_HEAD.parseItem();
            final ItemMeta meta = itemStack.getItemMeta();

            switch (i) {
                case 0:
                    SkullUtils.applySkin(itemStack.getItemMeta(), Enderpay.getFirstPlaceDonatorUuid());

                    meta.setDisplayName(
                            ChatColor.WHITE + Enderpay.getFirstPlaceDonatorUsername() + ChatColor.GRAY + " (" +
                                    ChatColor.LIGHT_PURPLE + Enderpay.getCurrency().getSymbol() + Enderpay.getFirstPlaceDonatorAmount() + "" + Enderpay.getCurrency().getIso4217() + ChatColor.GRAY + ")");

                    break;
                case 1:
                    SkullUtils.applySkin(itemStack.getItemMeta(), Enderpay.getSecondPlaceDonatorUuid());

                    meta.setDisplayName(
                            ChatColor.WHITE + Enderpay.getSecondPlaceDonatorUsername() + ChatColor.GRAY + " (" +
                                    ChatColor.LIGHT_PURPLE + Enderpay.getCurrency().getSymbol() + Enderpay.getSecondPlaceDonatorAmount() + "" + Enderpay.getCurrency().getIso4217() + ChatColor.GRAY + ")");

                    break;
                case 2:
                    SkullUtils.applySkin(itemStack.getItemMeta(), Enderpay.getThirdPlaceDonatorUuid());

                    meta.setDisplayName(
                            ChatColor.WHITE + Enderpay.getThirdPlaceDonatorUsername() + ChatColor.GRAY + " (" +
                                    ChatColor.LIGHT_PURPLE + Enderpay.getCurrency().getSymbol() + Enderpay.getThirdPlaceDonatorAmount() + "" + Enderpay.getCurrency().getIso4217() + ChatColor.GRAY + ")");

                    break;
            }

            itemStack.setItemMeta(meta);

            inventory.setItem(i, itemStack);
        }

        // add back menu item to the GUI
        inventory.setItem(totalSlots - 1, createGuiItem(
                Material.ARROW,
                "&fGo Back",
                1,
                true,
                false,
                ""
        ));
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
                Enderpay.getHomeGui().openInventory(player);
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
