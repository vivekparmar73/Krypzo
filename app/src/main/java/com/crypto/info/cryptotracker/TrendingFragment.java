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
import com.crypto.info.cryptotracker.Api.TrendingResponse;
import com.crypto.info.cryptotracker.adapters.MarketAdapter;
import com.crypto.info.cryptotracker.models.Coin;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrendingFragment extends Fragment {

    private RecyclerView recyclerView;
    private ShimmerFrameLayout shimmerLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MarketAdapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trending, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewTrending);
        shimmerLayout = view.findViewById(R.id.shimmerLayout);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MarketAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(coin -> {
            // Same as MarketsFragment
            Intent intent = new Intent(getContext(), ChartActivity.class);
            intent.putExtra("coin_id", coin.getId());
            intent.putExtra("coin_name", coin.getName());
            intent.putExtra("coin_image", coin.getImage());
            startActivity(intent);
        });

        swipeRefreshLayout.setOnRefreshListener(this::fetchTrendingCoins);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );

        fetchTrendingCoins();

        return view;
    }

    private void fetchTrendingCoins() {
        if (!swipeRefreshLayout.isRefreshing()) {
            shimmerLayout.startShimmer();
            shimmerLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        RetrofitClient.getInstance().getTrendingCoins()
                .enqueue(new Callback<TrendingResponse>() {
                    @Override
                    public void onResponse(Call<TrendingResponse> call, Response<TrendingResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<TrendingResponse.TrendingCoinItem> trendingItems = response.body().getCoins();

                            if (trendingItems.isEmpty()) {
                                stopLoading();
                                showToast("No trending coins available");
                                return;
                            }

                            // Build comma-separated list of coin IDs
                            StringBuilder idsBuilder = new StringBuilder();
                            for (TrendingResponse.TrendingCoinItem item : trendingItems) {
                                idsBuilder.append(item.getCoin().getId()).append(",");
                            }
                            String ids = idsBuilder.substring(0, idsBuilder.length() - 1); // Remove last comma

                            // Now fetch detailed market data for these IDs
                            RetrofitClient.getInstance()
                                    .getMarketDataByIds("usd", ids, "market_cap_desc", trendingItems.size(), 1, false)
                                    .enqueue(new Callback<List<Coin>>() {
                                        @Override
                                        public void onResponse(Call<List<Coin>> call, Response<List<Coin>> response) {
                                            stopLoading();
                                            if (response.isSuccessful() && response.body() != null) {
                                                adapter.updateData(response.body());
                                            } else {
                                                showToast("Failed to load detailed market data");
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<List<Coin>> call, Throwable t) {
                                            stopLoading();
                                            showToast("Network error: " + t.getMessage());
                                        }
                                    });
                        } else {
                            stopLoading();
                            showToast("Failed to load trending coins");
                        }
                    }

                    @Override
                    public void onFailure(Call<TrendingResponse> call, Throwable t) {
                        stopLoading();
                        showToast("Network error: " + t.getMessage());
                    }
                });
    }

    private void stopLoading() {
        shimmerLayout.stopShimmer();
        shimmerLayout.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        recyclerView.setVisibility(View.VISIBLE);
    }


    private void showToast(String message) {
        if (isAdded()) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
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
