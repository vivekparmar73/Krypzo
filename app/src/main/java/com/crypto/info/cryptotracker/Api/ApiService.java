package com.crypto.info.cryptotracker.Api;

import com.crypto.info.cryptotracker.models.Coin;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("coins/markets")
    Call<List<Coin>> getMarketData(
            @Query("vs_currency") String currency,
            @Query("order") String order,
            @Query("per_page") int perPage,
            @Query("page") int page,
            @Query("sparkline") boolean sparkline
    );


}
