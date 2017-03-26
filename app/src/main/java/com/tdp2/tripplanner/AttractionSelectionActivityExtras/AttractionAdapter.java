package com.tdp2.tripplanner.AttractionSelectionActivityExtras;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tdp2.tripplanner.R;
import com.tdp2.tripplanner.modelo.Attraction;

import java.util.List;

/**
 * Created by matias on 3/25/17.
 */

public class AttractionAdapter extends RecyclerView.Adapter<AttractionAdapter.MyViewHolder> {

    private Context mContext;
    private List<Attraction> attractionList;
    private AttractionFilter filter;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView thumbnail, more, fav, bookmark;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.attractionName);
            thumbnail = (ImageView) view.findViewById(R.id.atractionImage);
            more = (ImageView) view.findViewById(R.id.moreAttraction);
            fav = (ImageView) view.findViewById(R.id.favAttraction);
            fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext, R.string.attraction_faved, Toast.LENGTH_SHORT).show();
                }
            });
            bookmark = (ImageView) view.findViewById(R.id.saveAttraction);
        }
    }


    public AttractionAdapter(Context mContext, List<Attraction> attractionList) {
        this.mContext = mContext;
        this.attractionList = attractionList;
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
        holder.thumbnail.setImageResource(attraction.getImage());
        //TODO if already bookmarked draw the other icon, the same for favorites
    }

    // set adapter filtered list
    public void setList(List<Attraction> list) {
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
