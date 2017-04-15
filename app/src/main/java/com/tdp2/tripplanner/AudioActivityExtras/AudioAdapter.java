package com.tdp2.tripplanner.AudioActivityExtras;



import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.tdp2.tripplanner.R;
import com.tdp2.tripplanner.modelo.InterestingPoint;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by matias on 3/16/17.
 */

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.InterestingPointViewHolder> {
    private List<InterestingPoint> items;

    public  class InterestingPointViewHolder extends RecyclerView.ViewHolder{
        public TextView order;
        public TextView name;
        public ImageView image;

        public InterestingPointViewHolder (View view){
            super(view);
            order = (TextView) view.findViewById(R.id.orderText);
            name = (TextView) view.findViewById(R.id.nameText);
            image = (ImageView) view.findViewById(R.id.interestingPointImage);
        }
    }

    public AudioAdapter(List<InterestingPoint> items) {
        Collections.sort(items, new Comparator<InterestingPoint>() {
            @Override
            public int compare(InterestingPoint ipoint, InterestingPoint t1) {
                return ipoint.name.compareTo(t1.name);
            }
        });
        this.items = items;

    }

    public AudioAdapter.InterestingPointViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.interesting_point_card_view, parent, false);
        return new InterestingPointViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AudioAdapter.InterestingPointViewHolder holder, int position) {
        holder.order.setText(String.valueOf(items.get(position).order));
        holder.name.setText(items.get(position).name);
        holder.image.setImageResource(items.get(position).image);
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    /*// set adapter filtered list
    public void setList(List<City> list) {
        this.items = list;
    }
    //call when you want to filter
    public void filterList(String text) {
        filter.filter(text);
    }

    public Integer selectByLocation(Location location) {
        double currentMin = -1;
        Integer currentIdx = -1;
        double latDif;
        double mod;
        double longDif;
        City city;
        for (int i = 0; i < this.items.size(); i++) {
            city = this.items.get(i);
            latDif = location.getLatitude() - city.getLatitude();
            longDif = location.getLongitude() - city.getLongitude();
            mod = Math.sqrt((latDif * latDif) + (longDif * longDif));
            if (currentMin == -1) {
                currentMin = mod;
                currentIdx = i;
            }
            if (currentMin > mod) {
                currentMin = mod;
                currentIdx = i;
            }
        }
        return currentIdx;
    }*/

    public InterestingPoint getCityAtPosition(Integer position) {
        return this.items.get(position);
    }
}