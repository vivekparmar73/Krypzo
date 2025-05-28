package com.crypto.info.cryptotracker.Api;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MarketChartResponse {

    @SerializedName("prices")
    private List<List<Double>> prices;

    public List<List<Double>> getPrices() {
        return prices;
    }

    public void setPrices(List<List<Double>> prices) {
        this.prices = prices;
    }
}
