package com.tdp2.tripplanner.modelo;

import android.graphics.Bitmap;

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
    private byte[] audio;


    public Attraction(Integer id, String name, String moreInfo, Double latitude, Double longitude, Bitmap image) {
        this.id = id;

        this.name = name;
        this.moreInfo = moreInfo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.images = new ArrayList<>();
        this.images.add(image);
        this.audio = null;
    }

    public byte[] getAudio() {
        return audio;
    }

    public void setAudio(byte[] audio) {
        this.audio = audio;
    }

    public Boolean hasAudio() {
        return this.audio != null;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getHorario() {
        return horario;
    }

    public String getPrecio() {
        return precio;
    }

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
}
