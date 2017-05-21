package com.tdp2.tripplanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tdp2.tripplanner.AudioActivityExtras.InterestingPointAdapter;
import com.tdp2.tripplanner.attractionSelectionActivityExtras.AttractionDataHolder;
import com.tdp2.tripplanner.dao.APIDAO;
import com.tdp2.tripplanner.helpers.LocaleHandler;
import com.tdp2.tripplanner.modelo.InterestingPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InterestingPointSelection extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {

    APIDAO dao;
    Toolbar toolbar;
    RecyclerView recycler;
    InterestingPointAdapter adapter;
    ProgressBar progress;
    Integer attractionId;
    ImageButton refreshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interesting_point_selection);
        attractionId = AttractionDataHolder.getData().getId();


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

}
