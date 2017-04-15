package com.tdp2.tripplanner.dao;

import android.content.ContentValues;
import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tdp2.tripplanner.helpers.APIAccessor;

import org.json.JSONObject;


/**
 * Created by matias on 4/5/17.
 */

public class APIDAO {
    private static String CIUDADES = "ciudadesJson";
    private static String ATRACCIONES_CIUDAD = "atraccionesCiudadJson/";
    private static String ATRACCION = "atraccion/";
    private static String BASE_URL = "http://secure-dawn-22758.herokuapp.com/";


    public void getCities(Context appContext, Response.Listener<JSONObject> responseCallback, Response.ErrorListener errorCallback) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, BASE_URL + CIUDADES, null, responseCallback, errorCallback);

        // Access the RequestQueue through your singleton class.
        APIAccessor.getInstance(appContext).addToRequestQueue(jsObjRequest);
    }

    public void getAttractionForCity(Context appContext, Response.Listener<JSONObject> responseCallback, Response.ErrorListener errorCallback,
                                      Integer cityId) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, BASE_URL + ATRACCIONES_CIUDAD + "/" + String.valueOf(cityId), null, responseCallback, errorCallback);

        // Access the RequestQueue through your singleton class.
        APIAccessor.getInstance(appContext).addToRequestQueue(jsObjRequest);
    }

    public void getAttractionInfo(Context appContext, Response.Listener<JSONObject> responseCallback, Response.ErrorListener errorCallback,
                                     Integer attractionId) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, BASE_URL + ATRACCION + "/" + String.valueOf(attractionId), null, responseCallback, errorCallback);

        // Access the RequestQueue through your singleton class.
        APIAccessor.getInstance(appContext).addToRequestQueue(jsObjRequest);
    }

}
