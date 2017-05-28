package com.tdp2.tripplanner.ToursExtrasd;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tdp2.tripplanner.AttractionDetailActivity;
import com.tdp2.tripplanner.R;
import com.tdp2.tripplanner.attractionSelectionActivityExtras.AttractionDataHolder;
import com.tdp2.tripplanner.modelo.Attraction;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Casa on 28/5/2017.
 */

public class AttractionInTourAdapter extends RecyclerView.Adapter<AttractionInTourAdapter.AttractionInTourViewHolder> {

    private Context mContext;
    private List<Attraction> attractionList;

    @Override
    public AttractionInTourViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.attraction_in_tour_card_view, parent, false);

        return new AttractionInTourViewHolder(itemView, mContext);
    }

    @Override
    public void onBindViewHolder(AttractionInTourViewHolder holder, int position) {
        Attraction attraction = attractionList.get(position);
        holder.attractionTitle.setText(attraction.getOrder()+". "+attraction.getName());
        holder.attractionImage.setImageBitmap(attraction.getMainImage());
    }

    @Override
    public int getItemCount() {
        return attractionList.size();
    }

    public AttractionInTourAdapter(Context mContext, List<Attraction> attractionList){
        Collections.sort(attractionList, new Comparator<Attraction>() {
            @Override
            public int compare(Attraction attraction1, Attraction attraction2) {
                if (attraction1.order > attraction2.order) return 1;
                else if (attraction1.order == attraction2.order) return 0;
                else return -1;
            }
        });
        this.mContext = mContext;
        this.attractionList = attractionList;
    }

    public class AttractionInTourViewHolder extends RecyclerView.ViewHolder {
        public TextView attractionTitle;
        public ImageView attractionImage;
        public Context mContext;
        public RelativeLayout card;

        public AttractionInTourViewHolder(View view, Context context) {
            super(view);
            mContext = context;
            attractionTitle = (TextView) view.findViewById(R.id.attraction_in_tour_name);
            attractionImage = (ImageView) view.findViewById(R.id.attraction_in_tour_image);
            card = (RelativeLayout) view.findViewById(R.id.attraction_in_tour_card);

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, AttractionDetailActivity.class);
                    AttractionDataHolder.setData(attractionList.get(getAdapterPosition()));
                    mContext.startActivity(intent);
                }
            });
        }

    }
}
