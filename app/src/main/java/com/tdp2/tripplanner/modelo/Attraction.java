package com.tdp2.tripplanner.modelo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.BoringLayout;
import android.util.Base64;
import android.util.Log;

import com.tdp2.tripplanner.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private boolean isFavorite;


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
        this.isFavorite = false;
    }

    public static Attraction buildFromJson(JSONObject current) {
        try {
            byte[] img = Base64.decode(current.getString("imagen"), Base64.DEFAULT);
            Attraction nueva = new Attraction(current.getInt("idAtraccion"), current.getString("nombre"), current.getString("descripcion"),
                    current.getDouble("latitud"), current.getDouble("longitud"),
                    BitmapFactory.decodeByteArray(img, 0, img.length));
            return nueva;
        } catch (JSONException e) {
            Log.e("ATTRACTION_JSON", "error building from json " + e.toString());
            return null;
        }
    }

    public void addDetailsFromJson(JSONObject data) {
        try {
            this.setHorario(data.getString("horario"));
            //this.attraction.setId(2);
            this.setPrecio(data.getString("precio"));
            Integer recorrible = data.getInt("recorrible");
            Boolean brecorrible = recorrible == 1;
            this.setEsRecorrible(brecorrible);
            JSONArray imagenes = data.getJSONArray("listaImagenes");
            for (int i = 1; i < imagenes.length(); i++) {
                byte[] img = Base64.decode(imagenes.getString(i), Base64.DEFAULT);
                this.addImage(BitmapFactory.decodeByteArray(img, 0, img.length));
            }

            String audio = data.getString("audioEN");
            if (!audio.equals("null")) this.setAudio(audio);

            String video = data.getString("video");
            if (!video.equals("null")) {
                //video = "http://www.androidbegin.com/tutorial/AndroidCommercial.3gp";
                this.setVideoLink(video);
            }
        } catch (JSONException e) {
            Log.e("ERROR", e.toString());
        }
    }

    public Boolean hasVideo() {
        return this.videoLink != null;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
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
}
