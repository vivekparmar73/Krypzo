package com.crypto.info.cryptotracker.Api;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface GeminiApi {
    // --- CHANGE THIS LINE ---
    // Replace 'gemini-1.0-pro' with the exact model name you found that supports 'generateContent'
    @POST("v1beta/models/gemini-1.5-flash-latest:generateContent") // <-- Example: Use a model from your ListModels output
    Call<GeminiResponse> generateContent(
            @Body GeminiRequest request,
            @Query("key") String apiKey
    );
}