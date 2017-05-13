package com.tdp2.tripplanner.dao;

import android.content.ContentValues;
import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tdp2.tripplanner.InterestingPointDetailActivity;
import com.tdp2.tripplanner.InterestingPointSelection;
import com.tdp2.tripplanner.helpers.APIAccessor;

import org.json.JSONObject;


/**
 * Created by matias on 4/5/17.
 */

public class APIDAO {
    private static String CIUDADES = "ciudadesJson";
    private static String ATRACCIONES_CIUDAD = "atraccionesCiudadJson/";
    private static String PUNTOS_DE_INTERES_ATRACCION = "puntoAtraccionJson";
    private static String ATRACCION = "atraccion/";
    private static String PUNTO_DE_INTERES = "punto";
    private static String BASE_URL = "http://secure-dawn-22758.herokuapp.com/";
    private static String RESENIAS = "reseniasPaginadasAtraccionJson/";


    public void getComments(Context appContext, Response.Listener<JSONObject> callback, Response.ErrorListener errorCallback,
                            Integer attractionID,Integer pageNumber) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, BASE_URL + RESENIAS + String.valueOf(attractionID) +
        "/" + String.valueOf(pageNumber), null , callback, errorCallback);

        APIAccessor.getInstance(appContext).addToRequestQueue(request);
    }

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

    public void getInterestingPointsForAttraction(Context applicationContext, Response.Listener<JSONObject> responseCallback, Response.ErrorListener errorCallback,  Integer id) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, BASE_URL + PUNTOS_DE_INTERES_ATRACCION + "/" + String.valueOf(id), null, responseCallback, errorCallback);

        // Access the RequestQueue through your singleton class.
        APIAccessor.getInstance(applicationContext).addToRequestQueue(jsObjRequest);
    }

    public void getInterestingPointInfo(Context applicationContext,  Response.Listener<JSONObject> responseCallback, Response.ErrorListener errorCallback, Integer id) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, BASE_URL + PUNTO_DE_INTERES + "/" + String.valueOf(id), null, responseCallback, errorCallback);

        // Access the RequestQueue through your singleton class.
        APIAccessor.getInstance(applicationContext).addToRequestQueue(jsObjRequest);
    }
}
