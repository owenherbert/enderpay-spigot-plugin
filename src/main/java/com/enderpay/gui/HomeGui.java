package com.enderpay.gui;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.enderpay.Enderpay;
import com.enderpay.model.Category;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public final class HomeGui extends BaseGui implements Listener {

    private final int pagesSlotIndex;
    private final int donatorsSlotIndex;
    private final int donationPartiesSlotIndex;
    private final int currenciesSlotIndex;

    public HomeGui() {

        Enderpay.getPlugin().getServer().getPluginManager().registerEvents(this, Enderpay.getPlugin());

        double categoryCount = Enderpay.getCategories().size();
        int categoryRowCount = (int) Math.ceil(categoryCount / 9);

        int totalSlots = (categoryRowCount + 1) * 9;

        this.currenciesSlotIndex = totalSlots - 4;
        this.donationPartiesSlotIndex = totalSlots - 3;
        this.pagesSlotIndex = totalSlots - 2;
        this.donatorsSlotIndex = totalSlots - 1;

        super.inventory = Bukkit.createInventory(null, totalSlots, Enderpay.getStore().getName());

        fillItems(totalSlots);

    }

    private void fillItems(int totalSlots) {

        // add category items to the GUI
        for (int i = 0; i < Enderpay.getCategories().size(); i++) {

            Category category = Enderpay.getCategories().get(i);

            Material material = Material.getMaterial(category.getItemDescription().getId().toUpperCase());
            if (material == null) {
                material = Material.BEDROCK;
            }

            ItemStack itemStack = createGuiItem(
                    material,
                    category.getItemDescription().getName(),
                    category.getItemDescription().getQuantity(),
                    true,
                    category.getItemDescription().isEnchanted(),
                    category.getItemDescription().getLore()
            );

            super.inventory.addItem(itemStack);

        }

        // add glass panes to the GUI
        for (int i = 0; i < 8; i++) {

            int itemIndex = totalSlots - i - 1 - 1; // convert to index by removing one and leave space for pages item

            super.inventory.setItem(itemIndex, makeGlassGuiItem());

        }

        // add pages menu item to the GUI
        super.inventory.setItem(pagesSlotIndex, createGuiItem(
                Material.PAPER,
                "&fPages",
                1,
                true,
                true,
                " ",
                "&8Click on this item to open a list",
                "&8of available store pages."
        ));

        // add donators menu item to the GUI
        super.inventory.setItem(donatorsSlotIndex, createGuiItem(
                XMaterial.ENDER_EYE.parseMaterial(),
                "&fTop Donators",
                1,
                true,
                true,
                " ",
                "&8Click on this item to open the top",
                "&8donators page."
        ));

        // add donation parties menu item to the GUI
        super.inventory.setItem(donationPartiesSlotIndex, createGuiItem(
                XMaterial.FIREWORK_ROCKET.parseMaterial(),
                "&fDonation Parties",
                1,
                true,
                true,
                " ",
                "&8Click on this item to open the",
                "&8donation party status page."
        ));

        // add currency selector menu item to the GUI
        super.inventory.setItem(currenciesSlotIndex, createGuiItem(
                XMaterial.GOLD_INGOT.parseMaterial(),
                "&fCurrency Selector",
                1,
                true,
                true,
                " ",
                "&8Click on this item to open the",
                "&8currency selector page."
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

            if (slotIndex == pagesSlotIndex) {

                PageGui pageGui = new PageGui();

                if (!Enderpay.getPages().isEmpty()) {
                    pageGui.openInventory(player);
                    return;
                }

                player.closeInventory();
                player.sendMessage("");
                player.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + Enderpay.getStore().getName());
                player.sendMessage("");
                player.sendMessage(ChatColor.GRAY + "There are no pages to display!");
                player.sendMessage("");
            }

            if (slotIndex == donatorsSlotIndex) {

                if (Enderpay.getFirstPlaceDonatorUsername() != null) {
                    DonatorsGui donatorsGui = new DonatorsGui(player.getName());
                    donatorsGui.openInventory(player);
                    return;
                }

                player.closeInventory();
                player.sendMessage("");
                player.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + Enderpay.getStore().getName());
                player.sendMessage("");
                player.sendMessage(ChatColor.GRAY + "There are no donators to display!");
                player.sendMessage("");

            }

            if (slotIndex == donationPartiesSlotIndex) {

                if (!Enderpay.getDonationParties().isEmpty()) {
                    DonationPartyGui donationPartyGui = new DonationPartyGui();
                    donationPartyGui.openInventory(player);
                    return;
                }

                player.closeInventory();
                player.sendMessage("");
                player.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + Enderpay.getStore().getName());
                player.sendMessage("");
                player.sendMessage(ChatColor.GRAY + "There are no donation parties to display!");
                player.sendMessage("");
            }

            if (slotIndex == currenciesSlotIndex) {
                CurrencyGui currencyGui = new CurrencyGui();
                currencyGui.openInventory(player);
                return;
            }

            if (slotIndex < Enderpay.getCategories().size()) {
                CategoryGui categoryGui = new CategoryGui(Enderpay.getCategories().get(slotIndex).getId(), player.getName());
                categoryGui.openInventory(player);
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
