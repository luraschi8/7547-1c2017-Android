package com.tdp2.tripplanner.modelo;

import android.graphics.Bitmap;

/**
 * Created by matias on 3/16/17.
 */

public class City {

    private String name;
    private String country;
    private Double latitude;
    private Double longitude;
    private Bitmap image;
    private Integer id;

    public City (Integer id, String name, String country, Bitmap image, Double latitude, Double longitude) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
        this.image = image;
    }


    public String getName() {
        return name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getCountry() {
        return this.country;
    }

    public Bitmap getImage() {
        return this.image;
    }

    public Integer getId() {
        return id;
    }
}
