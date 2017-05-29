package com.tdp2.tripplanner.ToursExtrasd;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.tdp2.tripplanner.AttractionDetailActivity;
import com.tdp2.tripplanner.AttractionSelectionActivity;
import com.tdp2.tripplanner.AttractionsInTourActivity;
import com.tdp2.tripplanner.R;
import com.tdp2.tripplanner.attractionSelectionActivityExtras.AttractionDataHolder;
import com.tdp2.tripplanner.attractionSelectionActivityExtras.MapHandler;
import com.tdp2.tripplanner.modelo.Attraction;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import static com.tdp2.tripplanner.helpers.LocationService.MY_PERMISSIONS_REQUEST_FINE_LOCATION;

/**
 * Created by Casa on 28/5/2017.
 */

public class MapTourHandler implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private AttractionsInTourActivity mOwner;
    private Hashtable<String, Attraction> markers;
    private ArrayList<Attraction> attractionList;
    private Boolean locationPermission;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private GoogleMap mMap;
    ArrayList<LatLng> MarkerPoints;

    public MapTourHandler (AttractionsInTourActivity mOwner) {
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
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8));
                    }
                    return false;
                }
            });
        }
        googleMap.setInfoWindowAdapter(new MapTourHandler.CustomInfoWindowAdapter());
        googleMap.setOnInfoWindowClickListener(this);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        Double sumLat = 0D, sumLong =0D;
        LatLng previousLat = null;
        LatLng firstLat =  new LatLng(attractionList.get(0).getLatitude(), attractionList.get(0).getLongitude());
        for(int i = 0; i<attractionList.size(); i++) {
            Attraction attraction = attractionList.get(i);
            sumLat = sumLat + attraction.getLatitude();
            sumLong = sumLong + attraction.getLongitude();
            LatLng currentLat = new LatLng(attraction.getLatitude(), attraction.getLongitude());
            final Marker currentMarker =  googleMap.addMarker(new MarkerOptions().position(currentLat).title(attraction.getName()));
            markers.put(currentMarker.getId(), attraction);

            if (previousLat == null) {
                previousLat = currentLat;
                continue;
            }


            // Checks, whether start and end locations are captured
            LatLng origin = previousLat;
            LatLng dest = currentLat;

            // Getting URL to the Google Directions API
            String url = getUrl(origin, dest);
            Log.d("onMapClick", url.toString());
            FetchUrl FetchUrl = new FetchUrl();

            // Start downloading json data from Google Directions API
            FetchUrl.execute(url);
            previousLat = currentLat;
            //move map camera
        }

        LatLng lastLat =  new LatLng(attractionList.get(attractionList.size()-1).getLatitude(), attractionList.get(attractionList.size()-1).getLongitude());
        String url = getUrl(lastLat, firstLat);
        Log.d("onMapClick", url.toString());
        FetchUrl FetchUrl = new FetchUrl();

        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(sumLat / attractionList.size(), sumLong / attractionList.size()), 12.0f));
    }

    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
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


    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
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
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        this.checkForLocationPermission();
        if (this.locationPermission) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            this.onLocationChanged(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));
        }

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
            view = MapTourHandler.this.mOwner.getLayoutInflater().inflate(R.layout.map_info_window, null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            final Attraction markedAttraction = markers.get(marker.getId());

            view = MapTourHandler.this.mOwner.getLayoutInflater().inflate(R.layout.map_info_window, null);

            //Get map badge
            ImageView img = (ImageView) view.findViewById(R.id.map_badge);
            img.setImageBitmap(markedAttraction.getMainImage());

            //Get view title
            TextView title = (TextView) view.findViewById(R.id.map_title);
            title.setText(String.format("%s >", markedAttraction.getName()));

            return view;
        }

        @Override
        public View getInfoWindow(final Marker marker) {
            return null;
        }
    }
}