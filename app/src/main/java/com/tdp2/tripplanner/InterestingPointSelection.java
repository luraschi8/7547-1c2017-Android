package com.tdp2.tripplanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.MapFragment;
import com.tdp2.tripplanner.AudioActivityExtras.InterestingPointAdapter;
import com.tdp2.tripplanner.ToursExtrasd.AttractionInTourAdapter;
import com.tdp2.tripplanner.attractionSelectionActivityExtras.AttractionAdapter;
import com.tdp2.tripplanner.attractionSelectionActivityExtras.AttractionDataHolder;
import com.tdp2.tripplanner.dao.APIDAO;
import com.tdp2.tripplanner.helpers.LocaleHandler;
import com.tdp2.tripplanner.modelo.Attraction;
import com.tdp2.tripplanner.modelo.InterestingPoint;
import com.tdp2.tripplanner.modelo.UserInstance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class InterestingPointSelection extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {

    APIDAO dao;
    Toolbar toolbar;
    RecyclerView recycler;
    private Attraction attraction;
    private Boolean viewingMap;
    private Boolean lazyMap;
    InterestingPointAdapter adapter;
    ProgressBar progress;
    private View gridView, mapView;
    private int attractionId;
    ImageButton refreshButton;
    ImageView plano_image;
    private String attractionName;
    private ArrayList interestingPointsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewingMap = false;
        lazyMap = false;
        gridView = getLayoutInflater().inflate(R.layout.activity_interesting_point_selection, null);
        mapView = getLayoutInflater().inflate(R.layout.activity_plano_atraccion, null);
        setContentView(gridView);

        LocaleHandler.updateLocaleSettings(this.getBaseContext());

        attractionId = AttractionDataHolder.getData().getId();
        attractionName = AttractionDataHolder.getData().getName();

        toolbar = (Toolbar) findViewById(R.id.toolbar_ipgrid);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(this.getString(R.string.interesting_point_in) + " " + attractionName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recycler = (RecyclerView) findViewById(R.id.recycler_interestingPoint);

        interestingPointsList = new ArrayList<>();
        adapter = new InterestingPointAdapter(interestingPointsList,this);

        RecyclerView.LayoutManager mLayoutManager =  new LinearLayoutManager(this);
        recycler.setLayoutManager(mLayoutManager);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(adapter);

        //Obtener el progress bar
        progress = (ProgressBar) findViewById(R.id.progressBar_interestingPoint);
        progress.setVisibility(View.VISIBLE);

        //Obtengo el refreshButton
        refreshButton = (ImageButton) findViewById(R.id.refreshButton_interestingPoint);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshInterestingPoints(attractionId);
                refreshButton.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
            }
        });

        refreshButton.setVisibility(View.GONE);
        recycler.setVisibility(View.GONE);
        dao = new APIDAO();
        this.refreshInterestingPoints(attractionId);
    }

  /*  private void creation() {
        viewingMap = false;
        lazyMap = false;
        gridView = getLayoutInflater().inflate(R.layout.activity_interesting_point_selection, null);
        mapView = getLayoutInflater().inflate(R.layout.activity_plano_atraccion, null);
        setContentView(R.layout.activity_interesting_point_selection);
        attractionId = AttractionDataHolder.getData().getId();

        attraction = AttractionDataHolder.getData();


        LocaleHandler.updateLocaleSettings(this.getBaseContext());

        dao = new APIDAO();


        // Inicializar Ciudades esto se cambia por pegarle al API
        List items = new ArrayList();

        configToolBar();
        configRecycler();

        configAdapter(items);
        configProgressBar();
        configRefreshButton();

        refreshInterestingPoints();
    }*/

    private void refreshInterestingPoints(int attractionId) {
        this.dao.getInterestingPointsForAttraction(this.getApplicationContext(), this, this, attractionId);
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

    public void inflateMapToolBar(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.interesting_point, menu);
    }

    public void inflateGridToolBar(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.interesting_point_grid, menu);
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_plano);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(getResources().getString(R.string.interesting_point_in) + " " + attractionName);
        setSupportActionBar(toolbar);
        plano_image = (ImageView) findViewById(R.id.plano_image);

        try {
            new InterestingPointSelection.DownloadImageTask(plano_image)
                    .execute(AttractionDataHolder.getData().getPlanoURL());
        }catch (Exception ex) {
            Toast.makeText(this, R.string.error_cargar_imagen, Toast.LENGTH_SHORT).show();

        }


    }
    @Override
    public void onErrorResponse(VolleyError error) {
        if(!getResources().getBoolean(R.bool.mockUp)) {
            Log.e("ERROR RESPONSE", error.toString());
            Toast.makeText(this, R.string.no_internet_error, Toast.LENGTH_SHORT).show();
            progress.setVisibility(View.GONE);
            refreshButton.setVisibility(View.VISIBLE);
        } else {
            this.mockInterestingPoints();
        }

    }

    private void mockInterestingPoints() {
        ArrayList<InterestingPoint> puntosDeInteresDeAtraccion = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(getResources().getString(R.string.interestingPointsInAttractionMock));
            for (int i = 0; i < data.length(); i++) {
                JSONObject current = data.getJSONObject(i);
                puntosDeInteresDeAtraccion.add(InterestingPoint.buildFromJson(current));

            }
        } catch (JSONException e) {
            Log.e("ERROR JSON", e.getMessage());
            return;
        }
        this.adapter = new InterestingPointAdapter(puntosDeInteresDeAtraccion, this);
        this.recycler.setAdapter(this.adapter);
        this.interestingPointsList = puntosDeInteresDeAtraccion;
        progress.setVisibility(View.GONE);
        recycler.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResponse(JSONObject response) {
        ArrayList<InterestingPoint> puntosDeInteresDeAtraccion = new ArrayList<>();
        try {
            JSONArray data = response.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject current = data.getJSONObject(i);
                puntosDeInteresDeAtraccion.add(InterestingPoint.buildFromJson(current));

            }
        } catch (JSONException e) {
            Log.e("ERROR JSON", e.getMessage());
            return;
        }
        this.adapter = new InterestingPointAdapter(puntosDeInteresDeAtraccion, this);
        this.recycler.setAdapter(this.adapter);
        this.interestingPointsList = puntosDeInteresDeAtraccion;
        progress.setVisibility(View.GONE);
        recycler.setVisibility(View.VISIBLE);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String urldisplay = params[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


}
