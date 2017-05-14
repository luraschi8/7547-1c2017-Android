package com.tdp2.tripplanner.dao;

import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tdp2.tripplanner.InterestingPointDetailActivity;
import com.tdp2.tripplanner.InterestingPointSelection;
import com.tdp2.tripplanner.attractionSelectionActivityExtras.AttractionDataHolder;
import com.tdp2.tripplanner.helpers.APIAccessor;
import com.tdp2.tripplanner.modelo.Comment;

import org.json.JSONException;
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
    private static String CREAR_RESENIA = "crearResenia";


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

    public void postComment(Context applicationContext, Response.Listener<JSONObject> responseCallback,
                            Response.ErrorListener errorListener, Comment comment) {
        JSONObject toPost = this.buildJSonFromComment(comment);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BASE_URL + CREAR_RESENIA, toPost,
                responseCallback, errorListener);
        APIAccessor.getInstance(applicationContext).addToRequestQueue(request);
    }

    private JSONObject buildJSonFromComment(Comment comment){
        JSONObject json = new JSONObject();
        JSONObject jsonAtraccion = new JSONObject();
        try {
            json.put("comentario", comment.getComment());
            json.put("nombreUsuario", "AndroidAPP"); //TODO cambiar por el username de la red social.
            json.put("calificacion", comment.getRating());
            jsonAtraccion.put("id", AttractionDataHolder.getData().getId());
            json.put("atraccion", jsonAtraccion);
        } catch (JSONException e) {
            Log.e("JSON ERROR", "buildJSonFromComment: " + e.toString());
            return null;
        }
        return json;
    }
}
