package com.tdp2.tripplanner.modelo;

import java.util.Set;

/**
 * Created by matias on 3/25/17.
 */

public class Attraction {
    private String name;
    private Set<String> tags;
    private String moreInfo;
    private Double latitude;
    private Double longitude;
    private Integer image;

    public Attraction(String name, Set<String> tags, String moreInfo, Double latitude, Double longitude, Integer image) {
        this.name = name;
        this.tags = tags;
        this.moreInfo = moreInfo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public Set<String> getTags() {
        return tags;
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

    public Integer getImage() {
        return image;
    }
}
