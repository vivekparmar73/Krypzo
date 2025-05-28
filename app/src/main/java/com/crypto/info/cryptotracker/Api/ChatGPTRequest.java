package com.crypto.info.cryptotracker.Api;

import java.util.List;
import java.util.Map;

public class ChatGPTRequest {
    public String model;
    public List<Map<String, String>> messages;
    public double temperature;

    public ChatGPTRequest(List<Map<String, String>> messages) {
        this.model = "gpt-3.5-turbo";
        this.messages = messages;
        this.temperature = 0.7; // optional but recommended
    }
}
