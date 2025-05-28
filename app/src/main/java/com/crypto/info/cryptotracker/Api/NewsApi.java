package com.crypto.info.cryptotracker.Api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApi {
    @GET("v2/everything")
    Call<NewsResponse> getCryptoNews(
            @Query("q") String query,
            @Query("apiKey") String apiKey
    );
}
