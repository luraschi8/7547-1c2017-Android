package com.tdp2.tripplanner;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;


public class AttractionDetailActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    public final static String EXTRA_MESSAGE = "com.tdp2.tripplanner.MediaPlayer";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Nombre de la atrracion");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ImageView imageView = (ImageView) findViewById(R.id.attractionDetailImage);
        imageView.setImageResource(R.drawable.colon_sample);

        //Get the attraction info text view
        TextView moreInfo = (TextView) findViewById(R.id.attraction_more_info);
        moreInfo.setText("Esto es la informacion de la atraccion que viene en el json.\n Podria incluir horarios y bla bla bla");

        //Get the eta
        TextView etaText = (TextView) findViewById(R.id.eta);
        etaText.setText("Tiempo estimado: 4 minutos a pie.");

        //Le saco el autofocus al campo de comentarios
        EditText commentField = (EditText) findViewById(R.id.comment_edit_text);
        commentField.setSelected(false);

        TextView commentsView = (TextView) findViewById(R.id.comments);
        commentsView.setText("Aca van todos los comentarios previos.");

        TextView tags = (TextView) findViewById(R.id.tags);
        tags.setTextColor(Color.WHITE);
        tags.setText("Tag1, Tag2, Tag3, ..., Tag n");

        String url = "https://www.hrupin.com/wp-content/uploads/mp3/testsong_20_sec.mp3"; // your URL here
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(url);
           /* mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){

                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });*/
            mediaPlayer.prepareAsync();; // might take long! (for buffering, etc)
        } catch (IOException e) {
            FloatingActionButton play_audio_fab = (FloatingActionButton)findViewById(R.id.play_audio_fab);
            play_audio_fab.setEnabled(false);       }

        FloatingActionButton myFab = (FloatingActionButton) this.findViewById(R.id.play_audio_fab);
        myFab.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent intent = new Intent(getBaseContext(), AudioPlayerActivity.class);
                //EditText editText = (EditText) findViewById(R.id.edit_message);
                //String message = editText.getText().toString();
                //intent.putExtra(EXTRA_MESSAGE, message);
                AudioPlayerActivity.mediaPlayer = mediaPlayer;
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_attraction_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
