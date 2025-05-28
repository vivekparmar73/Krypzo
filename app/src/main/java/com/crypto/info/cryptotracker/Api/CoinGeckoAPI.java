package com.crypto.info.cryptotracker.Api;

import com.crypto.info.cryptotracker.models.Coin;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CoinGeckoAPI {
    @GET("coins/markets")
    Call<List<Coin>> getMarketData(
            @Query("vs_currency") String currency,
            @Query("order") String order,
            @Query("per_page") int perPage,
            @Query("page") int page,
            @Query("sparkline") boolean sparkline

    );

    @GET("coins/{id}/market_chart")
    Call<MarketChartResponse> getMarketChart(
            @Path("id") String coinId,
            @Query("vs_currency") String vsCurrency,
            @Query("days") int days
    );

    // In your Retrofit API interface (e.g. ApiService.java)
    @GET("search/trending")
    Call<TrendingResponse> getTrendingCoins();

    @GET("coins/markets")
    Call<List<Coin>> getMarketDataByIds(
            @Query("vs_currency") String currency,
            @Query("ids") String ids,
            @Query("order") String order,
            @Query("per_page") int perPage,
            @Query("page") int page,
            @Query("sparkline") boolean sparkline
    );


}
