package com.tdp2.tripplanner.dao;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tdp2.tripplanner.attractionSelectionActivityExtras.AttractionDataHolder;
import com.tdp2.tripplanner.helpers.APIAccessor;
import com.tdp2.tripplanner.modelo.SocialUser;
import com.tdp2.tripplanner.helpers.LocaleHandler;
import com.tdp2.tripplanner.modelo.Comment;
import com.tdp2.tripplanner.modelo.UserInstance;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by matias on 4/5/17.
 */

public class APIDAO {
    private static String CIUDADES = "ciudadesJson";
    private static String ATRACCIONES_CIUDAD = "atraccionesCiudadJson/";
    private static String PUNTOS_DE_INTERES_ATRACCION = "puntoAtraccionJson";
    private static String ATRACCION = "atraccion";
    private static String PUNTO_DE_INTERES = "punto";
    private static String LOGIN = ""; //todo: completar
    private static String BASE_URL = "http://10.0.2.2:8080/";
    private static String RESENIAS = "reseniasPaginadasAtraccionJson/";
    private static String CREAR_RESENIA = "crearResenia";
    private static String ACCESO_USUARIO = "accesoUsuario";
    private static String AGREGAR_FAVORITO = "agregarFavorito";
    private static String SACAR_FAVORITO = "sacarFavorito";
    private static String OBTENER_FAVORITOS = "usuarioFavoritos";


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
        String url = BASE_URL + ATRACCIONES_CIUDAD + "/" + String.valueOf(cityId) + "/" + LocaleHandler.loadLanguageSelection(appContext);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url , null, responseCallback, errorCallback);

        // Access the RequestQueue through your singleton class.
        APIAccessor.getInstance(appContext).addToRequestQueue(jsObjRequest);
    }

    public void getAttractionInfo(Context appContext, Response.Listener<JSONObject> responseCallback, Response.ErrorListener errorCallback,
                                     Integer attractionId) {
        String url =  BASE_URL + ATRACCION + "/" + String.valueOf(attractionId) + "/" + LocaleHandler.loadLanguageSelection(appContext);
        JSONObject object = UserInstance.toJSON(appContext);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, object, responseCallback, errorCallback);

        // Access the RequestQueue through your singleton class.
        APIAccessor.getInstance(appContext).addToRequestQueue(jsObjRequest);
    }

    public void getInterestingPointsForAttraction(Context applicationContext, Response.Listener<JSONObject> responseCallback, Response.ErrorListener errorCallback,  Integer id) {
        String url = BASE_URL + PUNTOS_DE_INTERES_ATRACCION + "/" + String.valueOf(id) + "/" + LocaleHandler.loadLanguageSelection(applicationContext);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, responseCallback, errorCallback);

        // Access the RequestQueue through your singleton class.
        APIAccessor.getInstance(applicationContext).addToRequestQueue(jsObjRequest);
    }

    public void getInterestingPointInfo(Context applicationContext,  Response.Listener<JSONObject> responseCallback, Response.ErrorListener errorCallback, Integer id) {
        String url = BASE_URL + PUNTO_DE_INTERES + "/" + String.valueOf(id) + "/" + LocaleHandler.loadLanguageSelection(applicationContext);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET,url, null, responseCallback, errorCallback);

        // Access the RequestQueue through your singleton class.
        APIAccessor.getInstance(applicationContext).addToRequestQueue(jsObjRequest);
    }

    public void postLoginProfile(Context applicationContext,  Response.Listener<JSONObject> responseCallback, Response.ErrorListener errorCallback, SocialUser profile) {
        JSONObject params = profile.GetJsonObject();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(BASE_URL + LOGIN + "/", params, responseCallback, errorCallback);

        // Access the RequestQueue through your singleton class.
        APIAccessor.getInstance(applicationContext).addToRequestQueue(jsObjRequest);
    }

    public void postComment(Context applicationContext, Response.Listener<JSONObject> responseCallback,
                            Response.ErrorListener errorListener, Comment comment) {
        JSONObject toPost = comment.toJSON();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BASE_URL + CREAR_RESENIA, toPost,
                responseCallback, errorListener);
        APIAccessor.getInstance(applicationContext).addToRequestQueue(request);
    }

    public void registerUserAccess(Context applicationContext, Response.Listener<JSONObject> callback,
                                   Response.ErrorListener errorListener) {
        JSONObject object = UserInstance.toJSON(applicationContext);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BASE_URL + ACCESO_USUARIO, object,
                callback, errorListener);
        APIAccessor.getInstance(applicationContext).addToRequestQueue(request);
    }

    public void saveToFavorites(Context applicationContext, Response.Listener<JSONObject> callback,
                                Response.ErrorListener errorListener, Integer idAtraccion) {
        JSONObject object = UserInstance.toJSON(applicationContext);
        try {
            object.put("idAtraccion", String.valueOf(idAtraccion));
        } catch (JSONException e) {
            Log.e("ERROR", e.toString());
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BASE_URL + AGREGAR_FAVORITO, object,
                callback, errorListener);
        APIAccessor.getInstance(applicationContext).addToRequestQueue(request);
    }

    public void removeFavorite(Context applicationContext, Response.Listener<JSONObject> callback,
                                    Response.ErrorListener errorListener, Integer idAtraccion) {
        JSONObject object = UserInstance.toJSON(applicationContext);
        try {
            object.put("idAtraccion", String.valueOf(idAtraccion));
        } catch (JSONException e) {
            Log.e("ERROR", e.toString());
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BASE_URL + SACAR_FAVORITO, object,
                callback, errorListener);
        APIAccessor.getInstance(applicationContext).addToRequestQueue(request);
    }

    public void favoritesForUser(Context applicationContext, Response.Listener<JSONObject> callback,
                               Response.ErrorListener errorListener, Integer idCity) {
        JSONObject object = UserInstance.toJSON(applicationContext);
        try {
            object.put("idCiudad", String.valueOf(idCity));
        } catch (JSONException e) {
            Log.e("ERROR", e.toString());
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, BASE_URL + OBTENER_FAVORITOS, object,
                callback, errorListener);
        APIAccessor.getInstance(applicationContext).addToRequestQueue(request);
    }

}
