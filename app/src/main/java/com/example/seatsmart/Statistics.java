package com.example.seatsmart;


import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.utility.DataRef.seatStatus;

public class Statistics extends AppCompatActivity {

    /* Referenced from https://github.com/PhilJay/MPAndroidChart */

    public class Formatter extends ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return "" + ((int) value);
        }
    }

    // UI reference
    private PieChart pieChart;
    private Button btnBack;
    private PieDataSet dataset;
    private List<PieEntry> entries;
    private PieData data;
    private final Handler handler = new Handler();

    // Float references
    private Float red = 0f;
    private Float yellow = 0f;
    private Float green = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);

        // Initialize UI
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pieChart = findViewById(R.id.piechart);

        getData();
        formatPieChart();
        pieChart.animateXY(2000, 2000);
        autoRefresh();
    }

    private final void autoRefresh(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshData();
                getData();
                formatPieChart();
                autoRefresh();
            }
        },2000); // refresh page every 2 seconds
    }

    private void refreshData(){
        red = 0f;
        yellow = 0f;
        green = 0f;
        entries.clear();
    }

    private void getData(){
        for (Map.Entry<String, Seat> entry: seatStatus.entrySet()){
            String color = entry.getValue().getStatus();
            if(color.equals("red")){
                red += 1;
            } else if(color.equals("green")) {
                green += 1;
            } else{
                yellow += 1;
            }
        }

        // Add entries
        entries = new ArrayList<>();
        entries.add(new PieEntry(red, "Unavailable"));
        entries.add(new PieEntry(yellow, "Temporarily Occupied"));
        entries.add(new PieEntry(green, "Available"));
        dataset = new PieDataSet(entries, "");

        // Setting dataset parameters
        dataset.setSliceSpace(10f);
        dataset.setSelectionShift(10f);
        dataset.setValueLinePart1OffsetPercentage(80f);
        dataset.setValueLinePart1Length(0.5f);
        dataset.setValueLinePart2Length(0.5f);
        dataset.setValueTypeface(Typeface.DEFAULT_BOLD);
        dataset.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        // Add colors to dataset
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.YELLOW);
        colors.add(Color.rgb(0,146,69));
        dataset.setColors(colors);

        // Display integer instead of float
        dataset.setValueFormatter(new Formatter());

        // Display data onto the piechart
        data = new PieData(dataset);
    }

    private void formatPieChart(){

        // Format piechart data
        data.setValueTextSize(30);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setData(data);
        pieChart.setCenterText(getResources().getString(R.string.app_name));
        pieChart.setCenterTextTypeface(Typeface.SANS_SERIF);
        pieChart.setCenterTextSize(25);

        // Format description
        Description description = pieChart.getDescription();
        description.setText(getResources().getString(R.string.occupanyStatus));
        description.setTypeface(Typeface.DEFAULT_BOLD);
        description.setPosition(200,30);
        description.setTextSize(50);
        pieChart.invalidate(); // refresh

        // Format legend
        Legend legend = pieChart.getLegend();
        legend.setFormSize(15f);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        legend.setTextSize(18f);
        legend.setTextColor(Color.BLACK);
        legend.setXEntrySpace(5f);
        legend.setYEntrySpace(5f);
    }
}
