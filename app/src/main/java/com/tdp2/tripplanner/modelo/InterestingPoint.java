package com.tdp2.tripplanner.modelo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.tdp2.tripplanner.R;

import org.json.JSONException;
import org.json.JSONObject;

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

    public static InterestingPoint buildFromJson(JSONObject current) {
        try {
            Bitmap image;
            try{
                byte[] img = Base64.decode(current.getString("imagen"), Base64.DEFAULT);
                image = BitmapFactory.decodeByteArray(img, 0, img.length);
            }catch (Exception e) {
                image = null;
            }
            Integer id = current.getInt("id");
            String descripcion = current.getString("descripcion");
            String nombre = current.getString("nombre");
            Integer orden = current.getInt("orden");
            InterestingPoint interestingPoint = new InterestingPoint(id, nombre, orden, descripcion, image);
            return interestingPoint;

        } catch (JSONException e) {
            Log.e("ATTRACTION_JSON", "error building from json " + e.toString());
            return null;
        }
    }
}
