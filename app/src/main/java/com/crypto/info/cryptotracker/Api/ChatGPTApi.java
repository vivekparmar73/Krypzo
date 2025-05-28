package com.crypto.info.cryptotracker.Api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ChatGPTApi {
    @POST("v1/chat/completions")
    Call<ChatGPTResponse> getChatCompletion(@Body ChatGPTRequest request);
}
