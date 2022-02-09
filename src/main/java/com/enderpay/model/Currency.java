package com.enderpay.model;

public class Currency {

    private String symbol; // the currency symbol
    private String name; // the currency name
    private String iso4217; // the currency iso 4217
    private float rate; // the currency rate

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIso4217() {
        return iso4217;
    }

    public void setIso4217(String iso4217) {
        this.iso4217 = iso4217;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }
}
