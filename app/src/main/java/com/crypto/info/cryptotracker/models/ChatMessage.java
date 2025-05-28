package com.crypto.info.cryptotracker.models;

public class ChatMessage {
    public static final int SENDER_USER = 0;
    public static final int SENDER_BOT = 1;

    private final String message;
    private final int sender;

    public ChatMessage(String message, int sender) {
        this.message = message;
        this.sender = sender;
    }

    public String getMessage() { return message; }
    public int getSender() { return sender; }
}
