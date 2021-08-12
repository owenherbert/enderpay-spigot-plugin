package com.enderpay.model;

public class ItemDescription {

    private String id; // the item id
    private String name; // the item name
    private int quantity; // the item quantity
    private String lore; // the item lore
    private boolean isEnchanted; // if the item is enchanted

    public String getId() {
        return id;
    }

    public ItemDescription setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public ItemDescription setName(String name) {
        this.name = name;
        return this;
    }

    public int getQuantity() {
        return quantity;
    }

    public ItemDescription setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public String getLore() {
        return lore;
    }

    public ItemDescription setLore(String lore) {
        this.lore = lore;
        return this;
    }

    public boolean isEnchanted() {
        return isEnchanted;
    }

    public ItemDescription setEnchanted(boolean enchanted) {
        isEnchanted = enchanted;
        return this;
    }
}
