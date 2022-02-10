package com.enderpay.gui;

import com.cryptomorin.xseries.XSound;
import com.enderpay.Enderpay;
import com.enderpay.model.Page;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public final class PageGui extends BaseGui implements Listener {

    private final ArrayList<Page> pages;
    private final int backSlotIndex;

    public PageGui() {

        Enderpay.getPlugin().getServer().getPluginManager().registerEvents(this, Enderpay.getPlugin());

        this.pages = Enderpay.getPages();

        double pageCount = pages.size();
        int pageRowCount = (int) Math.ceil(pageCount / 9);

        int totalSlots = (pageRowCount + 1) * 9;

        this.backSlotIndex = totalSlots - 1;

        String inventoryName = Enderpay.getStore().getName() + " » " + "Pages";

        super.inventory = Bukkit.createInventory(null, totalSlots, inventoryName);

        fillItems(totalSlots);

    }

    private void fillItems(int totalSlots) {

        // add package items to the GUI
        for (int i = 0; i < this.pages.size(); i++) {

            Page page = this.pages.get(i);

            Material material = Material.getMaterial(page.getItemDescription().getId().toUpperCase());
            if (material == null) {
                material = Material.BEDROCK;
            }

            ItemStack itemStack = createGuiItem(
                    material,
                    page.getItemDescription().getName(),
                    page.getItemDescription().getQuantity(),
                    true,
                    page.getItemDescription().isEnchanted(),
                    page.getItemDescription().getLore()
            );

            super.inventory.addItem(itemStack);
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

            // show the home GUI to the player
            if (slotIndex == backSlotIndex) {
               HomeGui homeGui = new HomeGui();
               homeGui.openInventory(player);
                return;
            }

            if (slotIndex < pages.size()) {
                Page clickedPage = pages.get(slotIndex);

                player.closeInventory();
                player.sendMessage("");
                player.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + Enderpay.getStore().getName());
                player.sendMessage("");
                player.sendMessage(ChatColor.GRAY + "Click the link below to view the " + ChatColor.LIGHT_PURPLE + clickedPage.getName() + ChatColor.GRAY + " page!");
                player.sendMessage("");
                player.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.UNDERLINE + clickedPage.getLink());
                player.sendMessage("");
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
