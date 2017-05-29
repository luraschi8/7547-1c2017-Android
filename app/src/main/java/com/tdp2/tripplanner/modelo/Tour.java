package com.tdp2.tripplanner.modelo;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Casa on 28/5/2017.
 */

public class Tour {
    public int id;
    public String name;

    public Tour(int id, String nombre) {
        this.id = id;
        name = nombre;
    }

    public static Tour buildFromJson(JSONObject current) {
        try {
            Tour tour = new Tour(current.getInt("id"), current.getString("nombre"));
            return tour;
        } catch (JSONException e) {
            Log.e("TOUR_JSON", "error building from json " + e.toString());
            return null;
        }
    }
}
