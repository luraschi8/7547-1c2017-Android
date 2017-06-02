package com.tdp2.tripplanner.attractionSelectionActivityExtras;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tdp2.tripplanner.AttractionSelectionActivity;
import com.tdp2.tripplanner.AttractionDetailActivity;
import com.tdp2.tripplanner.R;
import com.tdp2.tripplanner.modelo.Attraction;

import java.util.ArrayList;
import java.util.Hashtable;

import static com.tdp2.tripplanner.helpers.LocationService.MY_PERMISSIONS_REQUEST_FINE_LOCATION;

/**
 * Created by matias on 4/21/17.
 */

public class MapHandler implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private Activity mOwner;
    private Hashtable<String, Attraction> markers;
    private ArrayList<Attraction> attractionList;
    private Boolean locationPermission;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private GoogleMap mMap;

    public MapHandler (Activity mOwner) {
        this.mOwner = mOwner;
        this.markers = new Hashtable<>();
        this.attractionList = new ArrayList<>();
        this.locationPermission = false;
    }

    public void setList (ArrayList<Attraction> attractionList) {
        this.attractionList = attractionList;
    }

    public void setLocationPermission(Boolean value) {
        this.locationPermission = value;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        this.checkForLocationPermission();
        if (this.locationPermission) {
            buildGoogleApiClient();
            googleMap.setMyLocationEnabled(true);
            googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    if (mLastLocation != null) {
                        LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                    }
                    return false;
                }
            });
        }
        googleMap.setInfoWindowAdapter(new MapHandler.CustomInfoWindowAdapter());
        googleMap.setOnInfoWindowClickListener(this);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        Double sumLat = 0D, sumLong =0D;
        for (Attraction attraction : attractionList){
            sumLat = sumLat + attraction.getLatitude();
            sumLong = sumLong + attraction.getLongitude();
            LatLng currentLat = new LatLng(attraction.getLatitude(), attraction.getLongitude());
            final Marker currentMarker =  googleMap.addMarker(new MarkerOptions().position(currentLat).title(attraction.getName()));
            markers.put(currentMarker.getId(), attraction);
        }
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(sumLat / attractionList.size(), sumLong / attractionList.size()), 12.0f));
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        final Attraction markedAttraction = markers.get(marker.getId());
        Intent intent = new Intent(this.mOwner, AttractionDetailActivity.class);
        AttractionDataHolder.setData(markedAttraction);
        this.mOwner.startActivity(intent);
    }

    public void checkForLocationPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this.mOwner, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.mOwner,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        }
        else this.locationPermission = true;
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this.mOwner)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        this.checkForLocationPermission();
        if (this.locationPermission) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

        this.onLocationChanged(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.isSuccess()) {
            Log.d("DEBUG", connectionResult.toString());
        }
    }


    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;
    }

    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private View view;

        public CustomInfoWindowAdapter() {
            view = MapHandler.this.mOwner.getLayoutInflater().inflate(R.layout.map_info_window, null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            final Attraction markedAttraction = markers.get(marker.getId());

            view = MapHandler.this.mOwner.getLayoutInflater().inflate(R.layout.map_info_window, null);

            //Get map badge
            ImageView img = (ImageView) view.findViewById(R.id.map_badge);
            img.setImageBitmap(markedAttraction.getMainImage());

            //Get view title
            TextView title = (TextView) view.findViewById(R.id.map_title);
            title.setText(String.format("%s", markedAttraction.getName()));

            return view;
        }

        @Override
        public View getInfoWindow(final Marker marker) {
            return null;
        }
    }
}
