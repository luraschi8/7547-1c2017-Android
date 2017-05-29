package com.tdp2.tripplanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import com.tdp2.tripplanner.citySelectionActivityExtras.CityDataHolder;
import com.tdp2.tripplanner.modelo.UserInstance;

public class CityMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(CityDataHolder.getData().getName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        configMenuItems();
    }

    private void configMenuItems(){
        RelativeLayout attractions = (RelativeLayout) findViewById(R.id.menu_attraction_card_bg);
        attractions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CityMenuActivity.this, AttractionSelectionActivity.class);
                CityMenuActivity.this.startActivity(intent);
            }
        });


        RelativeLayout tours = (RelativeLayout) findViewById(R.id.menu_tours_card_bg);
        tours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CityMenuActivity.this, ToursSelectionActivity.class);
                CityMenuActivity.this.startActivity(intent);
            }
        });
        //TODO Agregar aca la activity de favoritas y de recorridos.

        CardView cardView = (CardView) findViewById(R.id.favorite_attraction_card);
        if (UserInstance.getInstance(this) == null) cardView.setVisibility(View.GONE);
    }

}
