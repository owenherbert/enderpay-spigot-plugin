package com.enderpay.gui;

import com.cryptomorin.xseries.XSound;
import com.enderpay.Enderpay;
import com.enderpay.model.Category;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public final class HomeGui extends BaseGui implements Listener {

    private final int pagesSlotIndex;

    public HomeGui() {

        Enderpay.getPlugin().getServer().getPluginManager().registerEvents(this, Enderpay.getPlugin());

        double categoryCount = Enderpay.getCategories().size();
        int categoryRowCount = (int) Math.ceil(categoryCount / 9);

        int totalSlots = (categoryRowCount + 1) * 9;

        this.pagesSlotIndex = totalSlots - 1;

        inventory = Bukkit.createInventory(null, totalSlots, Enderpay.getStore().getName());

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

            inventory.addItem(itemStack);

        }

        // add glass panes to the GUI
        for (int i = 0; i < 8; i++) {

            int itemIndex = totalSlots - i - 1 - 1; // convert to index by removing one and leave space for pages item

            Material material = Material.getMaterial("GRAY_STAINED_GLASS_PANE");
            if (material == null) material = Material.AIR;

            inventory.setItem(itemIndex, createGuiItem(
                    material,
                    "",
                    1,
                    false,
                    false,
                    ""
            ));

        }

        // add pages menu item to the GUI
        inventory.setItem(totalSlots - 1, createGuiItem(
                Material.PAPER,
                "&fPages",
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

            if (slotIndex == pagesSlotIndex) {
                Enderpay.getPageGui().openInventory(player);
                return;
            }

            if (slotIndex < Enderpay.getCategoryGuiHashMap().size()) {
                CategoryGui categoryGui = Enderpay.getCategoryGuiHashMap().get(Enderpay.getCategories().get(slotIndex).getId());
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
