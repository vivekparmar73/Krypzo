package com.crypto.info.cryptotracker.adapters;

import android.content.Context;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crypto.info.cryptotracker.R;
import com.crypto.info.cryptotracker.models.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private final List<ChatMessage> messages;

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getSender();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = (viewType == ChatMessage.SENDER_USER) ? R.layout.item_user_message : R.layout.item_bot_message;
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage chatMessage = messages.get(position);
        String messageText = chatMessage.getMessage();

        // 1. Convert Markdown bold (**text**) to HTML bold (<b>text</b>)
        String formattedText = messageText.replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>");

        // 2. Convert double newlines (paragraph breaks) to HTML paragraph tags
        // This ensures proper paragraph separation
        formattedText = formattedText.replaceAll("\n\n", "<p>");

        // 3. Convert single newlines to HTML break tags
        // This handles simple line breaks within a paragraph or between list items if not already handled by <p>
        formattedText = formattedText.replaceAll("\n", "<br>");

        // 4. (Optional but recommended for lists) Convert basic Markdown list items to HTML list items
        // This regex looks for lines starting with a number and a dot (1.), or a hyphen (-)
        // and wraps the entire response in <ul> if lists are detected.
        // A more robust Markdown parser would be better for complex lists.
        if (formattedText.contains("- ") || formattedText.matches(".*\\d+\\.\\s.*")) {
            // Convert lines starting with '-' or '1.' etc. to <li> tags
            formattedText = formattedText.replaceAll("(?m)^\\s*-\\s*(.*)", "<li>$1</li>"); // For unordered lists
            formattedText = formattedText.replaceAll("(?m)^\\s*\\d+\\.\\s*(.*)", "<li>$1</li>"); // For ordered lists
            // Wrap in a <ul> tag if there are list items.
            // This is a simple approach; for mixed content, you might need a more advanced parser.
            if (formattedText.contains("<li>")) {
                formattedText = "<ul>" + formattedText + "</ul>";
            }
        }


        // Use Html.fromHtml to render HTML in TextView
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            holder.textView.setText(Html.fromHtml(formattedText, Html.FROM_HTML_MODE_COMPACT)); // Use COMPACT for better paragraph handling
        } else {
            holder.textView.setText(Html.fromHtml(formattedText));
        }

        // Set maxWidth dynamically to 75% of screen width
        Context context = holder.itemView.getContext();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int maxWidth = (int) (screenWidth * 0.75); // 75%
        holder.textView.setMaxWidth(maxWidth);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ChatViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.chatText);
        }
    }
}