package com.tdp2.tripplanner;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tdp2.tripplanner.citySelectionActivityExtras.CityAdapter;
import com.tdp2.tripplanner.citySelectionActivityExtras.RecyclerItemClickListener;
import com.tdp2.tripplanner.helpers.LocationRequester;
import com.tdp2.tripplanner.helpers.LocationService;
import com.tdp2.tripplanner.modelo.City;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CitySelectionActivity extends AppCompatActivity
        implements Response.Listener<JSONObject>, Response.ErrorListener{

    private CityAdapter adapter;
    private ProgressBar progress;
    private ImageButton refreshButton;
    private APIDAO dao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_selection);

        //Inicio el contador de updates de ubicacion
        //this.locationsReceived = 0;

        dao = new APIDAO();
        this.refreshCities();

        // Inicializar Ciudades esto se cambia por pegarle al API
        List items = new ArrayList();

        //Obtener el toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Trips");
        toolbar.setSubtitle(R.string.select_city);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setTitleTextColor(Color.WHITE);


        // Obtener el Recycler
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        RecyclerView.LayoutManager lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        // Crear un nuevo adaptador
        adapter = new CityAdapter(items, this);
        recycler.setAdapter(adapter);

        //Obtener el searchview
        SearchView searchView = (SearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filterList(newText);
                return true;
            }
        });

        //Obtener el progress bar
        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setVisibility(View.VISIBLE);

        //Obtengo el refreshButton
        refreshButton = (ImageButton) findViewById(R.id.refreshButton);
        refreshButton.setVisibility(View.GONE);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshCities();
                refreshButton.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
            }
        });
    }

    public void refreshCities() {
        this.dao.getCities(this.getApplicationContext(), this, this);
    }

    @Override
    public void updateLocation(Location location) {
        this.locationsReceived++;
        if (this.locationsReceived > 2) {
            this.locationService.stopLocationServices();
            return;
        }
        progress.setVisibility(View.GONE);
        this.adapter.setList(lista);
        this.adapter.notifyDataSetChanged();
    }
}
