package com.enderpay.papi;

import com.enderpay.Enderpay;
import com.enderpay.Plugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class EnderpayExpansion extends PlaceholderExpansion {

    private final Plugin plugin;

    public EnderpayExpansion(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "ep";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Rubics_";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {

        switch (identifier.toLowerCase()) {
            case "top_donator_1_username":
                return Enderpay.getFirstPlaceDonatorUsername();
            case "top_donator_1_total":
                return Enderpay.getFirstPlaceDonatorAmount();
            case "top_donator_2_username":
                return Enderpay.getSecondPlaceDonatorUsername();
            case "top_donator_2_total":
                return Enderpay.getSecondPlaceDonatorAmount();
            case "top_donator_3_username":
                return Enderpay.getThirdPlaceDonatorUsername();
            case "top_donator_3_total":
                return Enderpay.getThirdPlaceDonatorAmount();
            case "top_donator_day_username":
                return Enderpay.getDayDonatorUsername();
            case "top_donator_day_total":
                return Enderpay.getDayDonatorAmount();
            case "top_donator_week_username":
                return Enderpay.getWeekDonatorUsername();
            case "top_donator_week_total":
                return Enderpay.getWeekDonatorAmount();
            case "top_donator_month_username":
                return Enderpay.getMonthDonatorUsername();
            case "top_donator_month_total":
                return Enderpay.getMonthDonatorAmount();
            case "latest_donator_username":
                return Enderpay.getLatestDonatorUsername();
            default:
                return "";
        }
    }
}
