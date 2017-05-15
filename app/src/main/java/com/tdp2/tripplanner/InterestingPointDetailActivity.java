package com.tdp2.tripplanner;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tdp2.tripplanner.AudioActivityExtras.InterestingPointDataHolder;
import com.tdp2.tripplanner.dao.APIDAO;
import com.tdp2.tripplanner.helpers.LocaleHandler;
import com.tdp2.tripplanner.modelo.InterestingPoint;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static android.view.View.GONE;

public class InterestingPointDetailActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {

    private APIDAO dao;
    private InterestingPoint interestingPoint;
    private ProgressBar progressBar;
    private ImageButton refreshButton;
    private LinearLayout contentView;
    private LinearLayout loadingView;
    private TextView descripcion;
    private ImageView mainImage;
    private RelativeLayout audioPlayer;
    private Button forwardButton;
    private Button pauseButton;
    private Button playButton;
    private Button backwardButton;
    private TextView tx1;
    private TextView tx2;
    private MediaPlayer mediaPlayer;
    SeekBar seekbar;
    private double startTime = 0;
    private double finalTime = 0;
    private int forwardTime = 5000;
    private int backwardTime = 5000;
    public static int oneTimeOnly = 0;
    private Handler myHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interesting_point_detail);

        LocaleHandler.updateLocaleSettings(this.getBaseContext());

        contentView = (LinearLayout) findViewById(R.id.content_layout_ip);
        loadingView = (LinearLayout) findViewById(R.id.loading_layout);
        audioPlayer = (RelativeLayout) findViewById(R.id.audioPlayer);
        contentView.setVisibility(GONE);
        audioPlayer.setVisibility(GONE);
        this.interestingPoint = InterestingPointDataHolder.getData();
        dao = new APIDAO();
        this.getInterestingPoint(this.interestingPoint.getId());
        this.descripcion = (TextView) findViewById(R.id.ip_description);
        configToolBar();
        setMainImage();
        configProgressBar();
        configRefreshButton();
        configAudioPlayer();
    }

    private void configAudioPlayer() {
        //Controladores del audio
        forwardButton = (Button) findViewById(R.id.forward_button);
        pauseButton = (Button) findViewById(R.id.pause_button);
        playButton = (Button)findViewById(R.id.play_button);
        backwardButton = (Button)findViewById(R.id.backward_button);
        pauseButton.setEnabled(false);
        playButton.setEnabled(false);
        tx1 = (TextView)findViewById(R.id.textView2);
        tx2 = (TextView)findViewById(R.id.textView3);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        seekbar = (SeekBar)findViewById(R.id.seekBar);
        seekbar.setClickable(false);
        seekbar.setProgress(0);

        //loadAudio();
    }

    private void loadAudio() {
        String url = interestingPoint.getAudio();

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


    }

    private void getInterestingPoint(Integer id) {
        this.dao.getInterestingPointInfo(this.getApplicationContext(), this, this, id);
    }

    private void configRefreshButton() {
        //Obtengo el refreshButton
        refreshButton = (ImageButton) findViewById(R.id.refreshButtonAttraction);
        refreshButton.setVisibility(GONE);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getInterestingPoint(interestingPoint.getId());
                refreshButton.setVisibility(GONE);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void configProgressBar() {
        //Obtener el progress bar
        progressBar = (ProgressBar) findViewById(R.id.progressBarAttraction);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void setMainImage() {
        mainImage = (ImageView) findViewById(R.id.attractionDetailImage);
        mainImage.setImageBitmap(this.interestingPoint.getMainImage());
    }

    private void configToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(this.interestingPoint.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e("ERROR RESPONSE", error.toString());
        Toast.makeText(this, R.string.no_internet_error, Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(GONE);
        refreshButton.setVisibility(View.VISIBLE);
    }
    @Override
    public void onResponse(JSONObject response) {
        try {
            JSONObject data = response.getJSONObject("data");
            String audio = data.getString("audioEN");
            if (!audio.equals("null")){
                this.interestingPoint.setAudio(audio);
            }


        } catch (JSONException e) {
            Log.e("ERROR JSON", e.getMessage());
            return;
        }
        progressBar.setVisibility(View.GONE);
        loadingView.setVisibility(GONE);
        this.notifyDataChanged();
    }

    private void notifyDataChanged() {
        descripcion.setText(this.interestingPoint.descripcion);
        this.contentView.setVisibility(View.VISIBLE);
        updateAudioPlayer();
    }

    private void updateAudioPlayer() {
        if (this.interestingPoint.hasAudio()) {
            audioPlayer.setVisibility(View.VISIBLE);
            this.loadAudio();
        }
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
