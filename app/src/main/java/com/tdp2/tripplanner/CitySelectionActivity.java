package com.tdp2.tripplanner;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.tdp2.tripplanner.citySelectionActivityExtras.CityAdapter;
import com.tdp2.tripplanner.citySelectionActivityExtras.RecyclerItemClickListener;
import com.tdp2.tripplanner.modelo.City;

import java.util.ArrayList;
import java.util.List;

public class CitySelectionActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private Toolbar toolbar;
    private CityAdapter adapter;
    private RecyclerView.LayoutManager lManager;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_selection);

        // Inicializar Ciudades esto se cambia por pegarle al API
        List items = new ArrayList();

        items.add(new City("Buenos Aires", "Argentina",123.56, 123.56));
        items.add(new City("Nueva York", "U.S.A",0.0, 0.0));
        items.add(new City("Moscu", "Rusia",0.0, 0.0));
        items.add(new City("San Pablo", "Brazil",123.56, 123.56));
        items.add(new City("Rio de Janeiro", "Brazil",0.0, 0.0));
        items.add(new City("Roma", "Italia",0.0, 0.0));

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
                        adapter.changeSelectedItem(position);
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
    }
}
