package com.enderpay.gui;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.enderpay.Enderpay;
import com.enderpay.config.Config;
import com.enderpay.model.DonationParty;
import com.enderpay.utils.DateTimeHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class DonationPartyGui extends BaseGui implements Listener {

    private final ArrayList<DonationParty> donationParties;
    private final int backSlotIndex;

    public DonationPartyGui() {

        Enderpay.getPlugin().getServer().getPluginManager().registerEvents(this, Enderpay.getPlugin());

        this.donationParties = Enderpay.getDonationParties();

        double donationPartyCount = donationParties.size();
        int donationPartyRowCount = (int) Math.ceil(donationPartyCount / 9);

        int totalSlots = (donationPartyRowCount + 1) * 9;

        this.backSlotIndex = totalSlots - 1;

        String inventoryName = Enderpay.getStore().getName() + " » " + Config.getGuiTitleDonationParties();

        super.inventory = Bukkit.createInventory(null, totalSlots, inventoryName);

        super.inventory.clear();

        fillItems(totalSlots);

    }

    private void fillItems(int totalSlots) {

        for (int i = 0; i < this.donationParties.size(); i++) {

            DonationParty donationParty = this.donationParties.get(i);

            String percentageComplete = String.format("%.2f", donationParty.getPercentageComplete());

            ItemStack itemStack = createGuiItem(XMaterial.FIREWORK_ROCKET.parseMaterial(),
                    "&f" + donationParty.getName() + " &7(&d" + percentageComplete  + "%&7)",
                    1,
                    false,
                    true,
                    " ",
                    "&dStarted: &f" + DateTimeHelper.timeUntil(donationParty.getStartedAtIso8601()),
                    "&dEnds: &f" + DateTimeHelper.timeUntil(donationParty.getEndsAtIso8601())
            );

            super.inventory.addItem(itemStack);

        }

        // add glass panes to the GUI
        for (int i = 0; i < 8; i++) {

            int itemIndex = totalSlots - i - 1 - 1;

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

    };

}
