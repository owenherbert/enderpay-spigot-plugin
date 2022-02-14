package com.enderpay.config;

import com.enderpay.Enderpay;

public class Config {

    public static String API_KEY = "api-key";
    public static String API_SECRET = "api-secret";
    public static String BUY_CMD_FIREWORK = "buy-command-firework";
    public static String DEBUG_MODE = "debug-mode";
    public static String GUI_TITLE_CURRENCY = "gui-title-currency";
    public static String GUI_TITLE_DONATION_PARTIES = "gui-title-donation-parties";
    public static String GUI_TITLE_DONATORS = "gui-title-donators";
    public static String GUI_TITLE_PAGES = "gui-title-pages";

    public static String getApiKey() {
        return Enderpay.getPlugin().getConfig().getString(API_KEY);
    }

    public static String getApiSecret() {
        return Enderpay.getPlugin().getConfig().getString(API_SECRET);
    }

    public static Boolean getBuyCommandFireworkEnabled() {
        return Enderpay.getPlugin().getConfig().getBoolean(BUY_CMD_FIREWORK);
    }

    public static Boolean getDebugModeEnabled() {
        return Enderpay.getPlugin().getConfig().getBoolean(DEBUG_MODE);
    }

    public static String getGuiTitleCurrency() {
        return Enderpay.getPlugin().getConfig().getString(GUI_TITLE_CURRENCY);
    }

    public static String getGuiTitleDonationParties() {
        return Enderpay.getPlugin().getConfig().getString(GUI_TITLE_DONATION_PARTIES);
    }

    public static String getGuiTitleDonators() {
        return Enderpay.getPlugin().getConfig().getString(GUI_TITLE_DONATORS);
    }

    public static String getGuiTitlePages() {
        return Enderpay.getPlugin().getConfig().getString(GUI_TITLE_PAGES);
    }

}
