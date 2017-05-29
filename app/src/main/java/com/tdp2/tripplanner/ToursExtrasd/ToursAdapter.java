package com.tdp2.tripplanner.ToursExtrasd;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tdp2.tripplanner.AttractionsInTourActivity;
import com.tdp2.tripplanner.R;
import com.tdp2.tripplanner.attractionSelectionActivityExtras.AttractionAdapter;
import com.tdp2.tripplanner.modelo.Tour;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Casa on 27/5/2017.
 */

public class ToursAdapter extends RecyclerView.Adapter<ToursAdapter.TourViewHolder>{


    private Context mContext;
    private List<Tour> tourList;

    public ToursAdapter(List<Tour> tours, Context context) {
        Collections.sort(tours, new Comparator<Tour>() {
            @Override
            public int compare(Tour tour, Tour t1) {
                return tour.name.compareTo(t1.name);
            }
        });
        mContext = context;
        tourList = tours;
    }

    @Override
    public ToursAdapter.TourViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tour_card_view, parent, false);

        return new ToursAdapter.TourViewHolder(itemView, mContext);
    }

    @Override
    public void onBindViewHolder(ToursAdapter.TourViewHolder holder, int position) {
        Tour tour = tourList.get(position);
        holder.cardViewMainButton.setText(tour.name);
    }

    @Override
    public int getItemCount() {
        return tourList.size();
    }

    public class TourViewHolder extends RecyclerView.ViewHolder {
        public TextView cardViewMainButton;

        private final Context tourContext;
        public TourViewHolder(View view, Context context) {
            super(view);
            tourContext = context;
            cardViewMainButton = (TextView) view.findViewById(R.id.tour_button);
            cardViewMainButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(tourContext, AttractionsInTourActivity.class);
                    TourDataHolder.setData(tourList.get(getAdapterPosition()));
                    tourContext.startActivity(intent);
                }
            });
        }

    }
}
