package com.tdp2.tripplanner;


import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tdp2.tripplanner.attractionSelectionActivityExtras.AttractionAdapter;
import com.tdp2.tripplanner.attractionSelectionActivityExtras.AttractionDataHolder;
import com.tdp2.tripplanner.citySelectionActivityExtras.CityDataHolder;
import com.tdp2.tripplanner.dao.APIDAO;
import com.tdp2.tripplanner.modelo.Attraction;
import com.tdp2.tripplanner.modelo.City;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class AtractionGridViewActivity extends AppCompatActivity implements OnMapReadyCallback,
        Response.Listener<JSONObject>, Response.ErrorListener, GoogleMap.OnInfoWindowClickListener{

    private AttractionAdapter adapter;
    private List<Attraction> attractionList;
    private View gridView, mapView;
    private Boolean viewingMap;
    private Boolean lazyMap;
    private Hashtable<String, Attraction> markers;
    private APIDAO dao;
    private ProgressBar progress;
    private ImageButton refreshButton;
    private Integer cityId;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewingMap = false;
        lazyMap = false;
        gridView = getLayoutInflater().inflate(R.layout.activity_atraction_grid_view, null);
        mapView = getLayoutInflater().inflate(R.layout.attractions_map_view, null);
        setContentView(gridView);

        cityId = CityDataHolder.getData().getId();
        String cityName = CityDataHolder.getData().getName();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(this.getString(R.string.atraction_grid) + " " + cityName);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.attraction_recycler_view);


        dao = new APIDAO();
        this.refreshAttractions(cityId);
        attractionList = new ArrayList<>();
        markers = new Hashtable<>();
        //TODO Change to json from API
        //prepareAttractions();
        adapter = new AttractionAdapter(this, attractionList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        //Obtener el progress bar
        progress = (ProgressBar) findViewById(R.id.progressBarAttractions);
        progress.setVisibility(View.VISIBLE);

        //Obtengo el refreshButton
        refreshButton = (ImageButton) findViewById(R.id.refreshButtonAttractions);
        refreshButton.setVisibility(View.GONE);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshAttractions(cityId);
                refreshButton.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
            }
        });



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
        inflater.inflate(R.menu.attraction_map_menu, menu);
    }

    public void inflateGridToolBar(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.attraction_activity_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);

        if (searchItem != null) {
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            EditText searchPlate = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            searchPlate.setHint("Search");
            searchPlate.setTextColor(Color.WHITE);
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
        }
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
        toolbar.setTitle(R.string.atraction_grid);
        setSupportActionBar(toolbar);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.attraction_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
        googleMap.setOnInfoWindowClickListener(this);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        Double sumLat = 0D, sumLong =0D;
        for (Attraction attraction : attractionList){
            sumLat = sumLat + attraction.getLatitude();
            sumLong = sumLong + attraction.getLongitude();
            LatLng currentLat = new LatLng(attraction.getLatitude(), attraction.getLongitude());
            final Marker currentMarker =  googleMap.addMarker(new MarkerOptions().position(currentLat).title(attraction.getName()));
            markers.put(currentMarker.getId(), attraction);
        }
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(sumLat / attractionList.size(), sumLong / attractionList.size()), 12.0f));
    }


    public void refreshAttractions(Integer cityId){
        this.dao.getAttractionForCity(this.getApplicationContext(), this, this, cityId);
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        final Attraction markedAttraction = markers.get(marker.getId());
        Intent intent = new Intent(AtractionGridViewActivity.this, AttractionDetailActivity.class);
        AttractionDataHolder.setData(markedAttraction);
        AtractionGridViewActivity.this.startActivity(intent);
    }

    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private View view;

        public CustomInfoWindowAdapter() {
            view = getLayoutInflater().inflate(R.layout.map_info_window, null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            AtractionGridViewActivity.this.marker = marker;

            final Attraction markedAttraction = markers.get(marker.getId());

            view = getLayoutInflater().inflate(R.layout.map_info_window, null);

            //Get map badge
            ImageView img = (ImageView) view.findViewById(R.id.map_badge);
            img.setImageBitmap(markedAttraction.getMainImage());

            //Get view title
            TextView title = (TextView) view.findViewById(R.id.map_title);
            title.setText(markedAttraction.getName());

            return view;
        }

        @Override
        public View getInfoWindow(final Marker marker) {
            return null;
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e("ERROR RESPONSE", error.toString());
        Toast.makeText(this, R.string.no_internet_error, Toast.LENGTH_SHORT).show();
        progress.setVisibility(View.GONE);
        refreshButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResponse(JSONObject response) {
        ArrayList<Attraction> lista = new ArrayList<>();
        try {
            JSONArray data = response.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject current = data.getJSONObject(i);
                if( current.get("imagen").toString().equals("null") )
                    lista.add(new Attraction(current.getInt("id"), current.getString("nombre"), current.getString("descripcion"),
                            current.getDouble("latitud"), current.getDouble("longitud"),
                            BitmapFactory.decodeResource(this.getResources(), R.drawable.colon_sample)));
                else {
                    byte[] img = Base64.decode(current.getString("imagen"), Base64.DEFAULT);
                    lista.add(new Attraction(current.getInt("id"), current.getString("nombre"), current.getString("descripcion"),
                            current.getDouble("latitud"), current.getDouble("longitud"),
                            BitmapFactory.decodeByteArray(img, 0, img.length)));
                }
            }
        } catch (JSONException e) {
            Log.e("ERROR JSON", e.getMessage());
            return;
        }
        progress.setVisibility(View.GONE);
        this.adapter.setList(lista);
        this.attractionList = lista;
        this.adapter.notifyDataSetChanged();
    }
}

