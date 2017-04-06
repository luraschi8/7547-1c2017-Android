package com.tdp2.tripplanner;


import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tdp2.tripplanner.citySelectionActivityExtras.CityAdapter;
import com.tdp2.tripplanner.citySelectionActivityExtras.RecyclerItemClickListener;
import com.tdp2.tripplanner.dao.CityDAO;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_selection);

        //Inicio el contador de updates de ubicacion
        //this.locationsReceived = 0;

        CityDAO dao = new CityDAO();
        dao.getCities(this.getApplicationContext(), this, this);

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
        recycler.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(getBaseContext(), AtractionGridViewActivity.class);
                        intent.putExtra("EXTRA_CITY_SELECTED", adapter.getCityAtPosition(position).getId());
                        startActivity(intent);
                    }
                })
        );

        // Usar un administrador para LinearLayout
        RecyclerView.LayoutManager lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        // Crear un nuevo adaptador
        adapter = new CityAdapter(items);
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
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e("ERROR RESPONSE", error.getMessage());
        Toast.makeText(this,"Error getting cities.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        ArrayList<City> lista = new ArrayList<>();
        try {
            JSONArray data = response.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject current = data.getJSONObject(i);
                if( current.get("imagen").toString().equals("null") )
                    lista.add(new City(current.getInt("id"), current.getString("nombre"), current.getString("pais"),
                            BitmapFactory.decodeResource(this.getResources(), R.drawable.buenos_aires_sample) ,
                            current.getDouble("latitud"), current.getDouble("longitud")));
                else {
                    String img = current.getString("imagen");
                    lista.add(new City(current.getInt("id"), current.getString("nombre"), current.getString("pais"),
                            BitmapFactory.decodeByteArray(img.getBytes(), 0, img.getBytes().length),
                            current.getDouble("latitud"), current.getDouble("longitud")));
                }
            }
        } catch (JSONException e) {
            Log.e("ERROR JSON", e.getMessage());
            return;
        }
        progress.setVisibility(View.GONE);
        this.adapter.setList(lista);
        this.adapter.notifyDataSetChanged();
    }
}
