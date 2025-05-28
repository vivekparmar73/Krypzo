package com.crypto.info.cryptotracker.Api;

// TrendingResponse.java

import com.crypto.info.cryptotracker.models.Coin;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TrendingResponse {
    @SerializedName("coins")
    private List<TrendingCoinItem> coins;

    public List<TrendingCoinItem> getCoins() {
        return coins;
    }

    public static class TrendingCoinItem {
        @SerializedName("item")
        private Coin coin;

        public Coin getCoin() {
            return coin;
        }
    }
}
