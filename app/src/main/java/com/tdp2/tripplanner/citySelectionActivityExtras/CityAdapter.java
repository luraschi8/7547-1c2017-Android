package com.tdp2.tripplanner.citySelectionActivityExtras;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tdp2.tripplanner.CityMenuActivity;
import com.tdp2.tripplanner.R;
import com.tdp2.tripplanner.modelo.City;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by matias on 3/16/17.
 */

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder> {
    private List<City> items;
    private CityFilter filter;
    private final Context mContext;

    public class CityViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView country;
        public ImageView image;
        public final Context mContext;

        public CityViewHolder (View view, Context context){
            super(view);
            mContext = context;
            name = (TextView) view.findViewById(R.id.name);
            country = (TextView) view.findViewById(R.id.country);
            image = (ImageView) view.findViewById(R.id.cityImage);
            RelativeLayout bg = (RelativeLayout) view.findViewById(R.id.cityRelativeView);
            bg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(CityViewHolder.this.mContext, CityMenuActivity.class);
                    CityDataHolder.setData(items.get(getAdapterPosition()));
                    CityViewHolder.this.mContext.startActivity(intent);
                }
            });
        }
    }

    public CityAdapter (List<City> items, Context context) {
        Collections.sort(items, new Comparator<City>() {
            @Override
            public int compare(City city, City t1) {
                return city.getName().compareTo(t1.getName());
            }
        });
        this.mContext = context;
        this.setList(items);
        this.filter = new CityFilter(this.items, this);
    }

    public CityAdapter.CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.city_card_view, parent, false);
        return new CityViewHolder(v, mContext);
    }

    @Override
    public void onBindViewHolder(CityAdapter.CityViewHolder holder, int position) {
        holder.name.setText(items.get(position).getName());
        holder.country.setText(items.get(position).getCountry());
        holder.image.setImageBitmap(items.get(position).getImage());

    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    // set adapter filtered list
    public void setList(List<City> list) {
        Collections.sort( list,new Comparator<City>() {
            @Override
            public int compare(City city, City t1) {
                return city.getName().compareTo(t1.getName());
            }
        });
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
    }

    public City getCityAtPosition(int position) {
        return this.items.get(position);
    }
}