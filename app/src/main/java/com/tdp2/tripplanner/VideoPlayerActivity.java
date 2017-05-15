package com.tdp2.tripplanner;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.VideoView;


public class VideoPlayerActivity extends AppCompatActivity {

    // Declare variables
    ProgressDialog pDialog;
    VideoView videoview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_player);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ImageButton btnClose = (ImageButton) findViewById(R.id.btnClose);
        // close button click event
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoPlayerActivity.this.finish();
            }
        });

        // Find your VideoView in your video_main.xml layout
        videoview = (VideoView) findViewById(R.id.VideoView);
        // Execute StreamVideo AsyncTask

        // Create a progressbar
        pDialog = new ProgressDialog(VideoPlayerActivity.this);
        // Set progressbar title
        pDialog.setTitle(getString(R.string.video_dialog));
        // Set progressbar message
        pDialog.setMessage(getString(R.string.buffering));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        // Show progressbar
        pDialog.show();

        try {
            // Start the MediaController
            MediaController mediacontroller = new MediaController(
                    VideoPlayerActivity.this);
            mediacontroller.setAnchorView(videoview);
            // Get the URL from String VideoURL
            Uri video = Uri.parse(getIntent().getStringExtra("URL"));
            //Uri video = Uri.parse("http://www.androidbegin.com/tutorial/AndroidCommercial.3gp");
            videoview.setMediaController(mediacontroller);
            videoview.setVideoURI(video);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        videoview.requestFocus();
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                pDialog.dismiss();
                videoview.start();
            }
        });

        videoview.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                pDialog.dismiss();
                return false;
            }
        });
    }

}
