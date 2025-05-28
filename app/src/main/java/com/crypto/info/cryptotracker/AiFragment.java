package com.crypto.info.cryptotracker;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log; // Import for Log

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crypto.info.cryptotracker.Api.ApiClient;
import com.crypto.info.cryptotracker.Api.GeminiApi; // Changed from ChatGPTApi
import com.crypto.info.cryptotracker.Api.GeminiRequest; // Changed from ChatGPTRequest
import com.crypto.info.cryptotracker.Api.GeminiResponse; // Changed from ChatGPTResponse
import com.crypto.info.cryptotracker.adapters.ChatAdapter;
import com.crypto.info.cryptotracker.models.ChatMessage;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AiFragment extends Fragment {
    private RecyclerView chatRecyclerView;
    private EditText editTextMessage;
    private ImageView sendButton;
    private LinearLayout suggestionsLayout;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;

    private final String[] suggestionQuestions = {
            "What is Bitcoin?",
            "Is Ethereum better than Solana?",
            "NFTs: Are they still worth it?",
            "What are meme coins?",
            "How to invest in crypto safely?"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ai, container, false);

        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        editTextMessage = view.findViewById(R.id.editTextMessage);
        sendButton = view.findViewById(R.id.sendButton);
        suggestionsLayout = view.findViewById(R.id.suggestionsLayout);

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatRecyclerView.setAdapter(chatAdapter);

        // Suggestion chips
        for (String question : suggestionQuestions) {
            addSuggestionChip(question);
        }

        sendButton.setOnClickListener(v -> {
            String message = editTextMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                sendUserMessage(message);
                editTextMessage.setText("");
            }
        });

        return view;
    }

    private void addSuggestionChip(String text) {
        TextView chip = new TextView(getContext());
        chip.setText(text);
        chip.setTextColor(Color.WHITE);
        chip.setBackgroundResource(R.drawable.card_bg_dark); // create rounded bg
        chip.setPadding(24, 12, 24, 12);
        chip.setTextSize(13);
        chip.setTypeface(Typeface.DEFAULT_BOLD);
        chip.setClickable(true);
        chip.setOnClickListener(v -> sendUserMessage(text));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 0, 10, 0);
        suggestionsLayout.addView(chip, lp);
    }

    private void sendUserMessage(String question) {
        chatMessages.add(new ChatMessage(question, ChatMessage.SENDER_USER));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        chatRecyclerView.scrollToPosition(chatMessages.size() - 1);

        // Show placeholder for bot
        chatMessages.add(new ChatMessage("Typing...", ChatMessage.SENDER_BOT));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        chatRecyclerView.scrollToPosition(chatMessages.size() - 1);

        getGeminiResponse(question); // Changed method call
    }

    private void getGeminiResponse(String userMessage) {
        // Step 1: Create the GeminiRequest object
        // The GeminiRequest constructor should handle the 'contents' structure
        GeminiRequest request = new GeminiRequest(userMessage);

        // Step 2: Get the Gemini API service and API key
        GeminiApi api = ApiClient.getGeminiApi();
        String apiKey = ApiClient.getGeminiApiKey();

        // Step 3: Make the API call
        api.generateContent(request, apiKey).enqueue(new Callback<GeminiResponse>() {
            @Override
            public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String reply = "No response.";
                    if (response.body().candidates != null && !response.body().candidates.isEmpty()) {
                        GeminiResponse.Content content = response.body().candidates.get(0).content;
                        if (content != null && content.parts != null && !content.parts.isEmpty()) {
                            reply = content.parts.get(0).text.trim();
                        }
                    }
                    updateBotMessage(reply);
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e("Gemini", "Error response: " + errorBody);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    updateBotMessage("Sorry, I couldn't get a response from Gemini.");
                }
            }

            @Override
            public void onFailure(Call<GeminiResponse> call, Throwable t) {
                Log.e("Gemini", "API call failure", t);
                updateBotMessage("Failed to connect to Gemini AI.");
            }
        });
    }


    private void updateBotMessage(String message) {
        int lastIndex = chatMessages.size() - 1;
        // Ensure the last message is indeed the bot's placeholder before updating
        if (lastIndex >= 0 && chatMessages.get(lastIndex).getSender() == ChatMessage.SENDER_BOT) {
            chatMessages.set(lastIndex, new ChatMessage(message, ChatMessage.SENDER_BOT));
            chatAdapter.notifyItemChanged(lastIndex);
            chatRecyclerView.scrollToPosition(lastIndex);
        } else {
            // This case should ideally not happen if logic is correct, but as a fallback:
            chatMessages.add(new ChatMessage(message, ChatMessage.SENDER_BOT));
            chatAdapter.notifyItemInserted(chatMessages.size() - 1);
            chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
        }
    }
}