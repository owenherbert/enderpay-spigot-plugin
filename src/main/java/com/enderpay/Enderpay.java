package com.enderpay;

import com.enderpay.api.EnderpayApi;
import com.enderpay.gui.CategoryGui;
import com.enderpay.gui.DonatorsGui;
import com.enderpay.gui.HomeGui;
import com.enderpay.gui.PageGui;
import com.enderpay.model.Package;
import com.enderpay.model.*;
import com.enderpay.papi.EnderpayExpansion;
import com.enderpay.utils.UuidConverter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Enderpay {

    private static Plugin plugin; // the plugin

    private static HomeGui homeGui; // the home GUI
    private static PageGui pageGui; // the page GUI
    private static DonatorsGui donatorsGui; // the donator GUI
    private static HashMap<Integer, CategoryGui> categoryGuiHashMap = new HashMap<>(); // a hashmap of category GUIs

    private static boolean isLoaded = false; // if models and GUIs have been loaded

    private static ArrayList<Category> categories; // an array list of category models
    private static ArrayList<Package> packages; // an array list of package models
    private static ArrayList<Page> pages; // an array list of page models
    private static Store store; // the store model
    private static Currency currency; // the currency model

    // donator usernames
    private static String latestDonatorUsername = ""; // the username of the latest donator
    private static String firstPlaceDonatorUsername = ""; // the username of the donator in first place
    private static String secondPlaceDonatorUsername = ""; // the username of the donator in second place
    private static String thirdPlaceDonatorUsername = ""; // the username of the donator in third place
    private static String dayDonatorUsername = ""; // the username of the donator that has donated the most today
    private static String weekDonatorUsername = ""; // the username of the donator that has donated the most this week
    private static String monthDonatorUsername = ""; // the username of the donator that has donated the most this month

    private static String firstPlaceDonatorUuid = ""; // the uuid of the donator in first place
    private static String secondPlaceDonatorUuid = ""; // the uuid of the donator in second place
    private static String thirdPlaceDonatorUuid = ""; // the uuid of the donator in third place

    private static String firstPlaceDonatorAmount = "";
    private static String secondPlaceDonatorAmount = "";
    private static String thirdPlaceDonatorAmount = "";
    private static String dayDonatorAmount = "";
    private static String weekDonatorAmount = "";
    private static String monthDonatorAmount = "";

    public static ArrayList<Package> getPackagesWithCategoryId(int categoryId) {

        ArrayList<Package> categoryPackages = new ArrayList<>();

        for (Package pckg : packages) {
            if (pckg.getCategoryId() == categoryId) categoryPackages.add(pckg);
        }

        return categoryPackages;
    }

    public static Category getCategoryWithCategoryId(int categoryId) {

        for (Category category : categories) {
            if (category.getId() == categoryId) return category;
        }

        return null;
    }

    public static void checkForNewCommands() {

        EnderpayApi enderpayApi = new EnderpayApi();

        enderpayApi.makeRequestAsync(EnderpayApi.ENDPOINT_PLUGIN_COMMAND_QUEUE, EnderpayApi.METHOD_GET, null, jsonObject -> {

            JSONArray queueItems = jsonObject.getJSONObject("data").getJSONArray("queue");

            MessageBroadcaster.toConsole("Found " + queueItems.length() +" pending commands to execute!");

            JSONArray completedCommandIds = new JSONArray();

            for (int i = 0; i < queueItems.length(); i++) {

                try {

                    JSONObject queueItem = queueItems.getJSONObject(i);

                    int commandId = queueItem.getJSONObject("command").getInt("id");
                    String commandValue = queueItem.getJSONObject("command").getString("value");

                    if (queueItem.isNull("customer") || queueItem.isNull("conditions")) {

                        // execute the command if it meets the specified conditions
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Enderpay.getPlugin(), () -> {
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), commandValue);
                        });

                    } else {

                        String playerUndashedUuid = queueItem.getJSONObject("customer").getString("uuid");
                        boolean requirePlayerOnline = queueItem.getJSONObject("conditions").getBoolean("requirePlayerOnline");

                        UUID uuid = UUID.fromString(UuidConverter.insertDashUUID(playerUndashedUuid));

                        if (requirePlayerOnline) {

                            // is the player online?
                            Player player = Bukkit.getServer().getPlayer(uuid);

                            if (player == null) continue;

                            // is the a slot requirement?
                            int requiredSlots = queueItem.getJSONObject("conditions").getInt("requiredSlots");

                            int availableSlots = 0;
                            for (int j = 0; j < (9*4); j++) {

                                ItemStack itemStack = player.getInventory().getItem(j);
                                if (itemStack == null || itemStack.getData().getItemType() == Material.AIR) {
                                    availableSlots++;
                                }
                            }

                            // send message to player
                            if (availableSlots < requiredSlots) {
                                player.sendMessage("");
                                player.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + Enderpay.getStore().getName());
                                player.sendMessage("");
                                player.sendMessage(ChatColor.GRAY + "We tried adding items to your inventory but you don't ");
                                player.sendMessage(ChatColor.GRAY + "have enough space. Please clear " + ChatColor.LIGHT_PURPLE + requiredSlots + ChatColor.GRAY + " slots, and we'll try");
                                player.sendMessage(ChatColor.GRAY + "again shortly.");
                                player.sendMessage("");

                                continue;
                            }

                            // execute the command if it meets the specified conditions
                            Bukkit.getScheduler().scheduleSyncDelayedTask(Enderpay.getPlugin(), () -> {
                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), commandValue);
                            });

                        } else {

                            // execute the command if it meets the specified conditions
                            Bukkit.getScheduler().scheduleSyncDelayedTask(Enderpay.getPlugin(), () -> {
                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), commandValue);
                            });
                        }
                    }

                    completedCommandIds.put(commandId);

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

            // inform the API that the commands have been completed if the collection is empty
            if (!completedCommandIds.isEmpty()) {

                JSONObject jsonRequestBody = new JSONObject();
                jsonRequestBody.put("ids", completedCommandIds);

                MessageBroadcaster.toConsole("Completed " + completedCommandIds.length() +" commands!");

                // send the command ids back to the API to be marked as complete
                enderpayApi.makeRequestAsync(EnderpayApi.ENDPOINT_PLUGIN_COMMAND_QUEUE, EnderpayApi.METHOD_PUT, jsonRequestBody, jsonObject1 -> {

                });
            }
        });
    }

    public static void buildModelsAndGuis() {

        // create new array list objects
        categories = new ArrayList<>();
        packages = new ArrayList<>();
        pages = new ArrayList<>();

        EnderpayApi enderpayApi = new EnderpayApi();

        enderpayApi.makeRequestAsync(EnderpayApi.ENDPOINT_PLUGIN_STORE_GET, EnderpayApi.METHOD_GET, null, jsonObject -> {

            // set store
            JSONObject storeJsonData = jsonObject.getJSONObject("data").getJSONObject("store");

            store = new Store();
            store.setId(storeJsonData.getInt("id"));
            store.setName(storeJsonData.getString("name"));
            store.setDescription(!storeJsonData.isNull("description") ? storeJsonData.getString("description") : null);
            store.setDomain(storeJsonData.getString("domain"));

            // set currency
            JSONObject currencyJsonData = storeJsonData.getJSONObject("currency");

            currency = new Currency();
            currency.setSymbol(currencyJsonData.getString("symbol"));
            currency.setName(currencyJsonData.getString("currency"));
            currency.setIso4217(currencyJsonData.getString("iso4217"));

            enderpayApi.makeRequestAsync(EnderpayApi.ENDPOINT_PLUGIN_LISTING_GET, EnderpayApi.METHOD_GET, null, jsonObject1 -> {

                // set packages
                JSONArray packagesJsonData = jsonObject1.getJSONObject("data").getJSONArray("packages");
                for (int i = 0; i < packagesJsonData.length(); i++) {

                    JSONObject packageJsonData = packagesJsonData.getJSONObject(i);
                    JSONObject packageItemJsonObject = packageJsonData.getJSONObject("item");

                    Package pckg = new Package();
                    pckg.setId(packageJsonData.getInt("id"));
                    pckg.setName(packageJsonData.getString("name"));
                    pckg.setCategoryId(packageJsonData.getInt("categoryId"));
                    pckg.setPrice(packageJsonData.getDouble("price"));
                    pckg.setLink(packageJsonData.getString("link"));
                    pckg.setItemDescription(
                            new ItemDescription()
                                    .setId(packageItemJsonObject.getString("id"))
                                    .setName(packageItemJsonObject.getString("name"))
                                    .setQuantity(packageItemJsonObject.getInt("quantity"))
                                    .setLore(!packageItemJsonObject.isNull("lore") ? packageItemJsonObject.getString("lore") : null)
                                    .setEnchanted(packageItemJsonObject.getBoolean("isEnchanted"))
                    );

                    packages.add(pckg); // add package object to array list
                }

                // set categories
                JSONArray categoriesJsonData = jsonObject1.getJSONObject("data").getJSONArray("categories");
                for (int i = 0; i < categoriesJsonData.length(); i++) {

                    JSONObject categoryJsonObject = categoriesJsonData.getJSONObject(i);
                    JSONObject categoryItemJsonObject = categoryJsonObject.getJSONObject("item");

                    Category category = new Category();
                    category.setId(categoryJsonObject.getInt("id"));
                    category.setName(categoryJsonObject.getString("name"));
                    category.setOrderMethod(categoryJsonObject.getString("orderBy"));
                    category.setLink(categoryJsonObject.getString("link"));
                    category.setItemDescription(
                            new ItemDescription()
                                    .setId(categoryItemJsonObject.getString("id"))
                                    .setName(categoryItemJsonObject.getString("name"))
                                    .setQuantity(categoryItemJsonObject.getInt("quantity"))
                                    .setLore(!categoryItemJsonObject.isNull("lore") ? categoryItemJsonObject.getString("lore") : null)
                                    .setEnchanted(categoryItemJsonObject.getBoolean("isEnchanted"))
                    );

                    categories.add(category); // add category object to array list
                }

                // set pages
                JSONArray pagesJsonData = jsonObject1.getJSONObject("data").getJSONArray("pages");
                for (int i = 0; i < pagesJsonData.length(); i++) {

                    JSONObject pageJsonObject = pagesJsonData.getJSONObject(i);
                    JSONObject pageItemJsonObject = pageJsonObject.getJSONObject("item");

                    Page page = new Page();
                    page.setId(pageJsonObject.getInt("id"));
                    page.setName(pageJsonObject.getString("name"));
                    page.setLink(pageJsonObject.getString("link"));
                    page.setItemDescription(
                            new ItemDescription()
                                    .setId(pageItemJsonObject.getString("id"))
                                    .setName(pageItemJsonObject.getString("name"))
                                    .setQuantity(pageItemJsonObject.getInt("quantity"))
                                    .setLore(!pageItemJsonObject.isNull("lore") ? pageItemJsonObject.getString("lore") : null)
                                    .setEnchanted(pageItemJsonObject.getBoolean("isEnchanted"))
                    );

                    pages.add(page); // add page object to array list
                }

                // build GUIs
                homeGui = new HomeGui();
                pageGui = new PageGui();

                for (Category category : categories) {
                    categoryGuiHashMap.put(category.getId(), new CategoryGui(category.getId()));
                }

                enderpayApi.makeRequestAsync(EnderpayApi.ENDPOINT_PLUGIN_DONATORS_GET, EnderpayApi.METHOD_GET, null, jsonObject2 -> {

                    // set top donators
                    JSONArray topDonators = jsonObject2.getJSONObject("data").getJSONArray("topDonators");
                    if (!topDonators.isEmpty()) {

                        for (int i = 0; i < topDonators.length(); i++) {
                            JSONObject donator = topDonators.getJSONObject(i);

                            switch (donator.getInt("rank")) {
                                case 1:
                                    Enderpay.setFirstPlaceDonatorUsername(donator.getJSONObject("customer").getString("username"));
                                    Enderpay.setFirstPlaceDonatorUuid(donator.getJSONObject("customer").getString("uuid"));
                                    Enderpay.setFirstPlaceDonatorAmount(donator.getString("total"));
                                    break;
                                case 2:
                                    Enderpay.setSecondPlaceDonatorUsername(donator.getJSONObject("customer").getString("username"));
                                    Enderpay.setSecondPlaceDonatorUuid(donator.getJSONObject("customer").getString("uuid"));
                                    Enderpay.setSecondPlaceDonatorAmount(donator.getString("total"));
                                    break;
                                case 3:
                                    Enderpay.setThirdPlaceDonatorUsername(donator.getJSONObject("customer").getString("username"));
                                    Enderpay.setThirdPlaceDonatorUuid(donator.getJSONObject("customer").getString("uuid"));
                                    Enderpay.setThirdPlaceDonatorAmount(donator.getString("total"));
                                    break;
                            }
                        }
                    }

                    // set latest donator
                    if (!jsonObject2.getJSONObject("data").isNull("latestDonator")) {

                        JSONObject latestDonator = jsonObject2.getJSONObject("data").getJSONObject("latestDonator");

                        Enderpay.setLatestDonatorUsername(
                                latestDonator.getJSONObject("customer").getString("username")
                        );
                    }

                    // set day top donator
                    if (!jsonObject2.getJSONObject("data").isNull("dayTopDonator")) {

                        JSONObject dayTopDonator = jsonObject2.getJSONObject("data").getJSONObject("dayTopDonator");

                        Enderpay.setDayDonatorUsername(
                                dayTopDonator.getJSONObject("customer").getString("username")
                        );

                        Enderpay.setDayDonatorAmount(dayTopDonator.getString("total"));
                    }

                    // set week top donator
                    if (!jsonObject2.getJSONObject("data").isNull("weekTopDonator")) {

                        JSONObject weekTopDonator = jsonObject2.getJSONObject("data").getJSONObject("weekTopDonator");

                        Enderpay.setWeekDonatorUsername(
                                weekTopDonator.getJSONObject("customer").getString("username")
                        );

                        Enderpay.setWeekDonatorAmount(weekTopDonator.getString("total"));
                    }

                    // set month top donator
                    if (!jsonObject2.getJSONObject("data").isNull("monthTopDonator")) {

                        JSONObject monthTopDonator = jsonObject2.getJSONObject("data").getJSONObject("monthTopDonator");

                        Enderpay.setMonthDonatorUsername(
                                monthTopDonator.getJSONObject("customer").getString("username")
                        );

                        Enderpay.setMonthDonatorAmount(monthTopDonator.getString("total"));
                    }

                    // register Placeholder API expansion
                    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                        new EnderpayExpansion(plugin).register();
                    }

                    donatorsGui = new DonatorsGui();

                    isLoaded = true;
                });
            });
        });
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public static void setPlugin(Plugin plugin) {
        Enderpay.plugin = plugin;
    }

    public static HomeGui getHomeGui() {
        return homeGui;
    }

    public static void setHomeGui(HomeGui homeGui) {
        Enderpay.homeGui = homeGui;
    }

    public static PageGui getPageGui() {
        return pageGui;
    }

    public static void setPageGui(PageGui pageGui) {
        Enderpay.pageGui = pageGui;
    }

    public static DonatorsGui getDonatorsGui() {
        return donatorsGui;
    }

    public static void setDonatorsGui(DonatorsGui donatorsGui) {
        Enderpay.donatorsGui = donatorsGui;
    }

    public static HashMap<Integer, CategoryGui> getCategoryGuiHashMap() {
        return categoryGuiHashMap;
    }

    public static void setCategoryGuiHashMap(HashMap<Integer, CategoryGui> categoryGuiHashMap) {
        Enderpay.categoryGuiHashMap = categoryGuiHashMap;
    }

    public static boolean isLoaded() {
        return isLoaded;
    }

    public static void setIsLoaded(boolean isLoaded) {
        Enderpay.isLoaded = isLoaded;
    }

    public static ArrayList<Category> getCategories() {
        return categories;
    }

    public static void setCategories(ArrayList<Category> categories) {
        Enderpay.categories = categories;
    }

    public static ArrayList<Package> getPackages() {
        return packages;
    }

    public static void setPackages(ArrayList<Package> packages) {
        Enderpay.packages = packages;
    }

    public static ArrayList<Page> getPages() {
        return pages;
    }

    public static void setPages(ArrayList<Page> pages) {
        Enderpay.pages = pages;
    }

    public static Store getStore() {
        return store;
    }

    public static void setStore(Store store) {
        Enderpay.store = store;
    }

    public static Currency getCurrency() {
        return currency;
    }

    public static void setCurrency(Currency currency) {
        Enderpay.currency = currency;
    }

    public static String getLatestDonatorUsername() {
        return latestDonatorUsername;
    }

    public static void setLatestDonatorUsername(String latestDonatorUsername) {
        Enderpay.latestDonatorUsername = latestDonatorUsername;
    }

    public static String getFirstPlaceDonatorUsername() {
        return firstPlaceDonatorUsername;
    }

    public static void setFirstPlaceDonatorUsername(String firstPlaceDonatorUsername) {
        Enderpay.firstPlaceDonatorUsername = firstPlaceDonatorUsername;
    }

    public static String getSecondPlaceDonatorUsername() {
        return secondPlaceDonatorUsername;
    }

    public static void setSecondPlaceDonatorUsername(String secondPlaceDonatorUsername) {
        Enderpay.secondPlaceDonatorUsername = secondPlaceDonatorUsername;
    }

    public static String getThirdPlaceDonatorUsername() {
        return thirdPlaceDonatorUsername;
    }

    public static void setThirdPlaceDonatorUsername(String thirdPlaceDonatorUsername) {
        Enderpay.thirdPlaceDonatorUsername = thirdPlaceDonatorUsername;
    }

    public static String getDayDonatorUsername() {
        return dayDonatorUsername;
    }

    public static void setDayDonatorUsername(String dayDonatorUsername) {
        Enderpay.dayDonatorUsername = dayDonatorUsername;
    }

    public static String getWeekDonatorUsername() {
        return weekDonatorUsername;
    }

    public static void setWeekDonatorUsername(String weekDonatorUsername) {
        Enderpay.weekDonatorUsername = weekDonatorUsername;
    }

    public static String getMonthDonatorUsername() {
        return monthDonatorUsername;
    }

    public static void setMonthDonatorUsername(String monthDonatorUsername) {
        Enderpay.monthDonatorUsername = monthDonatorUsername;
    }

    public static String getFirstPlaceDonatorAmount() {
        return firstPlaceDonatorAmount;
    }

    public static void setFirstPlaceDonatorAmount(String firstPlaceDonatorAmount) {
        Enderpay.firstPlaceDonatorAmount = firstPlaceDonatorAmount;
    }

    public static String getSecondPlaceDonatorAmount() {
        return secondPlaceDonatorAmount;
    }

    public static void setSecondPlaceDonatorAmount(String secondPlaceDonatorAmount) {
        Enderpay.secondPlaceDonatorAmount = secondPlaceDonatorAmount;
    }

    public static String getThirdPlaceDonatorAmount() {
        return thirdPlaceDonatorAmount;
    }

    public static void setThirdPlaceDonatorAmount(String thirdPlaceDonatorAmount) {
        Enderpay.thirdPlaceDonatorAmount = thirdPlaceDonatorAmount;
    }

    public static String getDayDonatorAmount() {
        return dayDonatorAmount;
    }

    public static void setDayDonatorAmount(String dayDonatorAmount) {
        Enderpay.dayDonatorAmount = dayDonatorAmount;
    }

    public static String getWeekDonatorAmount() {
        return weekDonatorAmount;
    }

    public static void setWeekDonatorAmount(String weekDonatorAmount) {
        Enderpay.weekDonatorAmount = weekDonatorAmount;
    }

    public static String getMonthDonatorAmount() {
        return monthDonatorAmount;
    }

    public static void setMonthDonatorAmount(String monthDonatorAmount) {
        Enderpay.monthDonatorAmount = monthDonatorAmount;
    }

    public static String getFirstPlaceDonatorUuid() {
        return firstPlaceDonatorUuid;
    }

    public static void setFirstPlaceDonatorUuid(String firstPlaceDonatorUuid) {
        Enderpay.firstPlaceDonatorUuid = firstPlaceDonatorUuid;
    }

    public static String getSecondPlaceDonatorUuid() {
        return secondPlaceDonatorUuid;
    }

    public static void setSecondPlaceDonatorUuid(String secondPlaceDonatorUuid) {
        Enderpay.secondPlaceDonatorUuid = secondPlaceDonatorUuid;
    }

    public static String getThirdPlaceDonatorUuid() {
        return thirdPlaceDonatorUuid;
    }

    public static void setThirdPlaceDonatorUuid(String thirdPlaceDonatorUuid) {
        Enderpay.thirdPlaceDonatorUuid = thirdPlaceDonatorUuid;
    }
}
