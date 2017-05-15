package com.tdp2.tripplanner;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tdp2.tripplanner.fullscreenImageViewExtras.FullScreenImageAdapter;
import com.tdp2.tripplanner.helpers.LocaleHandler;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenImageViewActivity extends AppCompatActivity {
    private FullScreenImageAdapter adapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image_view);

        LocaleHandler.updateLocaleSettings(this.getBaseContext());


        viewPager = (ViewPager) findViewById(R.id.pager);

        Intent i = getIntent();
        int position = i.getIntExtra("POSITION", 0);

        adapter = new FullScreenImageAdapter(this);

        viewPager.setAdapter(adapter);

        // displaying selected image first
        viewPager.setCurrentItem(position);
    }
}
