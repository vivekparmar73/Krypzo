package com.crypto.info.cryptotracker.Api;

import java.util.List;

public class ChatGPTResponse {
    public List<Choice> choices;

    public static class Choice {
        public Message message;
    }

    public static class Message {
        public String role;
        public String content;
    }
}
