package com.crypto.info.cryptotracker.models;


public class Coin {
    private String id;
    private String symbol;
    private String name;
    private String image;
    private double current_price;
    private double price_change_percentage_24h;

    // Getters
    public String getId() { return id; }
    public String getSymbol() { return symbol.toUpperCase(); }
    public String getName() { return name; }
    public String getImage() { return image; }
    public double getCurrent_price() { return current_price; }
    public double getPrice_change_percentage_24h() { return price_change_percentage_24h; }
}
