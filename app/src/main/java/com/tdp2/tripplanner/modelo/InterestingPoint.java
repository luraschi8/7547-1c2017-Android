package com.tdp2.tripplanner.modelo;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by Casa on 15/4/2017.
 */

public class InterestingPoint {
    public String name;
    public int order;
    public Integer image;

    public InterestingPoint(String name, int order, Integer image) {
        this.name = name;
        this.order = order;
        this.image = image;
    }
}
