package com.tdp2.tripplanner;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import com.facebook.FacebookSdk;

/**
 * Created by Casa on 3/5/2017.
 */

public class Trips extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.e("Trips APP", "FacebookSdk.sdkInitialize");
        FacebookSdk.sdkInitialize(getApplicationContext());

        Trips.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return Trips.context;
    }
}
