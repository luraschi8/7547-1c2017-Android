package com.tdp2.tripplanner.modelo;

import android.graphics.Bitmap;

import java.util.Set;

/**
 * Created by matias on 3/25/17.
 */

public class Attraction {
    private String name;
    private String moreInfo;
    private Double latitude;
    private Double longitude;
    private Bitmap image;
    private Integer id;


    public Attraction(Integer id, String name, String moreInfo, Double latitude, Double longitude, Bitmap image) {
        this.id = id;

        this.name = name;
        this.moreInfo = moreInfo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getMoreInfo() {
        return moreInfo;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Bitmap getImage() {
        return image;
    }

    public Integer getId() {
        return id;
    }
}
