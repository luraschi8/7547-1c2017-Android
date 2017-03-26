package com.tdp2.tripplanner.attractionSelectionActivityExtras;

import android.widget.Filter;
import com.tdp2.tripplanner.modelo.Attraction;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by matias on 3/26/17.
 */

public class AttractionFilter extends Filter {
    private List<Attraction> attractions;
    private List<Attraction> filteredAttractions;
    private AttractionAdapter adapter;

    public AttractionFilter(List<Attraction> attractions, AttractionAdapter adapter) {
        this.attractions = new ArrayList<>(attractions);
        this.adapter = adapter;
        this.filteredAttractions = new ArrayList<>(attractions);
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        filteredAttractions.clear();
        final FilterResults results = new FilterResults();
        String currentName;

        //Do filtering here
        for (final Attraction item : attractions) {
            currentName = item.getName().toLowerCase();
            if (currentName.contains(constraint) ) {
                filteredAttractions.add(item);
            }
        }

        results.values = filteredAttractions;
        results.count = filteredAttractions.size();
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.setList(filteredAttractions);
        adapter.notifyDataSetChanged();
    }



}
