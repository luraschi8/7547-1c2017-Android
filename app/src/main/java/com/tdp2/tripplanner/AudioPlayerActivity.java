package com.tdp2.tripplanner;


import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tdp2.tripplanner.AudioActivityExtras.InterestingPointAdapter;
import com.tdp2.tripplanner.helpers.LocaleHandler;
import com.tdp2.tripplanner.modelo.Attraction;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AudioPlayerActivity extends AppCompatActivity {

    private Button forwardButton, pauseButton, playButton, backwardButton;
    public static MediaPlayer mediaPlayer;
    public final static String EXTRA_MESSAGE = "com.tdp2.tripplanner.MediaPlayer";
    private double startTime = 0;
    private double finalTime = 0;
    private Toolbar toolbar;
    private RecyclerView recycler;
    private InterestingPointAdapter adapter;
    private Handler myHandler = new Handler();;
    private int forwardTime = 5000;
    private int backwardTime = 5000;
    private SeekBar seekbar;
    private TextView tx1,tx2,tx3;
    ImageView attractionImage;
    private RecyclerView.LayoutManager lManager;
    public static Attraction attraction;

    public static int oneTimeOnly = 0;

    @Override
    protected void onRestart(){
        super.onRestart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        LocaleHandler.updateLocaleSettings(this.getBaseContext());

        oneTimeOnly = 0;
        //Obtener el toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Audio Guia");
        toolbar.setSubtitle(R.string.select_city);
        toolbar.setSubtitleTextColor(Color.WHITE);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Controladores del audio
        forwardButton = (Button) findViewById(R.id.forward_button);
        pauseButton = (Button) findViewById(R.id.pause_button);
        playButton = (Button)findViewById(R.id.play_button);
        backwardButton = (Button)findViewById(R.id.backward_button);

        pauseButton.setEnabled(false);
        playButton.setEnabled(false);


        //Textos del audio
        tx1 = (TextView)findViewById(R.id.textView2);
        tx2 = (TextView)findViewById(R.id.textView3);
        tx3 = (TextView)findViewById(R.id.attraction_name);
        tx3.setText(attraction.getName());

        //Image
        attractionImage = (ImageView) findViewById(R.id.mp3Image);
        attractionImage.setImageBitmap(attraction.getMainImage());

        String url = attraction.getAudio();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){

                @Override
                public void onPrepared(MediaPlayer mp) {
                    playButton.setEnabled(true);
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    oneTimeOnly = 0;
                    seekbar.setProgress(0);
                    mediaPlayer.seekTo(0);
                    playButton.setEnabled(true);
                    pauseButton.setEnabled(false);

                }
            });
            mediaPlayer.prepareAsync();; // might take long! (for buffering, etc)
        } catch (IOException e) {
            FloatingActionButton play_audio_fab = (FloatingActionButton)findViewById(R.id.play_audio_fab);
            play_audio_fab.setEnabled(false);
        }

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                playButton.setEnabled(true);
            }
        });
        seekbar = (SeekBar)findViewById(R.id.seekBar);
        seekbar.setClickable(false);
        seekbar.setProgress(0);
        pauseButton.setEnabled(false);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            if (mediaPlayer.isPlaying()){
            startTime = mediaPlayer.getCurrentPosition();
            tx1.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))
            );
            seekbar.setProgress((int)startTime);
            myHandler.postDelayed(this, 100);}
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) mediaPlayer.release();

    }

    public void forwardAction(View view) {
        int temp = (int)startTime;

        if((temp+forwardTime)<=finalTime){
            startTime = startTime + forwardTime;
            mediaPlayer.seekTo((int) startTime);
            Toast.makeText(getApplicationContext(),"You have Jumped forward 5 seconds",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(),"Cannot jump forward 5 seconds",Toast.LENGTH_SHORT).show();
        }
    }

    public void backwardAction(View view) {
        int temp = (int)startTime;

        if((temp-backwardTime)>0){
            startTime = startTime - backwardTime;
            mediaPlayer.seekTo((int) startTime);
            Toast.makeText(getApplicationContext(),"You have Jumped backward 5 seconds",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(),"Cannot jump backward 5 seconds",Toast.LENGTH_SHORT).show();
        }
    }

    public void pauseAction(View view) {
        Toast.makeText(getApplicationContext(), "Pausing sound",Toast.LENGTH_SHORT).show();
        mediaPlayer.pause();
        pauseButton.setEnabled(false);
        playButton.setEnabled(true);
    }

    public void playAction(View view) {
        Toast.makeText(getApplicationContext(), "Playing sound",Toast.LENGTH_SHORT).show();

        mediaPlayer.start();
        finalTime = mediaPlayer.getDuration();
        startTime = mediaPlayer.getCurrentPosition();

        if (oneTimeOnly == 0) {
            seekbar.setMax((int) finalTime);
            oneTimeOnly = 1;
            startTime = 0;
        }

        tx2.setText(String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                finalTime)))
        );

        tx1.setText(String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                startTime)))
        );

        seekbar.setProgress((int)startTime);
        myHandler.postDelayed(UpdateSongTime,100);
        pauseButton.setEnabled(true);
        playButton.setEnabled(false);

    }
}
