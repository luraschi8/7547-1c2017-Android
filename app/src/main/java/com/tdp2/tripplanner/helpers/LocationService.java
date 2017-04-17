package com.tdp2.tripplanner.helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.util.ArrayList;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by matias on 3/19/17.
 */

public class LocationService implements LocationListener {
    public static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1001;

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    //The minimum distance to change updates in meters
    private long minDistanceChange = 50; // 50 meters default

    //The minimum time beetwen updates in milliseconds
    private long minTimeBtwUpdate = 1000 * 60 * 1;//1000 * 60 * 1 = 1 minute default

    private LocationManager locationManager;
    private Boolean isGPSEnabled;
    private Boolean isNetworkEnabled;
    private Boolean locationServiceAvailable;
    private Boolean locationServiceStarted;
    private LocationRequester callback;
    private Context context;
    private Location currentBestLocation;

    /**
     * Constructor
     */
    public LocationService (Context context, LocationRequester callback) {
        this.callback = callback;
        this.context = context;
        this.locationServiceStarted = this.initLocationService(this.context);
        Log.i("LOCATION", "Location Service created.");
    }

    /**
     * Sets up location service after permissions is granted
     */
    private Boolean initLocationService(Context context) {


        if ( ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION )
                != PackageManager.PERMISSION_GRANTED ) {
            this.locationServiceAvailable = false;
            Log.e("LOCATION", "Permission not granted");
            return false;
        }

        try   {
            this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            // Get GPS and network status
            this.isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            this.isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


            if (!this.anyLocationServiceAvailable())    {
                // cannot get location
                this.locationServiceAvailable = false;
                return false;
            }
            else
            {
                this.locationServiceAvailable = true;
                //Try GPS First
                if (this.isGPSEnabled)  {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            this.minTimeBtwUpdate, this.minDistanceChange, this);
                    //Supply last location till GPS warms up.
                    this.onLocationChanged(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
                }
                if(isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            this.minTimeBtwUpdate, this.minDistanceChange, this);
                    //Supply last location till GPS warms up.
                    this.onLocationChanged(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
                }
                return true;
            }
        } catch (Exception ex)  {
            Log.e("LOCATION ERROR" ,"Error creating location service: " + ex.getMessage() );
            return false;
        }
    }

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     */
    protected boolean isBetterLocation(Location location) {
        if (this.currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


    @Override
    public void onLocationChanged(Location location) {
        if ( this.isBetterLocation(location) ) {
            this.currentBestLocation = location;
            this.callback.updateLocation(location);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.i("LOCATION", "Status changed " + s);
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.i("LOCATION", "Provider enabled " + s);
        if (s.equals("gps")) isGPSEnabled = true;
        if (s.equals("network")) isNetworkEnabled = true;
        this.locationServiceAvailable = this.anyLocationServiceAvailable();
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.i("LOCATION", "Provider disabled " + s);
        if (s.equals("gps")) isGPSEnabled = false;
        if (s.equals("network")) isNetworkEnabled = false;
        this.locationServiceAvailable = this.anyLocationServiceAvailable();
    }

    public Boolean isAvailable() {
        return this.locationServiceAvailable;
    }


    private Boolean anyLocationServiceAvailable() {
        return isNetworkEnabled || isGPSEnabled;
    }
}
