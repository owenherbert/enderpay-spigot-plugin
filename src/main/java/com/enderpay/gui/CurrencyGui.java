package com.enderpay.gui;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.enderpay.Enderpay;
import com.enderpay.model.Currency;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class CurrencyGui extends BaseGui implements Listener {

    private final ArrayList<Currency> currencies;
    private final int backSlotIndex;

    public CurrencyGui() {

        Enderpay.getPlugin().getServer().getPluginManager().registerEvents(this, Enderpay.getPlugin());

        this.currencies = Enderpay.getCurrencies();

        double currencyCount = this.currencies.size();
        int currencyRowCount = (int) Math.ceil(currencyCount / 9);

        int totalSlots = (currencyRowCount + 1) * 9;

        this.backSlotIndex = totalSlots - 1;

        String inventoryName = Enderpay.getStore().getName() + " » " + "Currency Selector";

        super.inventory = Bukkit.createInventory(null, totalSlots, inventoryName);

        super.inventory.clear();

        fillItems(totalSlots);

    }

    private void fillItems(int totalSlots) {

        for (int i = 0; i < this.currencies.size(); i++) {

            Currency currency = this.currencies.get(i);

            ItemStack itemStack = createGuiItem(XMaterial.GOLD_INGOT.parseMaterial(),
                    "&f" + currency.getName() + " &7(&d" + currency.getSymbol() + "&7)",
                    1,
                    true,
                    true,
                    " ",
                    "&8Click on this item to change the",
                    "&8store currency to &d" + currency.getIso4217() + "&8!");

            this.inventory.addItem(itemStack);

        }

        // add glass panes to the GUI
        for (int i = 0; i < 8; i++) {

            int itemIndex = totalSlots - i - 1 - 1;

            inventory.setItem(itemIndex, makeGlassGuiItem());

        }

        // add back menu item to the GUI
        inventory.setItem(backSlotIndex, makeBackGuiItem());

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

            // handle change player currency event
            if (slotIndex < this.currencies.size()) {

                Currency selectedCurrency = this.currencies.get(slotIndex);
                Enderpay.setPlayerStoreCurrency(player.getName(), selectedCurrency);

                player.closeInventory();
                player.sendMessage("");
                player.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + Enderpay.getStore().getName());
                player.sendMessage("");
                player.sendMessage(ChatColor.GRAY + "Your store currency has been changed to the " + ChatColor.LIGHT_PURPLE + selectedCurrency.getName() + ChatColor.GRAY + "!");
                player.sendMessage("");
                player.sendMessage("");

                HomeGui homeGui = new HomeGui();
                homeGui.openInventory(player);
            }

            if (slotIndex == backSlotIndex) {
                HomeGui homeGui = new HomeGui();
                homeGui.openInventory(player);
            }

        }

    }

}
