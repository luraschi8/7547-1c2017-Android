package com.tdp2.tripplanner.attractionSelectionActivityExtras;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.tdp2.tripplanner.AttractionDetailActivity;
import com.tdp2.tripplanner.R;
import com.tdp2.tripplanner.dao.APIDAO;
import com.tdp2.tripplanner.helpers.AddToFavoritesResponseListener;
import com.tdp2.tripplanner.helpers.FavController;
import com.tdp2.tripplanner.helpers.RemoveFavoriteResponseListener;
import com.tdp2.tripplanner.modelo.Attraction;
import com.tdp2.tripplanner.modelo.UserInstance;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by matias on 3/25/17.
 */

public class AttractionAdapter extends RecyclerView.Adapter<AttractionAdapter.MyViewHolder> {

    private Context mContext;
    private APIDAO dao;
    private List<Attraction> attractionList;
    private AttractionFilter filter;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView thumbnail, fav;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.attractionName);
            fav = (ImageView) view.findViewById(R.id.favAttraction);
            thumbnail = (ImageView) view.findViewById(R.id.atractionImage);
            thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, AttractionDetailActivity.class);
                    AttractionDataHolder.setData(attractionList.get(getAdapterPosition()));
                    mContext.startActivity(intent);
                }
            });
        }

    }


    public AttractionAdapter(Context mContext, List<Attraction> attractionList) {
        this.mContext = mContext;
        this.attractionList = attractionList;
        this.dao = new APIDAO();
        this.filter = new AttractionFilter(this.attractionList, this);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.atrraction_card_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Attraction attraction = attractionList.get(position);
        holder.title.setText(attraction.getName());
        holder.thumbnail.setImageBitmap(attraction.getMainImage());
        if (attraction.isFavorite()) holder.fav.setImageResource(R.drawable.heart);
        holder.fav.setOnClickListener(new FavController(mContext, attraction, holder.fav));
    }

    // set adapter filtered list
    public void setList(List<Attraction> list) {
        Collections.sort(list, new Comparator<Attraction>() {
            @Override
            public int compare(Attraction attraction, Attraction t1) {
                return attraction.getName().compareTo(t1.getName());
            }
        });
        this.attractionList = list;
    }
    //call when you want to filter
    public void filterList(String text) {
        filter.filter(text);
    }

    @Override
    public int getItemCount() {
        return attractionList.size();
    }

}
