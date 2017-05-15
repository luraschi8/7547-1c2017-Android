package com.tdp2.tripplanner.attractionDetailActivityExtras;

import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tdp2.tripplanner.AttractionDetailActivity;
import com.tdp2.tripplanner.attractionSelectionActivityExtras.AttractionDataHolder;
import com.tdp2.tripplanner.dao.APIDAO;
import com.tdp2.tripplanner.modelo.Comment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by matias on 5/12/17.
 */

public class CommentsDownloader implements Response.Listener<JSONObject>, Response.ErrorListener {

    private AttractionDetailActivity activity;
    private Integer nextPageNumber;
    private APIDAO dao;
    private Boolean reachedLastPage;

    public CommentsDownloader(AttractionDetailActivity activity) {
        this.nextPageNumber = 0;
        this.activity = activity;
        this.dao = new APIDAO();
        reachedLastPage = false;
    }

    public void getNextPage() {
        if (reachedLastPage) return;;
        this.dao.getComments(this.activity.getBaseContext(), this, this,
                AttractionDataHolder.getData().getId(), this.nextPageNumber);
        nextPageNumber++;
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(JSONObject response) {
        ArrayList<Comment> newComments = new ArrayList<Comment>();
        try {
            JSONArray comments = response.getJSONArray("data");
            for (int i=0; i < comments.length(); i++) {
                JSONObject currentComment = comments.getJSONObject(i);
                String dateTime = currentComment.getString("fecha");
                Double rating = currentComment.getDouble("calificacion");
                newComments.add(new Comment(currentComment.getString("comentario"),
                        currentComment.getString("nombreUsuario"), dateTime, rating.floatValue()));
            }
        } catch (JSONException e) {
            Log.e("ERROR JSON", e.getMessage());
            return;
        }
        if (newComments.size() == 0){
            reachedLastPage = true;
            return;
        }
        this.activity.appendComments(newComments);
    }
}
