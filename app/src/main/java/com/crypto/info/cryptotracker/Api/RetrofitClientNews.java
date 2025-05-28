package com.crypto.info.cryptotracker.Api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientNews {
    private static final String BASE_URL = "https://newsapi.org/";
    private static Retrofit retrofit;

    public static NewsApi getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(NewsApi.class);
    }
}
