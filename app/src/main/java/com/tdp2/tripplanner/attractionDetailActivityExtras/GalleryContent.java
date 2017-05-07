package com.tdp2.tripplanner.attractionDetailActivityExtras;

import android.graphics.Bitmap;

/**
 * Created by matias on 5/7/17.
 */

public class GalleryContent {

    private Bitmap image;

    public GalleryContent (Bitmap image) {
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
