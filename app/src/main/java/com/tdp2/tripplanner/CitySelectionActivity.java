package com.tdp2.tripplanner;



import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
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
import com.tdp2.tripplanner.citySelectionActivityExtras.CityAdapter;
import com.tdp2.tripplanner.citySelectionActivityExtras.CityDataHolder;
import com.tdp2.tripplanner.dao.APIDAO;
import com.tdp2.tripplanner.helpers.LocaleHandler;
import com.tdp2.tripplanner.helpers.LocationRequester;
import com.tdp2.tripplanner.helpers.LocationService;
import com.tdp2.tripplanner.modelo.City;
import com.tdp2.tripplanner.modelo.UserInstance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.tdp2.tripplanner.helpers.LocationService.MY_PERMISSIONS_REQUEST_FINE_LOCATION;

public class CitySelectionActivity extends AppCompatActivity
        implements Response.Listener<JSONObject>, Response.ErrorListener, LocationRequester{

    private CityAdapter adapter;
    private RecyclerView recycler;
    private ProgressBar progress;
    private ImageButton refreshButton;
    private APIDAO dao;
    private Boolean locationPermission;
    private LocationService locationService;
    private Integer locationUpdates = 0;
    FloatingActionButton locationButton;
    private String currentLanguage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_selection);

        this.currentLanguage = LocaleHandler.loadLanguageSelection(this.getBaseContext());
        LocaleHandler.updateLocaleSettings(this.getBaseContext());

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
        setSupportActionBar(toolbar);


        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.recycler);
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

        locationButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationUpdates = 0;
                initLocationServices();
            }
        });
        locationButton.setClickable(false);

        this.locationPermission = false;
    }

    public void refreshCities() {
        this.dao.getCities(this.getApplicationContext(), this, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.city_selection_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if(!getResources().getBoolean(R.bool.mockUp)) {
            Log.e("ERROR RESPONSE", error.toString());
            Toast.makeText(this, R.string.no_internet_error, Toast.LENGTH_SHORT).show();
            progress.setVisibility(View.GONE);
            refreshButton.setVisibility(View.VISIBLE);
        }else {
            this.mockCities();
        }

    }

    private void mockCities() {
        ArrayList<City> lista = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(getResources().getString(R.string.citiesMock));
            for (int i = 0; i < data.length(); i++) {
                JSONObject current = data.getJSONObject(i);
                if( current.get("image").toString().equals("null") )
                    lista.add(new City(current.getInt("id"), current.getString("nombre"), current.getString("pais"),
                            BitmapFactory.decodeResource(this.getResources(), R.drawable.buenos_aires_sample) ,
                            current.getDouble("latitud"), current.getDouble("longitud")));
                else {
                    byte[] img = Base64.decode(current.getString("image"), Base64.DEFAULT);
                    lista.add(new City(current.getInt("id"), current.getString("nombre"), current.getString("pais"),
                            BitmapFactory.decodeByteArray(img, 0, img.length),
                            current.getDouble("latitud"), current.getDouble("longitud")));
                }
            }
        } catch (JSONException e) {
            Log.e("ERROR JSON", e.getMessage());
            return;
        }
        progress.setVisibility(View.GONE);
        this.adapter = new CityAdapter(lista, this);
        this.recycler.setAdapter(this.adapter);
        locationButton.setClickable(true);
    }

    @Override
    public void onResponse(JSONObject response) {
        ArrayList<City> lista = new ArrayList<>();
        try {
            JSONArray data = response.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject current = data.getJSONObject(i);
                if( current.get("image").toString().equals("null") )
                    lista.add(new City(current.getInt("id"), current.getString("nombre"), current.getString("pais"),
                            BitmapFactory.decodeResource(this.getResources(), R.drawable.buenos_aires_sample) ,
                            current.getDouble("latitud"), current.getDouble("longitud")));
                else {
                    byte[] img = Base64.decode(current.getString("image"), Base64.DEFAULT);
                    lista.add(new City(current.getInt("id"), current.getString("nombre"), current.getString("pais"),
                            BitmapFactory.decodeByteArray(img, 0, img.length),
                            current.getDouble("latitud"), current.getDouble("longitud")));
                }
            }
        } catch (JSONException e) {
            Log.e("ERROR JSON", e.getMessage());
            return;
        }
        progress.setVisibility(View.GONE);
        this.adapter = new CityAdapter(lista, this);
        this.recycler.setAdapter(this.adapter);
        locationButton.setClickable(true);
    }


    public void checkForLocationPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
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
        locationUpdates++;
        if (locationUpdates > 1) return; //only one location is required.
        Integer pos = this.adapter.selectByLocation(location);
        final City detectada = this.adapter.getCityAtPosition(pos);
        float[] results = {0.0f};
        Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                detectada.getLatitude(), detectada.getLongitude(), results);
        if (results[0] > getResources().getInteger(R.integer.max_distance_meters)) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(R.string.no_city_detected);
            alert.setMessage(R.string.no_cities_msg);
            alert.setCancelable(false);
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            alert.show();
            locationButton.setClickable(true);
            return;
        }
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.city_detected);
        alert.setMessage(getResources().getString(R.string.detection_text) + detectada.getName() + "\n" +
                getResources().getString(R.string.see_attractions));
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent intent = new Intent(CitySelectionActivity.this, AttractionSelectionActivity.class);
                CityDataHolder.setData(detectada);
                CitySelectionActivity.this.startActivity(intent);
            }
        });

        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

        alert.show();
        locationButton.setClickable(true);
    }

    private void initLocationServices() {
        this.locationButton.setClickable(false);
        this.checkForLocationPermission();
        if (!this.locationPermission) {
            Toast.makeText(this, R.string.location_service_required, Toast.LENGTH_SHORT).show();
            this.locationButton.setClickable(true);
            return;
        }
        this.locationService = new LocationService(this, this);
        if (!locationService.isAvailable())
            Toast.makeText(this, R.string.no_location_service, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.settings_item:
                Intent intent = new Intent(this, SettingsActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.accounts_item:
                UserInstance.loginRedirect(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!this.currentLanguage.equals(LocaleHandler.loadLanguageSelection(this.getBaseContext()))) {
            this.recreate();
        }
    }
}
