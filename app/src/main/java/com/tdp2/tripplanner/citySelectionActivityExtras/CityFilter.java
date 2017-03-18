package com.tdp2.tripplanner.citySelectionActivityExtras;

import android.widget.Filter;

import com.tdp2.tripplanner.modelo.City;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matias on 3/18/17.
 */

public class CityFilter extends Filter {

    private List<City> cities;
    private List<City> filteredCities;
    private CityAdapter adapter;

    public CityFilter(List<City> cities, CityAdapter adapter) {
        this.cities = new ArrayList<>(cities);
        this.adapter = adapter;
        this.filteredCities = new ArrayList<>(cities);
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        filteredCities.clear();
        final FilterResults results = new FilterResults();
        String currentName;
        String currentCountry;

        //Do filtering here
        for (final City item : cities) {
            currentName = item.getName().toLowerCase();
            currentCountry = item.getCountry().toLowerCase();
            if (currentName.contains(constraint) || currentCountry.contains(constraint)) {
                filteredCities.add(item);
            }
        }

        results.values = filteredCities;
        results.count = filteredCities.size();
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.setList(filteredCities);
        adapter.notifyDataSetChanged();
    }
}
