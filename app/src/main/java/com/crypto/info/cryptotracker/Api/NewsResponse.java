package com.crypto.info.cryptotracker.Api;

import com.crypto.info.cryptotracker.models.NewsArticle;

import java.util.List;

public class NewsResponse {
    private List<NewsArticle> articles;
    public List<NewsArticle> getArticles() { return articles; }
}
