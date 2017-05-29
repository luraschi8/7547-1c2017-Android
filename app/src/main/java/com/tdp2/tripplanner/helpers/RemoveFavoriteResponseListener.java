package com.tdp2.tripplanner.helpers;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tdp2.tripplanner.R;
import com.tdp2.tripplanner.modelo.Attraction;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by matias on 5/28/17.
 */

public class RemoveFavoriteResponseListener implements Response.Listener<JSONObject>, Response.ErrorListener {

    private Context context;
    private ImageView buttonToToggle;
    private Attraction attraction;

    public RemoveFavoriteResponseListener(Context context, ImageView buttonToToggle, Attraction attraction) {
        this.context = context;
        this.buttonToToggle = buttonToToggle;
        this.attraction = attraction;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e("ERROR", error.toString());
    }


    @Override
    public void onResponse(JSONObject response) {
        attraction.setFavorite(false);
        buttonToToggle.setImageResource(R.drawable.heart_outline);
        Toast.makeText(this.context, R.string.remove_favorites, Toast.LENGTH_SHORT).show();
    }
}
