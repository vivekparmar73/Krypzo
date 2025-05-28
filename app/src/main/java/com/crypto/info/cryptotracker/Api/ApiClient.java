package com.crypto.info.cryptotracker.Api;


import com.crypto.info.cryptotracker.BuildConfig;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import okhttp3.logging.HttpLoggingInterceptor;

public class ApiClient {
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/";
    private static GeminiApi geminiApi;

    public static GeminiApi getGeminiApi() {
        if (geminiApi == null) {
            // Create a logging interceptor
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY); // Log request and response bodies

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging) // Add the logging interceptor
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            geminiApi = retrofit.create(GeminiApi.class);
        }
        return geminiApi;
    }

    public static String getGeminiApiKey() {
        return BuildConfig.GEMINI_API_KEY;
    }
}