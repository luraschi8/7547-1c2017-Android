package com.tdp2.tripplanner.citySelectionActivityExtras;

import android.location.Location;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
    private Integer selectedItem = 0; //Initial selected item.
    private CityFilter filter;

    public class CityViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView country;
        public ImageView image;

        public CityViewHolder (View view){
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            country = (TextView) view.findViewById(R.id.country);
            image = (ImageView) view.findViewById(R.id.cityImage);
        }
    }

    public CityAdapter (List<City> items) {
        Collections.sort(items, new Comparator<City>() {
            @Override
            public int compare(City city, City t1) {
                return city.getName().compareTo(t1.getName());
            }
        });
        this.items = items;
        this.filter = new CityFilter(this.items, this);
    }

    public CityAdapter.CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.city_card_view, parent, false);
        return new CityViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CityAdapter.CityViewHolder holder, int position) {
        holder.name.setText(items.get(position).getName());
        holder.country.setText(items.get(position).getCountry());
        holder.image.setImageResource(items.get(position).getImage());
        holder.itemView.setSelected(selectedItem == position);
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public void changeSelectedItem (Integer newSelected) {
        notifyItemChanged(selectedItem);
        selectedItem = newSelected;
        notifyItemChanged(selectedItem);
    }

    // set adapter filtered list
    public void setList(List<City> list) {
        this.items = list;
    }
    //call when you want to filter
    public void filterList(String text) {
        filter.filter(text);
    }

    public void selectByLocation(Location location) {
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
        this.changeSelectedItem(currentIdx);
    }

    public City getSelectedCity() {
        return this.items.get(this.selectedItem);
    }
}