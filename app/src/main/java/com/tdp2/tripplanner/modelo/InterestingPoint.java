package com.tdp2.tripplanner.modelo;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by Casa on 15/4/2017.
 */

public class InterestingPoint {
    public String name;
    public int order;
    public Bitmap image;
    public Integer id;
    public String descripcion;
    private String audio;

    public InterestingPoint(Integer id, String name, int order, String descripcion, Bitmap image) {
        this.id = id;
        this.name = name;
        this.order = order;
        this.image = image;
        this.descripcion = descripcion;
        this.audio = null;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Bitmap getMainImage() {
        return image;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public boolean hasAudio() {
        return audio != null;
    }

    public String getAudio() {
        return audio;
    }
}
