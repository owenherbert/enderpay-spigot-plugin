package com.enderpay;

import com.enderpay.api.EnderpayApi;
import com.enderpay.gui.CategoryGui;
import com.enderpay.gui.HomeGui;
import com.enderpay.gui.PageGui;
import com.enderpay.model.Package;
import com.enderpay.model.*;
import com.enderpay.utils.UuidConverter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Enderpay {

    private static Plugin plugin; // the plugin

    private static HomeGui homeGui; // the home GUI
    private static PageGui pageGui; // the page GUI
    private static HashMap<Integer, CategoryGui> categoryGuiHashMap = new HashMap<>(); // a hashmap of category GUIs

    private static boolean isLoaded = false; // if models and GUIs have been loaded

    private static ArrayList<Category> categories; // an array list of category models
    private static ArrayList<Package> packages; // an array list of package models
    private static ArrayList<Page> pages; // an array list of page models
    private static Store store; // the store model
    private static Currency currency; // the currency model

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

        Bukkit.getScheduler().runTaskAsynchronously(Enderpay.getPlugin(), () -> {

            try {

                EnderpayApi enderpayApi = new EnderpayApi();

                JSONObject cmdQueueApiResponse = enderpayApi.makeRequest(EnderpayApi.ENDPOINT_PLUGIN_COMMAND_QUEUE, EnderpayApi.METHOD_GET, null);

                JSONArray queueItems = cmdQueueApiResponse.getJSONObject("data").getJSONArray("queue");

                MessageBroadcaster.toConsole("Found " + queueItems.length() +" pending commands to execute!");

                JSONArray completedCommandIds = new JSONArray();

                for (int i = 0; i < queueItems.length(); i++) {

                    try {

                        JSONObject queueItem = queueItems.getJSONObject(i);

                        int commandId = queueItem.getJSONObject("command").getInt("id");
                        String commandValue = queueItem.getJSONObject("command").getString("value");

                        if (queueItem.isNull("customer") || queueItem.isNull("conditions")) {

                            // the queue item has no conditions/customer so it can be executed immediately.
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), commandValue);

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
                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), commandValue);

                            } else {

                                // if the player is not required online the command can be executed immediately
                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), commandValue);
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
                    enderpayApi.makeRequest(EnderpayApi.ENDPOINT_PLUGIN_COMMAND_QUEUE, EnderpayApi.METHOD_PUT, jsonRequestBody);

                }

            } catch (IOException exception) {
                MessageBroadcaster.toConsole("An error occurred while communicating with the Enderpay API: " + exception.getMessage());
            } catch (Exception exception) {
                MessageBroadcaster.toConsole("An error occurred while trying to check for pending commands.");
            }

        });
    }

    public static void buildModelsAndGuis() {

        Bukkit.getScheduler().runTaskAsynchronously(Enderpay.getPlugin(), () -> {

            try {

                // create new array list objects
                categories = new ArrayList<>();
                packages = new ArrayList<>();
                pages = new ArrayList<>();

                EnderpayApi enderpayApi = new EnderpayApi();

                // get responses from database
                try {

                    JSONObject storeApiResponse = enderpayApi.makeRequest(EnderpayApi.ENDPOINT_PLUGIN_STORE_GET, EnderpayApi.METHOD_GET, null);
                    JSONObject listingApiResponse = enderpayApi.makeRequest(EnderpayApi.ENDPOINT_PLUGIN_LISTING_GET, EnderpayApi.METHOD_GET, null);

                    // set store
                    JSONObject storeJsonData = storeApiResponse.getJSONObject("data").getJSONObject("store");

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

                    // set packages
                    JSONArray packagesJsonData = listingApiResponse.getJSONObject("data").getJSONArray("packages");
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
                    JSONArray categoriesJsonData = listingApiResponse.getJSONObject("data").getJSONArray("categories");
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
                    JSONArray pagesJsonData = listingApiResponse.getJSONObject("data").getJSONArray("pages");
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

                    isLoaded = true;

                    homeGui = new HomeGui();
                    pageGui = new PageGui();

                    for (Category category : categories) {
                        categoryGuiHashMap.put(category.getId(), new CategoryGui(category.getId()));
                    }

                } catch (IOException exception) {
                    MessageBroadcaster.toConsole("An error occurred while communicating with the Enderpay API: " + exception.getMessage());
                }

            } catch (Exception exception) {
                MessageBroadcaster.toConsole("An error occurred while trying to build store GUIs!");
            }

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
}
