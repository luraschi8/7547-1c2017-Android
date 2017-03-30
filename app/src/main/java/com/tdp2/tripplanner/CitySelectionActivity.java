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
import android.view.View;
import android.widget.Toast;

import com.tdp2.tripplanner.citySelectionActivityExtras.CityAdapter;
import com.tdp2.tripplanner.citySelectionActivityExtras.RecyclerItemClickListener;
import com.tdp2.tripplanner.helpers.LocationRequester;
import com.tdp2.tripplanner.helpers.LocationService;
import com.tdp2.tripplanner.modelo.City;

import java.util.ArrayList;
import java.util.List;

import static com.tdp2.tripplanner.helpers.LocationService.MY_PERMISSIONS_REQUEST_FINE_LOCATION;

public class CitySelectionActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private Toolbar toolbar;
    private CityAdapter adapter;
    private RecyclerView.LayoutManager lManager;
    private SearchView searchView;
    //LocationService locationService;
    //private Boolean locationPermission;
    //private Integer locationsReceived;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_selection);

        //Inicio el contador de updates de ubicacion
        //this.locationsReceived = 0;

        // Inicializar Ciudades esto se cambia por pegarle al API
        List items = new ArrayList();

        items.add(new City("Buenos Aires", "Argentina", R.drawable.buenos_aires_sample,-34.609438, -58.434704));
        items.add(new City("Nueva York", "U.S.A", R.drawable.buenos_aires_sample,40.76164, -73.982131));
        items.add(new City("Moscu", "Rusia", R.drawable.buenos_aires_sample,55.755247, 37.620386));
        items.add(new City("San Pablo", "Brazil", R.drawable.buenos_aires_sample,123.56, 123.56));
        items.add(new City("Rio de Janeiro", "Brazil", R.drawable.buenos_aires_sample,-23.547914, 46.633069));
        items.add(new City("Roma", "Italia", R.drawable.buenos_aires_sample,41.901736, 12.497607));

        //Obtener el toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Trips");
        toolbar.setSubtitle(R.string.select_city);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setTitleTextColor(Color.WHITE);


        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent intent = new Intent(getBaseContext(), AtractionGridViewActivity.class);
                        intent.putExtra("EXTRA_CITY_SELECTED", adapter.getCityAtPosition(position).getId());
                        startActivity(intent);
                    }
                })
        );

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        // Crear un nuevo adaptador
        adapter = new CityAdapter(items);
        recycler.setAdapter(adapter);

        //Obtener el searchview
        searchView = (SearchView) findViewById(R.id.search_view);
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

        //this.locationPermission = false;
        //this.checkForLocationPermission();
        //if (this.locationPermission) this.initLocationServices();
    }

/*
    public void checkForLocationPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        }
        else this.locationPermission = true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.initLocationServices();

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

    @Override
    public void updateLocation(Location location) {
        this.locationsReceived++;
        if (this.locationsReceived > 2) {
            this.locationService.stopLocationServices();
            return;
        }
        this.adapter.selectByLocation(location);
    }

    private void initLocationServices() {
        this.locationService = new LocationService(this, 0L, 60000L, this);

        if (!locationService.isAvailable())
            Toast.makeText(this, R.string.no_location_service,
                    Toast.LENGTH_SHORT).show();
    }

*/

}
