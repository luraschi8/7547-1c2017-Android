package com.tdp2.tripplanner.attractionSelectionActivityExtras;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tdp2.tripplanner.AtractionGridViewActivity;
import com.tdp2.tripplanner.AttractionDetailActivity;
import com.tdp2.tripplanner.R;
import com.tdp2.tripplanner.modelo.Attraction;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by matias on 4/21/17.
 */

public class MapHandler implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private AtractionGridViewActivity mOwner;
    private Hashtable<String, Attraction> markers;
    private ArrayList<Attraction> attractionList;

    public MapHandler (AtractionGridViewActivity mOwner) {
        this.mOwner = mOwner;
        markers = new Hashtable<>();
        this.attractionList = new ArrayList<>();
    }

    public void setList (ArrayList<Attraction> attractionList) {
        this.attractionList = attractionList;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
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
            title.setText(String.format("%s >", markedAttraction.getName()));

            return view;
        }

        @Override
        public View getInfoWindow(final Marker marker) {
            return null;
        }
    }
}
