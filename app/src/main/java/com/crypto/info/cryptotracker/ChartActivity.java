package com.crypto.info.cryptotracker;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.crypto.info.cryptotracker.Api.MarketChartResponse;
import com.crypto.info.cryptotracker.Api.RetrofitClient;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChartActivity extends AppCompatActivity {

    private LineChart lineChart;
    private ProgressBar progressBar;
    private TextView tvCurrentPrice, tvTimeRange;
    private String coinId;
    private String coinName;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.chartRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lineChart = findViewById(R.id.lineChart);
        progressBar = findViewById(R.id.progressBarChart);
        tvCurrentPrice = findViewById(R.id.tvCurrentPrice);
        tvTimeRange = findViewById(R.id.tvTimeRange);

        coinId = getIntent().getStringExtra("coin_id");
        coinName = getIntent().getStringExtra("coin_name");

        if (coinId == null || coinId.isEmpty()) {
            Toast.makeText(this, "Coin ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (coinName != null) {
            setTitle(coinName);
        }

        ImageView ivCoinIcon = findViewById(R.id.ivCoinIcon);
        TextView tvCoinName = findViewById(R.id.tvCoinName);

        tvCoinName.setText(coinName);

        String coinImage = getIntent().getStringExtra("coin_image");
        if (coinImage != null && !coinImage.isEmpty()) {
            Glide.with(this).load(coinImage).into(ivCoinIcon);
        } else {
            ivCoinIcon.setImageResource(R.drawable.ic_launcher_foreground); // default icon
        }


        setupChart();
        fetchChartData();
    }

    private void setupChart() {
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setBackgroundColor(Color.TRANSPARENT);
        lineChart.getLegend().setEnabled(false);
        lineChart.setExtraOffsets(20f, 20f, 20f, 20f);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new DateValueFormatter());
        xAxis.setLabelRotationAngle(-45f);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setAxisLineColor(Color.parseColor("#3A4F7D"));
        xAxis.setGridColor(Color.parseColor("#1A2A4A"));
        xAxis.setDrawGridLines(true);
        xAxis.setAvoidFirstLastClipping(true);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisLineColor(Color.parseColor("#3A4F7D"));
        leftAxis.setGridColor(Color.parseColor("#1A2A4A"));
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);

        lineChart.getAxisRight().setEnabled(false);
    }

    private void fetchChartData() {
        progressBar.setVisibility(View.VISIBLE);

        RetrofitClient.getInstance()
                .getMarketChart(coinId, "usd", 1)
                .enqueue(new Callback<MarketChartResponse>() {
                    @Override
                    public void onResponse(Call<MarketChartResponse> call, Response<MarketChartResponse> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            showChart(response.body());
                        } else {
                            showToast("Failed to load chart data");
                        }
                    }

                    @Override
                    public void onFailure(Call<MarketChartResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        showToast("Error: " + t.getMessage());
                    }
                });
    }

    private void showChart(MarketChartResponse data) {
        if (data.getPrices() == null || data.getPrices().isEmpty()) {
            showToast("No data available");
            return;
        }

        List<Entry> entries = new ArrayList<>();
        List<Float> prices = new ArrayList<>();

        for (List<Double> pricePoint : data.getPrices()) {
            long timestamp = pricePoint.get(0).longValue();
            float price = pricePoint.get(1).floatValue();
            prices.add(price);

            float xValue = timestamp;
            entries.add(new Entry(xValue, price));
        }

        float currentPrice = prices.get(prices.size() - 1);
        tvCurrentPrice.setText(String.format(Locale.getDefault(), "$%.2f", currentPrice));
        tvTimeRange.setText("1 Day");

        float firstPrice = prices.get(0);
        float lastPrice = prices.get(prices.size() - 1);
        int lineColor = lastPrice >= firstPrice ? Color.parseColor("#4CAF50") : Color.parseColor("#F44336");

        LineDataSet dataSet = new LineDataSet(entries, "Price (USD)");
        dataSet.setColor(lineColor);
        dataSet.setLineWidth(2.5f);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(lineColor);
        dataSet.setFillAlpha(30);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawHorizontalHighlightIndicator(false);
        dataSet.setHighLightColor(Color.WHITE);
        dataSet.setHighlightLineWidth(1f);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        lineChart.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.getDefault(), "$%.0fK", value / 1000);
            }
        });

        lineChart.invalidate();
        lineChart.animateX(1500);
    }

    private void showToast(String message) {
        Toast.makeText(ChartActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private static class DateValueFormatter extends ValueFormatter {
        private final SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

        @Override
        public String getFormattedValue(float value) {
            return mFormat.format(new Date((long) value));
        }
    }
}