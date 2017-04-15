package com.tdp2.tripplanner.fullscreenImageViewExtras;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tdp2.tripplanner.R;
import com.tdp2.tripplanner.attractionSelectionActivityExtras.AttractionDataHolder;

/**
 * Created by matias on 4/15/17.
 */

public class FullScreenImageAdapter extends PagerAdapter {
    private Activity _activity;
    private LayoutInflater inflater;

    // constructor
    public FullScreenImageAdapter(Activity activity) {
        this._activity = activity;
    }

    @Override
    public int getCount() {
        return AttractionDataHolder.getData().getImages().size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imgDisplay;
        ImageButton btnClose;

        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.full_screen_image_layout, container,
                false);

        imgDisplay = (ImageView) viewLayout.findViewById(R.id.imgDisplay);
        btnClose = (ImageButton) viewLayout.findViewById(R.id.btnClose);

        imgDisplay.setImageBitmap(AttractionDataHolder.getData().getImages().get(position));

        // close button click event
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _activity.finish();
            }
        });

        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }
}
