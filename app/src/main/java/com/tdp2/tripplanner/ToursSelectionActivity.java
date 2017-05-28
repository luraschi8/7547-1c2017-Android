package com.tdp2.tripplanner;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tdp2.tripplanner.ToursExtrasd.ToursAdapter;
import com.tdp2.tripplanner.attractionSelectionActivityExtras.AttractionAdapter;
import com.tdp2.tripplanner.attractionSelectionActivityExtras.MapHandler;
import com.tdp2.tripplanner.citySelectionActivityExtras.CityDataHolder;
import com.tdp2.tripplanner.dao.APIDAO;
import com.tdp2.tripplanner.helpers.LocaleHandler;
import com.tdp2.tripplanner.modelo.Attraction;
import com.tdp2.tripplanner.modelo.Tour;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ToursSelectionActivity extends AppCompatActivity implements Response.Listener<JSONObject>,
        Response.ErrorListener{

    private Integer cityId;
    private String cityName;
    private RecyclerView recyclerView;
    private ArrayList toursList;
    private APIDAO dao;
    private ToursAdapter adapter;
    private LinearLayoutManager mLayoutManager;
    private ProgressBar progressBar;
    private ImageButton refreshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tours_selection);

        LocaleHandler.updateLocaleSettings(this.getBaseContext());

        cityId = CityDataHolder.getData().getId();
        cityName = CityDataHolder.getData().getName();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_toursgrid);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(this.getString(R.string.tours_grid) + " " + cityName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.tours_recycler_view);

        toursList = new ArrayList<>();
        adapter = new ToursAdapter(toursList, this);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        progressBar = (ProgressBar) findViewById(R.id.progressBarTours);
        progressBar.setVisibility(View.VISIBLE);

        //Obtengo el refreshButton
        refreshButton = (ImageButton) findViewById(R.id.refreshButtonTours);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshTours(cityId);
                refreshButton.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        refreshButton.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        dao = new APIDAO();
        refreshTours(cityId);
    }

    public void refreshTours(Integer cityId){
        this.dao.getToursForCity(this.getApplicationContext(), this, this, cityId);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        /*Log.e("ERROR RESPONSE", error.toString());

        Toast.makeText(this, R.string.no_internet_error, Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
        refreshButton.setVisibility(View.VISIBLE);*/
        ArrayList<Tour> tours = new ArrayList<>();
        try {
            JSONArray data = new JSONArray("[{\"id\":1,\"nombre\":\"Recorrido de las mejores cafeterías de la ciudad\"},{\"id\":2,\"nombre\":\"Caminito\"}]");
            for (int i = 0; i < data.length(); i++) {
                JSONObject current = data.getJSONObject(i);
                tours.add(Tour.buildFromJson(current));
            }
        } catch (JSONException e) {
            Log.e("ERROR JSON", e.getMessage());
            return;
        }

        adapter = new ToursAdapter(tours, this);
        recyclerView.setAdapter(adapter);
        toursList = tours;
        progressBar.setVisibility(View.GONE);
        refreshButton.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResponse(JSONObject response) {
        ArrayList<Tour> tours = new ArrayList<>();
        try {
            JSONArray data = response.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject current = data.getJSONObject(i);
                tours.add(Tour.buildFromJson(current));
            }
        } catch (JSONException e) {
            Log.e("ERROR JSON", e.getMessage());
            return;
        }

        adapter = new ToursAdapter(tours, this);
        recyclerView.setAdapter(adapter);
        toursList = tours;
        progressBar.setVisibility(View.GONE);
        refreshButton.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }
}
