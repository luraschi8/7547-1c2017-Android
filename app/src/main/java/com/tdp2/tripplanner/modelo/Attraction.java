package com.tdp2.tripplanner.modelo;

import android.graphics.Bitmap;
import android.text.BoringLayout;

import java.util.ArrayList;


/**
 * Created by matias on 3/25/17.
 */

public class Attraction {
    private String name;
    private String moreInfo;
    private Double latitude;
    private Double longitude;
    private ArrayList<Bitmap> images;
    private Integer id;
    private String horario;
    private String precio;
    private boolean esRecorrible;
    private String audioLink;
    private String videoLink;
    private Bitmap videoThumb;
    private String planoURL;


    public Attraction(Integer id, String name, String moreInfo, Double latitude, Double longitude, Bitmap image) {
        this.id = id;
        this.name = name;
        this.moreInfo = moreInfo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.images = new ArrayList<>();
        this.images.add(image);
        this.audioLink = null;
        this.videoLink = null;
        this.videoThumb = null;
    }


    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public String getAudio() {
        return audioLink;
    }

    public void setAudio(String audio) {
        this.audioLink = audio;
    }

    public Boolean hasAudio() {
        return this.audioLink != null;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public void setEsRecorrible(Boolean esRecorrible) { this.esRecorrible = esRecorrible; }

    public String getName() {
        return name;
    }

    public String getFullInfo() {
        return moreInfo + "<br><br><b>Horario: </b>" + horario + "\n<br><b>Precio: </b>" + precio;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Bitmap getMainImage() {
        return this.images.get(0);
    }

    public Integer getId() {
        return id;
    }

    public void addImage(Bitmap image) {
        this.images.add(this.images.size(), image);
    }

    public ArrayList<Bitmap> getImages() {
        return this.images;
    }

    public boolean getEsRecorrible() {
        return esRecorrible;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getVideoThumb() {
        return videoThumb;
    }

    public void setVideoThumb(Bitmap videoThumb) {
        this.videoThumb = videoThumb;
    }

    public void setPlanoURL(String planoURL) {
        this.planoURL = planoURL;
    }

    public String getPlanoURL() {
        return planoURL;
    }
}
