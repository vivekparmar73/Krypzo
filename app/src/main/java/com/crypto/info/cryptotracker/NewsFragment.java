package com.crypto.info.cryptotracker;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.crypto.info.cryptotracker.Api.NewsApi;
import com.crypto.info.cryptotracker.Api.NewsResponse;
import com.crypto.info.cryptotracker.Api.RetrofitClientNews;
import com.crypto.info.cryptotracker.adapters.NewsAdapter;
import com.crypto.info.cryptotracker.models.NewsArticle;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NewsFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        recyclerView = view.findViewById(R.id.newsRecyclerView);
        progressBar = view.findViewById(R.id.newsProgressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchNews();
        return view;
    }
    private void fetchNews() {
        progressBar.setVisibility(View.VISIBLE);

        NewsApi api = RetrofitClientNews.getInstance();
        api.getCryptoNews("crypto OR nft", "a36cd3b5a33640a2ab4dd849a14be3fd").enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<NewsArticle> articles = response.body().getArticles();
                    recyclerView.setAdapter(new NewsAdapter(articles));
                } else {
                    Toast.makeText(getContext(), "Failed to load news", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
