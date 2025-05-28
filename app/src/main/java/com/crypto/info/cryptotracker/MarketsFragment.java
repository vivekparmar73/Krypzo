package com.crypto.info.cryptotracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.crypto.info.cryptotracker.Api.RetrofitClient;
import com.crypto.info.cryptotracker.adapters.MarketAdapter;
import com.crypto.info.cryptotracker.models.Coin;
import com.facebook.shimmer.ShimmerFrameLayout;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MarketsFragment extends Fragment {
    private RecyclerView recyclerView;
    private ShimmerFrameLayout shimmerLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MarketAdapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_markets, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewMarkets);
        shimmerLayout = view.findViewById(R.id.shimmerLayout);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MarketAdapter();
        recyclerView.setAdapter(adapter);

        // Setup click listener
        adapter.setOnItemClickListener(coin -> {
            Intent intent = new Intent(getContext(), ChartActivity.class);
            intent.putExtra("coin_id", coin.getId());
            intent.putExtra("coin_name", coin.getName());
            intent.putExtra("coin_image", coin.getImage());
            startActivity(intent);
        });

        // Setup swipe to refresh
        swipeRefreshLayout.setOnRefreshListener(this::fetchCoinData);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );

        // Initial data load
        fetchCoinData();
        return view;
    }

    private void fetchCoinData() {
        // Show loading state
        if (!swipeRefreshLayout.isRefreshing()) {
            shimmerLayout.startShimmer();
            shimmerLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        RetrofitClient.getInstance().getMarketData("usd", "market_cap_desc", 50, 1, false)
                .enqueue(new Callback<List<Coin>>() {
                    @Override
                    public void onResponse(Call<List<Coin>> call, Response<List<Coin>> response) {
                        // Hide loading states
                        shimmerLayout.stopShimmer();
                        shimmerLayout.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        recyclerView.setVisibility(View.VISIBLE);

                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().isEmpty()) {
                                showToast("No data available");
                            } else {
                                adapter.updateData(response.body());
                            }
                        } else {
                            showError("Failed to load data: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Coin>> call, Throwable t) {
                        // Hide loading states
                        shimmerLayout.stopShimmer();
                        shimmerLayout.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        recyclerView.setVisibility(View.VISIBLE);

                        showError("Network error: " + t.getMessage());
                    }
                });
    }

    private void showToast(String message) {
        if (isAdded()) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void showError(String message) {
        if (isAdded()) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        shimmerLayout.startShimmer();
    }

    @Override
    public void onPause() {
        shimmerLayout.stopShimmer();
        super.onPause();
    }
}