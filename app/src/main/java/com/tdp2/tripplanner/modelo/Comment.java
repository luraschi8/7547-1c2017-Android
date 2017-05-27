package com.tdp2.tripplanner.modelo;

import android.util.Log;

import com.tdp2.tripplanner.attractionSelectionActivityExtras.AttractionDataHolder;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by matias on 5/12/17.
 */

public class Comment {

    private String comment;
    private String username;
    private String dateAndTime;
    private Float rating;

    public Comment(String comment, String username, String dateAndTime, Float rating) {
        this.comment = comment;
        this.username = username;
        this.dateAndTime = dateAndTime;
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public String getUsername() {
        return username;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public Float getRating() {
        return rating;
    }

    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        JSONObject jsonAtraccion = new JSONObject();
        try {
            json.put("comentario", this.getComment());
            json.put("nombreUsuario", this.getUsername());
            json.put("calificacion", this.getRating());
            jsonAtraccion.put("id", AttractionDataHolder.getData().getId());
            json.put("atraccion", jsonAtraccion);
        } catch (JSONException e) {
            Log.e("JSON ERROR", "buildJSonFromComment: " + e.toString());
            return null;
        }
        return json;
    }
}
