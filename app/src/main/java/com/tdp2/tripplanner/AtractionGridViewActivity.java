package com.tdp2.tripplanner;

import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.MapFragment;
import com.tdp2.tripplanner.attractionSelectionActivityExtras.AttractionAdapter;
import com.tdp2.tripplanner.attractionSelectionActivityExtras.MapHandler;
import com.tdp2.tripplanner.citySelectionActivityExtras.CityDataHolder;
import com.tdp2.tripplanner.dao.APIDAO;
import com.tdp2.tripplanner.helpers.LocaleHandler;
import com.tdp2.tripplanner.modelo.Attraction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.tdp2.tripplanner.helpers.LocationService.MY_PERMISSIONS_REQUEST_FINE_LOCATION;


public class AtractionGridViewActivity extends AppCompatActivity implements Response.Listener<JSONObject>,
        Response.ErrorListener {

    private MapHandler mMapHandler;
    private AttractionAdapter adapter;
    private ArrayList<Attraction> attractionList;
    private View gridView, mapView;
    private Boolean viewingMap;
    private Boolean lazyMap;
    private APIDAO dao;
    private ProgressBar progress;
    private ImageButton refreshButton;
    private Integer cityId;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewingMap = false;
        lazyMap = false;
        gridView = getLayoutInflater().inflate(R.layout.activity_atraction_grid_view, null);
        mapView = getLayoutInflater().inflate(R.layout.attractions_map_view, null);
        setContentView(gridView);


        LocaleHandler.updateLocaleSettings(this.getBaseContext());

        cityId = CityDataHolder.getData().getId();
        String cityName = CityDataHolder.getData().getName();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(this.getString(R.string.atraction_grid) + " " + cityName);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.attraction_recycler_view);


        dao = new APIDAO();
        this.refreshAttractions(cityId);
        attractionList = new ArrayList<>();
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
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshAttractions(cityId);
                refreshButton.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
            }
        });
        refreshButton.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        this.mMapHandler = new MapHandler(this);
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
        mapFragment.getMapAsync(this.mMapHandler);
    }


    public void refreshAttractions(Integer cityId){
        this.dao.getAttractionForCity(this.getApplicationContext(), this, this, cityId);
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
        this.adapter = new AttractionAdapter(this, lista);
        this.recyclerView.setAdapter(this.adapter);
        this.attractionList = lista;
        this.mMapHandler.setList(lista);
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

