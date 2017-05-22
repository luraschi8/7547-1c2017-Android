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
        viewingMap = false;
        lazyMap = false;
        gridView = getLayoutInflater().inflate(R.layout.activity_interesting_point_selection, null);
        mapView = getLayoutInflater().inflate(R.layout.activity_plano_atraccion, null);
        setContentView(R.layout.activity_interesting_point_selection);
        attractionId = AttractionDataHolder.getData().getId();

        attraction = AttractionDataHolder.getData();
        plano_image = (ImageView) findViewById(R.id.plano_image);

        try {
            new InterestingPointSelection.DownloadImageTask(plano_image)
                    .execute(attraction.getPlanoURL());
        }catch (Exception ex) {
            //plano_image.setVisibility(GONE);
        }

        LocaleHandler.updateLocaleSettings(this.getBaseContext());

        dao = new APIDAO();
        refreshInterestingPoints();

        // Inicializar Ciudades esto se cambia por pegarle al API
        List items = new ArrayList();

        configToolBar();
        configRecycler();

        configAdapter(items);
        configProgressBar();
        configRefreshButton();
    }

    private void refreshInterestingPoints() {
        this.dao.getInterestingPointsForAttraction(this.getApplicationContext(), this, this, this.attractionId);
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
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Trips");
        toolbar.setSubtitle(R.string.select_interesting_point);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setTitleTextColor(Color.WHITE);
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.map_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(R.string.atraction_grid);
        setSupportActionBar(toolbar);


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
        progress.setVisibility(View.GONE);
        this.adapter.setList(puntosDeInteresDeAtraccion);
        this.adapter.notifyDataSetChanged();
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
