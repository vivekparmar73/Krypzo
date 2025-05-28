package com.crypto.info.cryptotracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.crypto.info.cryptotracker.R;
import com.crypto.info.cryptotracker.models.Coin;

import java.util.ArrayList;
import java.util.List;

public class MarketAdapter extends RecyclerView.Adapter<MarketAdapter.ViewHolder> {

    // Interface for click listener
    public interface OnItemClickListener {
        void onItemClick(Coin coin);
    }

    private final List<Coin> coins;
    private OnItemClickListener listener;

    public MarketAdapter() {
        this.coins = new ArrayList<>();
    }

    public MarketAdapter(List<Coin> coins) {
        this.coins = new ArrayList<>(coins);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<Coin> newCoins) {
        coins.clear();
        coins.addAll(newCoins);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.coin_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Coin coin = coins.get(position);

        holder.coinName.setText(coin.getName());
        holder.coinSymbol.setText(coin.getSymbol().toUpperCase());
        holder.coinPrice.setText("$" + String.format("%.2f", coin.getCurrent_price()));

        double change = coin.getPrice_change_percentage_24h();
        holder.coinChange.setText(String.format("%.2f%%", change));

        int color = ContextCompat.getColor(
                holder.itemView.getContext(),
                change >= 0 ? R.color.green : R.color.red
        );
        holder.coinChange.setTextColor(color);

        Glide.with(holder.itemView.getContext())
                .load(coin.getImage())
                .into(holder.coinImage);

        // Set click listener on the whole item view
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(coin);
            }
        });
    }

    @Override
    public int getItemCount() {
        return coins.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView coinName, coinSymbol, coinPrice, coinChange;
        ImageView coinImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            coinName = itemView.findViewById(R.id.coinName);
            coinSymbol = itemView.findViewById(R.id.coinSymbol);
            coinPrice = itemView.findViewById(R.id.coinPrice);
            coinChange = itemView.findViewById(R.id.coinChange);
            coinImage = itemView.findViewById(R.id.coinImage);
        }
    }
}
