package com.enderpay.gui;

import com.cryptomorin.xseries.XSound;
import com.enderpay.Enderpay;
import com.enderpay.model.Category;
import com.enderpay.model.Currency;
import com.enderpay.model.Package;
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

public final class CategoryGui extends BaseGui implements Listener {

    private final ArrayList<Package> packages;
    private final int backSlotIndex;
    private final String playerUsername;

    public CategoryGui(int categoryId, String playerUsername) {

        Enderpay.getPlugin().getServer().getPluginManager().registerEvents(this, Enderpay.getPlugin());

        Category category = Enderpay.getCategoryWithCategoryId(categoryId);

        this.packages = Enderpay.getPackagesWithCategoryId(categoryId);
        this.playerUsername = playerUsername;

        double packageCount = packages.size();
        int packageRowCount = (int) Math.ceil(packageCount / 9);

        int totalSlots = (packageRowCount + 1) * 9;

        this.backSlotIndex = totalSlots - 1;

        String inventoryName = Enderpay.getStore().getName() + " » " + category.getName();

        super.inventory = Bukkit.createInventory(null, totalSlots, inventoryName);

        fillItems(totalSlots, playerUsername);

    }

    private void fillItems(int totalSlots, String playerUsername) {

        // add package items to the GUI
        for (int i = 0; i < this.packages.size(); i++) {

            Package pckg = this.packages.get(i);

            Material material = Material.getMaterial(pckg.getItemDescription().getId().toUpperCase());
            if (material == null) {
                material = Material.BEDROCK;
            }

            Currency playerCurrency = Enderpay.getPlayerStoreCurrency(playerUsername);

            String price = String.format("%.2f", pckg.getPrice() * playerCurrency.getRate());


            String name = pckg.getItemDescription().getName() + " " + ChatColor.GRAY + "(" +
                    ChatColor.LIGHT_PURPLE + playerCurrency.getSymbol() + price + " " +
                    playerCurrency.getIso4217() + ChatColor.GRAY + ")";

            ItemStack itemStack = createGuiItem(
                    material,
                    name,
                    pckg.getItemDescription().getQuantity(),
                    true,
                    pckg.getItemDescription().isEnchanted(),
                    pckg.getItemDescription().getLore()
            );

            super.inventory.addItem(itemStack);
        }

        // add glass panes to the GUI
        for (int i = 0; i < 8; i++) {

            int itemIndex = totalSlots - i - 1 - 1; // convert to index by removing one and leave space for pages item

            super.inventory.setItem(itemIndex, makeGlassGuiItem());

        }

        // add back menu item to the GUI
        super.inventory.setItem(totalSlots - 1, makeBackGuiItem());
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

            if (slotIndex < packages.size()) {
                Package clickedPackage = packages.get(slotIndex);

                player.closeInventory();
                player.sendMessage("");
                player.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + Enderpay.getStore().getName());
                player.sendMessage("");
                player.sendMessage(ChatColor.GRAY + "Click the link below to view " + ChatColor.LIGHT_PURPLE + clickedPackage.getName() + ChatColor.GRAY + "!");
                player.sendMessage("");
                player.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.UNDERLINE + clickedPackage.getLink() + "?username=" + player.getName());
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
