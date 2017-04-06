package com.tdp2.tripplanner.dao;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tdp2.tripplanner.helpers.APIAccessor;
import com.tdp2.tripplanner.modelo.City;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by matias on 4/5/17.
 */

public class CityDAO {
    private static String CIUDADES = "ciudadesJson";
    private static String BASE_URL = "https://secure-dawn-22758.herokuapp.com/";


    public void getCities(Context appContext, Response.Listener responseCallback, Response.ErrorListener errorCallback) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, BASE_URL + CIUDADES, null, responseCallback, errorCallback);

        // Access the RequestQueue through your singleton class.
        APIAccessor.getInstance(appContext).addToRequestQueue(jsObjRequest);
    }


}
