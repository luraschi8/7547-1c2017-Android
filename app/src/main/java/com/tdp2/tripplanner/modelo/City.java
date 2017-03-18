package com.tdp2.tripplanner.modelo;

/**
 * Created by matias on 3/16/17.
 */

public class City {

    private String name;
    private String country;
    private Double latitude;
    private Double longitude;

    public City (String name, String country, Double latitude, Double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
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
}
