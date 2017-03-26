package com.tdp2.tripplanner;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.tdp2.tripplanner.AttractionSelectionActivityExtras.AttractionAdapter;
import com.tdp2.tripplanner.modelo.Attraction;

import java.util.ArrayList;
import java.util.List;

public class AtractionGridViewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AttractionAdapter adapter;
    private List<Attraction> attractionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atraction_grid_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        //initCollapsingToolbar();

        recyclerView = (RecyclerView) findViewById(R.id.attraction_recycler_view);

        attractionList = new ArrayList<>();
        //TODO Change to json from API
        prepareAttractions();
        adapter = new AttractionAdapter(this, attractionList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        //TODO Change to json from API
        prepareAttractions();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_map:
                action(R.string.map_view);
                return true;
            default:
                return true;
        }
    }

    private void action(int resid) {
        Toast.makeText(this, getText(resid), Toast.LENGTH_SHORT).show();
    }

    /*

    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }
*/
    /**
     * Adding few attractions for testing
     */
    private void prepareAttractions() {
        Attraction attraction1 = new Attraction("Planetario", null, "Texto de prueba", 0D, 0D, R.drawable.planetario_sample);
        attractionList.add(attraction1);

        Attraction attraction2 = new Attraction("Teatro Colon", null, "Texto de prueba", 0D, 0D, R.drawable.colon_sample);
        attractionList.add(attraction2);

        Attraction attraction3 = new Attraction("Planetario", null, "Texto de prueba", 0D, 0D, R.drawable.planetario_sample);
        attractionList.add(attraction3);

        Attraction attraction4 = new Attraction("Teatro Colon", null, "Texto de prueba", 0D, 0D, R.drawable.colon_sample);
        attractionList.add(attraction4);

        Attraction attraction5 = new Attraction("Planetario", null, "Texto de prueba", 0D, 0D, R.drawable.planetario_sample);
        attractionList.add(attraction5);

        Attraction attraction6 = new Attraction("Planetario", null, "Texto de prueba", 0D, 0D, R.drawable.planetario_sample);
        attractionList.add(attraction6);

        Attraction attraction7 = new Attraction("Teatro Colon", null, "Texto de prueba", 0D, 0D, R.drawable.colon_sample);
        attractionList.add(attraction7);

        Attraction attraction8 = new Attraction("Planetario", null, "Texto de prueba", 0D, 0D, R.drawable.planetario_sample);
        attractionList.add(attraction8);

        Attraction attraction9 = new Attraction("Teatro Colon", null, "Texto de prueba", 0D, 0D, R.drawable.colon_sample);
        attractionList.add(attraction9);

        Attraction attraction10 = new Attraction("Planetario", null, "Texto de prueba", 0D, 0D, R.drawable.planetario_sample);
        attractionList.add(attraction10);
    }

}

