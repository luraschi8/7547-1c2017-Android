package com.tdp2.tripplanner;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.MapFragment;
import com.tdp2.tripplanner.ToursExtrasd.AttractionInTourAdapter;
import com.tdp2.tripplanner.ToursExtrasd.MapTourHandler;
import com.tdp2.tripplanner.ToursExtrasd.TourDataHolder;
import com.tdp2.tripplanner.attractionSelectionActivityExtras.MapHandler;
import com.tdp2.tripplanner.dao.APIDAO;
import com.tdp2.tripplanner.helpers.LocaleHandler;
import com.tdp2.tripplanner.modelo.Attraction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.tdp2.tripplanner.helpers.LocationService.MY_PERMISSIONS_REQUEST_FINE_LOCATION;

public class AttractionsInTourActivity extends AppCompatActivity implements Response.Listener<JSONObject>,
        Response.ErrorListener {

    private boolean viewingMap, lazyMap;
    private View gridView, mapView;
    private int tourId;
    private String tourName;
    Toolbar toolbar;
    private RecyclerView recyclerView;
    private ArrayList attractionList;
    private AttractionInTourAdapter adapter;
    private ProgressBar progress;
    private ImageButton refreshButton;
    private MapTourHandler mMapHandler;
    private APIDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewingMap = false;
        lazyMap = false;
        gridView = getLayoutInflater().inflate(R.layout.activity_attractions_in_tour, null);
        mapView = getLayoutInflater().inflate(R.layout.attractions_map_view, null);
        setContentView(gridView);


        LocaleHandler.updateLocaleSettings(this.getBaseContext());

        tourId = TourDataHolder.getData().id;
        tourName = TourDataHolder.getData().name;

        toolbar = (Toolbar) findViewById(R.id.toolbar_attractions_in_tour);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(this.getString(R.string.attractions_in_tour) + " " + tourName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerAttractionsInTour);


        attractionList = new ArrayList<>();
        adapter = new AttractionInTourAdapter(this, attractionList);

        RecyclerView.LayoutManager mLayoutManager =  new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        //Obtener el progress bar
        progress = (ProgressBar) findViewById(R.id.progressBarAttractionsInTour);
        progress.setVisibility(View.VISIBLE);

        //Obtengo el refreshButton
        refreshButton = (ImageButton) findViewById(R.id.refreshAttractionsInTour);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshAttractions(tourId);
                refreshButton.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
            }
        });
        refreshButton.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        mMapHandler = new MapTourHandler(this);
        dao = new APIDAO();
        this.refreshAttractions(tourId);

    }

    private void refreshAttractions(int tourId) {
        this.dao.getAttractionsForTour(this.getApplicationContext(), this, this, tourId);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (this.viewingMap) {
            inflateMapToolBar(menu);
        } else {
            inflateGridToolBar(menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void inflateMapToolBar(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.attraction_in_tour_map_menu, menu);
    }

    public void inflateGridToolBar(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.attraction_in_tour_activity_menu, menu);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_map:
                action(R.string.map_view);
                return true;
            case R.id.action_change_grid:
                action(R.string.grid_view);
            default:
                return true;
        }
    }

    private void action(int resid) {
        switch (resid) {
            case R.string.map_view:
                this.viewingMap = true;
                this.setContentView(mapView);
                if (!this.lazyMap) initMapView();
                return;
            case R.string.grid_view:
                this.viewingMap = false;
                setContentView(gridView);
                return;
        }
    }

    private void initMapView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.map_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(tourName);
        setSupportActionBar(toolbar);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.attraction_map);
        mapFragment.getMapAsync(this.mMapHandler);
    }



    @Override
    public void onErrorResponse(VolleyError error) {
        if (!getResources().getBoolean(R.bool.mockUp)) {
            Log.e("ERROR RESPONSE", error.toString());
            Toast.makeText(this, R.string.no_internet_error, Toast.LENGTH_SHORT).show();
            progress.setVisibility(View.GONE);
            refreshButton.setVisibility(View.VISIBLE);
        }else {
            this.mockAttractions();
        }

    }

    private void mockAttractions() {
        ArrayList<Attraction> lista = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(getResources().getString(R.string.attractionsInTourMock));
            for (int i = 0; i < data.length(); i++) {
                JSONObject current = data.getJSONObject(i);
                lista.add(Attraction.buildFromJson(current));
            }
        } catch (JSONException e) {
            Log.e("ERROR JSON", e.getMessage());
            return;
        }
        this.adapter = new AttractionInTourAdapter(this, lista);
        this.recyclerView.setAdapter(this.adapter);
        this.attractionList = lista;
        this.mMapHandler.setList(lista);
        progress.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResponse(JSONObject response) {
        ArrayList<Attraction> lista = new ArrayList<>();
        try {
            JSONArray data = response.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject current = data.getJSONObject(i);
                lista.add(Attraction.buildFromJson(current));
            }
        } catch (JSONException e) {
            Log.e("ERROR JSON", e.getMessage());
            return;
        }
        this.adapter = new AttractionInTourAdapter(this, lista);
        this.recyclerView.setAdapter(this.adapter);
        this.attractionList = lista;
        this.mMapHandler.setList(lista);
        progress.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.mMapHandler.setLocationPermission(true);
                } else {
                    Toast.makeText(this, R.string.location_service_required,
                            Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
