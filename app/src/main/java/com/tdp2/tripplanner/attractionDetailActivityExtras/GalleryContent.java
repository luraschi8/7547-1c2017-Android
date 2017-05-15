package com.tdp2.tripplanner.attractionDetailActivityExtras;

import android.graphics.Bitmap;

/**
 * Created by matias on 5/7/17.
 */

public class GalleryContent {

    private Bitmap image;
    private String type;
    private String url;

    public GalleryContent (Bitmap image, String type) {
        this.image = image;
        this.type = type;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getType() {
        return this.type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
