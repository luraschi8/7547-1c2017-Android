package com.tdp2.tripplanner.citySelectionActivityExtras;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by matias on 5/26/17.
 */

public class UserRegistrationListener implements Response.Listener<JSONObject>, Response.ErrorListener {


    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e("ACCESS_ERROR", error.toString());
    }

    @Override
    public void onResponse(JSONObject response) {
        Log.e("ACCESS_SUCCESS", "Access successfully registered.");
    }
}
