package com.crypto.info.cryptotracker.Api;

import java.util.List;

import java.util.List;

public class GeminiRequest {
    public List<Content> contents; // This is correct for Gemini

    public GeminiRequest(String userMessage) {
        this.contents = new java.util.ArrayList<>();
        this.contents.add(new Content(userMessage));
    }

    public static class Content {
        public List<Part> parts;

        public Content(String text) {
            this.parts = new java.util.ArrayList<>();
            this.parts.add(new Part(text));
        }
    }

    public static class Part {
        public String text;

        public Part(String text) {
            this.text = text;
        }
    }
}