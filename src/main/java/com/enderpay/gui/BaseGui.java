package com.enderpay.gui;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public abstract class BaseGui {

    protected Inventory inventory;

    protected String replaceAndColours(String original) {
        return original.replaceAll("&", "§");
    }

    protected ItemStack makeBackGuiItem() {
        return createGuiItem(
                Material.ARROW,
                "&fGo Back",
                1,
                true,
                false,
                " ",
                "&8Click on this item to open the",
                "&8previous page."
        );
    }

    protected ItemStack makeGlassGuiItem() {
        return createGuiItem(
                XMaterial.GRAY_STAINED_GLASS_PANE.parseMaterial(),
                " ",
                1,
                false,
                false,
                ""
        );
    }

    protected ItemStack createGuiItem(final Material material, final String name, int quantity, boolean displayClickInName, boolean isEnchanted, final String... lore) {

        boolean emptyLore = false;

        // replace lore colour codes
        String[] newLore = new String[lore.length];

        for (int i = 0; i < lore.length; i++) {
            String loreLine = lore[i];

            if (loreLine == null || loreLine.length() == 0) {
                emptyLore = true;
                break;
            }

            newLore[i] = replaceAndColours(loreLine);
        }

        final ItemStack item = new ItemStack(material, quantity);
        final ItemMeta meta = item.getItemMeta();

        // if there is meta
        if (meta != null) {

            // Set the name of the item
            String displayName = replaceAndColours(name);
            if (displayClickInName) displayName += "" + ChatColor.GRAY + " (Click)";

            meta.setDisplayName(displayName);

            // Set the lore of the item
            if (!emptyLore) meta.setLore(Arrays.asList(newLore));

            // add enchantment
            if (isEnchanted) {
                meta.addEnchant(Enchantment.KNOCKBACK, 1, true);
            }

            item.setItemMeta(meta);

        }

        return item;
    }

    public void openInventory(final HumanEntity entity) {
        entity.openInventory(inventory);
    }

}
