package com.tdp2.tripplanner;

import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tdp2.tripplanner.attractionDetailActivityExtras.ImageGalleryAdapter;
import com.tdp2.tripplanner.attractionSelectionActivityExtras.AttractionDataHolder;
import com.tdp2.tripplanner.dao.APIDAO;
import com.tdp2.tripplanner.modelo.Attraction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.view.View.GONE;

public class AttractionDetailActivity extends AppCompatActivity
        implements Response.Listener<JSONObject>, Response.ErrorListener{

    private APIDAO dao;
    private Attraction attraction;
    private ProgressBar progress;
    private ImageButton refreshButton;
    private LinearLayout contentView;
    private LinearLayout loadingView;
    private ImageGalleryAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_detail);
        contentView = (LinearLayout) findViewById(R.id.content_layout);
        loadingView = (LinearLayout) findViewById(R.id.loading_layout);
        contentView.setVisibility(View.GONE);

        this.attraction = AttractionDataHolder.getData();

        dao = new APIDAO();
        this.getAttraction(this.attraction.getId());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(this.attraction.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ImageView imageView = (ImageView) findViewById(R.id.attractionDetailImage);
        imageView.setImageBitmap(this.attraction.getMainImage());


        //Obtener el progress bar
        progress = (ProgressBar) findViewById(R.id.progressBarAttraction);
        progress.setVisibility(View.VISIBLE);

        //Obtengo el refreshButton
        refreshButton = (ImageButton) findViewById(R.id.refreshButtonAttraction);
        refreshButton.setVisibility(GONE);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAttraction(attraction.getId());
                refreshButton.setVisibility(GONE);
                progress.setVisibility(View.VISIBLE);
            }
        });

        TextView galleryTitle = (TextView) findViewById(R.id.gallery_title);
        galleryTitle.setText(getResources().getText(R.string.gallery_title));

        adapter = new ImageGalleryAdapter(this.attraction.getImages(), this);
        RecyclerView galleryRecycler = (RecyclerView) findViewById(R.id.gallery_recycler);
        galleryRecycler.setLayoutManager(new GridLayoutManager(this, 1, LinearLayoutManager.HORIZONTAL, false));
        galleryRecycler.setAdapter(adapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_attraction_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e("ERROR RESPONSE", error.toString());
        Toast.makeText(this, R.string.no_internet_error, Toast.LENGTH_SHORT).show();
        progress.setVisibility(GONE);
        refreshButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            JSONObject data = response.getJSONObject("data");
            this.attraction.setHorario(data.getString("horario"));
            this.attraction.setPrecio(data.getString("precio"));
            JSONArray imagenes = data.getJSONArray("listaImagenes");
            for (int i = 1; i < imagenes.length(); i++) {
                byte[] img = Base64.decode(imagenes.getString(i), Base64.DEFAULT);
                this.attraction.addImage(BitmapFactory.decodeByteArray(img, 0, img.length));
            }
            String audio = data.getString(getResources().getString(R.string.audioXML));
            if (!audio.equals("null")){
                 this.attraction.setAudio(audio);
            }
        } catch (JSONException e) {
            Log.e("ERROR JSON", e.getMessage());
            return;
        }
        progress.setVisibility(GONE);
        loadingView.setVisibility(GONE);
        this.notifyDataChanged();
    }

    private void getAttraction(Integer attractionID) {
        this.dao.getAttractionInfo(this.getApplicationContext(), this, this, attractionID);
    }

    private void notifyDataChanged() {
        //Get the attraction info text view
        TextView moreInfo = (TextView) findViewById(R.id.attraction_more_info);
        if (Build.VERSION.SDK_INT >= 24) {
            moreInfo.setText(Html.fromHtml(this.attraction.getFullInfo(), Html.FROM_HTML_MODE_COMPACT)); // for 24 api and more
        } else {
            moreInfo.setText(Html.fromHtml(this.attraction.getFullInfo())); //api 23 or less.
        }

        //Le saco el autofocus al campo de comentarios
        EditText commentField = (EditText) findViewById(R.id.comment_edit_text);
        commentField.setSelected(false);

        TextView commentsView = (TextView) findViewById(R.id.comments);
        commentsView.setText("Aca van todos los comentarios previos.");


        this.adapter.setList(this.attraction.getImages());
        this.contentView.setVisibility(View.VISIBLE);
    }

}
