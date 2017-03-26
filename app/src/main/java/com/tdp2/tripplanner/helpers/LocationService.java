package com.tdp2.tripplanner.helpers;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by matias on 3/19/17.
 */

public class LocationService implements LocationListener {

    public static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1001;

    //The minimum distance to change updates in meters
    private long minDistanceChange = 0; // 10 meters

    //The minimum time beetwen updates in milliseconds
    private long minTimeBtwUpdate = 0;//1000 * 60 * 1; // 1 minute

    private LocationManager locationManager;
    private Boolean isGPSEnabled;
    private Boolean isNetworkEnabled;
    private Boolean locationServiceAvailable;
    private LocationRequester callback;
    private Context context;

    /**
     * Constructor
     */
    public LocationService (Context context, Long minDistanceChange, Long minTimeBtwUpdates, LocationRequester callback) {
        this.minDistanceChange = minDistanceChange;
        this.minTimeBtwUpdate = minTimeBtwUpdates;
        this.callback = callback;
        this.context = context;
        initLocationService(this.context);
        Log.i("LOCATION", "Location Service created.");
    }



    /**
     * Sets up location service after permissions is granted
     */
    private void initLocationService(Context context) {


        if ( ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION )
                != PackageManager.PERMISSION_GRANTED ) {
            this.locationServiceAvailable = false;
            Log.e("LOCATION", "Permission not granted");
        }

        try   {
            this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            // Get GPS and network status
            this.isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            this.isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


            if (!isNetworkEnabled && !isGPSEnabled)    {
                // cannot get location
                this.locationServiceAvailable = false;
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
                } else {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            this.minTimeBtwUpdate, this.minDistanceChange, this);
                    //Supply last location till GPS warms up.
                    this.onLocationChanged(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
                }

            }
        } catch (Exception ex)  {
            Log.e("LOCATION ERROR" ,"Error creating location service: " + ex.getMessage() );

        }
    }
    @Override
    public void onLocationChanged(Location location) {
        this.callback.updateLocation(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.i("LOCATION", "Status changed " + s);
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.i("LOCATION", "Provider enabled " + s);
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.i("LOCATION", "Provider disabled " + s);
    }

    public Boolean isAvailable() {
        return this.locationServiceAvailable;
    }

    public void stopLocationServices() {
        this.locationManager.removeUpdates(this);
    }
}
