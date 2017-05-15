package com.tdp2.tripplanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tdp2.tripplanner.attractionDetailActivityExtras.CommentsAdapter;
import com.tdp2.tripplanner.attractionDetailActivityExtras.CommentsDownloader;
import com.tdp2.tripplanner.attractionDetailActivityExtras.EndlessNestedScrollListener;
import com.tdp2.tripplanner.attractionDetailActivityExtras.GalleryContent;
import com.tdp2.tripplanner.attractionDetailActivityExtras.ImageGalleryAdapter;
import com.tdp2.tripplanner.attractionDetailActivityExtras.ShareCommentController;
import com.tdp2.tripplanner.attractionSelectionActivityExtras.AttractionDataHolder;
import com.tdp2.tripplanner.dao.APIDAO;
import com.tdp2.tripplanner.helpers.LocaleHandler;
import com.tdp2.tripplanner.modelo.Attraction;
import com.tdp2.tripplanner.modelo.Comment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.view.View.GONE;

public class AttractionDetailActivity extends AppCompatActivity
        implements Response.Listener<JSONObject>, Response.ErrorListener{

    private APIDAO dao;
    private Attraction attraction;
    private ProgressBar progress;
    private ImageButton refreshButton;
    private Button puntosDeInteresButton;
    private LinearLayout contentView;
    private LinearLayout loadingView;
    private ImageGalleryAdapter adapter;
    FloatingActionButton playButton;
    private ArrayList<GalleryContent> galleryContents;
    private CommentsDownloader commentsDownloader;
    private CommentsAdapter commentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_detail);
        contentView = (LinearLayout) findViewById(R.id.content_layout);
        loadingView = (LinearLayout) findViewById(R.id.loading_layout);
        contentView.setVisibility(GONE);


        LocaleHandler.updateLocaleSettings(this.getBaseContext());

        this.attraction = AttractionDataHolder.getData();
        dao = new APIDAO();
        this.getAttraction(this.attraction.getId());

        this.commentsDownloader = new CommentsDownloader(this);

        NestedScrollView scrollView = (NestedScrollView) findViewById(R.id.nested_scroll);
        scrollView.setOnScrollChangeListener(new EndlessNestedScrollListener(this.commentsDownloader));

        configPuntosDeInteresButton();
        configToolBar();
        setMainImage();
        configProgressBar();
        configRefreshButton();
        configPlayAudioButton();
        configImageGallery();
        configDirectionsButton();
        configCommentsSection();
        configMyCommentSection();
    }

    private void configMyCommentSection() {
        Button shareButton = (Button) findViewById(R.id.share_button);
        final RatingBar myRating = (RatingBar) findViewById(R.id.myrating_bar);
        final EditText myComment = (EditText) findViewById(R.id.comment_edit_text);
        shareButton.setOnClickListener(new ShareCommentController(this.getApplicationContext(), myComment, myRating, this.dao));
    }

    private void configCommentsSection() {
        RecyclerView commentsRecyclerView = (RecyclerView) findViewById(R.id.comments_list);
        this.commentsAdapter = new CommentsAdapter();
        commentsRecyclerView.setAdapter(this.commentsAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        commentsRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void configDirectionsButton() {
        FloatingActionButton directions = (FloatingActionButton) findViewById(R.id.directions_fab);
        directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + String.valueOf(attraction.getLatitude()) + "," +
                String.valueOf(attraction.getLongitude()));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
    }

    private void configPuntosDeInteresButton() {
        puntosDeInteresButton = (Button) findViewById(R.id.button_puntos_interes);
    }

    private void configImageGallery() {
        TextView galleryTitle = (TextView) findViewById(R.id.gallery_title);
        galleryTitle.setText(getResources().getText(R.string.gallery_title));

        getGalleryContents();
        adapter = new ImageGalleryAdapter(this.galleryContents, this);
        RecyclerView galleryRecycler = (RecyclerView) findViewById(R.id.gallery_recycler);
        galleryRecycler.setLayoutManager(new GridLayoutManager(this, 1, LinearLayoutManager.HORIZONTAL, false));
        galleryRecycler.setAdapter(adapter);
    }

    private void configPlayAudioButton() {
        playButton = (FloatingActionButton) this.findViewById(R.id.play_audio_fab);
        if (!attraction.hasAudio()) {

            playButton.setEnabled(false);
            playButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.unenabledButton));
        }
    }

    private void configRefreshButton() {
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
    }

    private void configProgressBar() {
        //Obtener el progress bar
        progress = (ProgressBar) findViewById(R.id.progressBarAttraction);
        progress.setVisibility(View.VISIBLE);
    }

    private void setMainImage() {
    ImageView imageView = (ImageView) findViewById(R.id.attractionDetailImage);
        imageView.setImageBitmap(this.attraction.getMainImage());
}

    private void configToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(this.attraction.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void getGalleryContents() {
        this.galleryContents = new ArrayList<>();
        for (Bitmap img : this.attraction.getImages()) {
            this.galleryContents.add(new GalleryContent(img, "img"));
        }
        if (this.attraction.getVideoLink() != null) {
            String url = this.attraction.getVideoLink();
            GalleryContent contenido = new GalleryContent(this.attraction.getVideoThumb(), "vid");
            contenido.setUrl(url);
            this.galleryContents.add(contenido);
        }
    }

    public static Bitmap retriveVideoFrameFromVideo(String videoPath)
    {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try
        {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
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
            //this.attraction.setId(2);
            this.attraction.setPrecio(data.getString("precio"));
            Integer recorrible = data.getInt("recorrible");
            Boolean brecorrible = recorrible == 1;
            this.attraction.setEsRecorrible(brecorrible);
            JSONArray imagenes = data.getJSONArray("listaImagenes");
            for (int i = 1; i < imagenes.length(); i++) {
                byte[] img = Base64.decode(imagenes.getString(i), Base64.DEFAULT);
                this.attraction.addImage(BitmapFactory.decodeByteArray(img, 0, img.length));
            }

            String audio = data.getString(getResources().getString(R.string.audioXML));
            if (!audio.equals("null")) this.attraction.setAudio(audio);

            String video = data.getString(getString(R.string.videoXML));
            if(!video.equals("null")){
                //video = "http://www.androidbegin.com/tutorial/AndroidCommercial.3gp";
                this.attraction.setVideoLink(video);
                this.attraction.setVideoThumb(retriveVideoFrameFromVideo(video));
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


        getGalleryContents();
        this.adapter.setList(this.galleryContents);
        this.contentView.setVisibility(View.VISIBLE);
        updatePlayAudioButton();
        updatePuntosDeInteresButton();
        updateComments();
    }

    private void updateComments() {
        this.commentsDownloader.getNextPage();
    }

    private void updatePuntosDeInteresButton() {
        if (!attraction.getEsRecorrible()) {
            this.puntosDeInteresButton.setVisibility(View.GONE);
        }
    }

    private void updatePlayAudioButton() {
        if (attraction.hasAudio()) {

            playButton.setEnabled(true);
            playButton.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.enabledButton));
        }
    }

    public void playAudio(View view) {
        Intent intent = new Intent(getBaseContext(), AudioPlayerActivity.class);
        AudioPlayerActivity.attraction = this.attraction;
        startActivity(intent);
    }

    public void verListaPuntosDeInteres(View view) {
        Intent intent = new Intent(getBaseContext(), InterestingPointSelection.class);
        startActivity(intent);
    }

    public void appendComments(ArrayList<Comment> newComments) {
        this.commentsAdapter.setList(newComments);
    }
}
