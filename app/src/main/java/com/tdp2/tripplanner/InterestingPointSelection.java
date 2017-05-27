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
    Integer attractionId;
    ImageButton refreshButton;
    ImageView plano_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.creation();
    }

    private void creation() {
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
    }
    private void refreshInterestingPoints() {
        //this.dao.getInterestingPointsForAttraction(this.getApplicationContext(), this, this, this.attractionId);
        this.getMockInterestingPoints();
    }

    private void getMockInterestingPoints() {
        ArrayList<InterestingPoint> puntosDeInteresDeAtraccion = new ArrayList<>();
        try {
            JSONArray data = new JSONArray("[{\"id\":1,\"idPunto\":1,\"nombre\":\"Primer Punto de InterÃ©s\",\"descripcion\":\"AcÃ¡ vas a encontrar los animales mÃ¡s corruptos de todos. \",\"orden\":1,\"audio\":null,\"imagen\":\"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\",\"idioma\":null}]");
            for (int i = 0; i < data.length(); i++) {
                JSONObject current = data.getJSONObject(i);

                Bitmap image;
                if (current.get("imagen").toString().equals("null")) {
                    image = BitmapFactory.decodeResource(this.getResources(), R.drawable.buenos_aires_sample);
                } else {
                    byte[] img = Base64.decode(current.getString("imagen"), Base64.DEFAULT);
                    image = BitmapFactory.decodeByteArray(img, 0, img.length);
                }
                Integer id = current.getInt("id");
                String descripcion = current.getString("descripcion");
                String nombre = current.getString("nombre");
                Integer orden = current.getInt("orden");
                InterestingPoint interestingPoint = new InterestingPoint(id, nombre, orden, descripcion, image);
                puntosDeInteresDeAtraccion.add(interestingPoint);



            }
        } catch (JSONException e) {
            Log.e("ERROR JSON", e.getMessage());
            return;
        }

        this.adapter = new InterestingPointAdapter(puntosDeInteresDeAtraccion, this);
        this.recycler.setAdapter(this.adapter);
        //this.attractionList = lista;
        progress.setVisibility(View.GONE);
        recycler.setVisibility(View.VISIBLE);

    }

    private void configRefreshButton() {
        refreshButton = (ImageButton) findViewById(R.id.refreshButton);
        refreshButton.setVisibility(View.GONE);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshInterestingPoints();
                refreshButton.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
            }
        });
        refreshButton.setVisibility(View.GONE);
        recycler.setVisibility(View.GONE);
    }

    private void configProgressBar() {
        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setVisibility(View.VISIBLE);
    }

    private void configAdapter(List items) {
        adapter = new InterestingPointAdapter(items, this);
        recycler.setAdapter(adapter);
    }

    private void configRecycler() {
        recycler = (RecyclerView) findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        RecyclerView.LayoutManager lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);
    }

    private void configToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_ipgrid);
        toolbar.setTitle("Trips");
        toolbar.setSubtitle(R.string.select_interesting_point);
        toolbar.setSubtitleTextColor(Color.WHITE);

        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                this.creation();
                return;
        }
    }

    private void initMapView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_plano);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(R.string.atraction_grid);
        setSupportActionBar(toolbar);
        plano_image = (ImageView) findViewById(R.id.plano_image);

        try {
            new InterestingPointSelection.DownloadImageTask(plano_image)
                    .execute(attraction.getPlanoURL());
        }catch (Exception ex) {
            //plano_image.setVisibility(GONE);
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
        ArrayList<InterestingPoint> puntosDeInteresDeAtraccion = new ArrayList<>();
        try {
            JSONArray data = response.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject current = data.getJSONObject(i);
                Integer borrado = current.getInt("borrado");
                Boolean bborrado = borrado == 1 ;
                if (!bborrado) {
                    Bitmap image;
                    if (current.get("imagenString").toString().equals("null")) {
                        image = BitmapFactory.decodeResource(this.getResources(), R.drawable.buenos_aires_sample);
                    } else {
                        byte[] img = Base64.decode(current.getString("imagenString"), Base64.DEFAULT);
                        image = BitmapFactory.decodeByteArray(img, 0, img.length);
                    }
                    Integer id = current.getInt("id");
                    String descripcion = current.getString("descripcion");
                    String nombre = current.getString("nombre");
                    Integer orden = current.getInt("orden");
                    InterestingPoint interestingPoint = new InterestingPoint(id, nombre, orden, descripcion, image);
                    puntosDeInteresDeAtraccion.add(interestingPoint);

                }

            }
        } catch (JSONException e) {
            Log.e("ERROR JSON", e.getMessage());
            return;
        }
        this.adapter = new InterestingPointAdapter(puntosDeInteresDeAtraccion, this);
        this.recycler.setAdapter(this.adapter);
        //this.attractionList = lista;
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
