package com.tdp2.tripplanner.AudioActivityExtras;



import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tdp2.tripplanner.InterestingPointDetailActivity;
import com.tdp2.tripplanner.R;
import com.tdp2.tripplanner.modelo.InterestingPoint;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by matias on 3/16/17.
 */

public class InterestingPointAdapter extends RecyclerView.Adapter<InterestingPointAdapter.InterestingPointViewHolder> {
    private List<InterestingPoint> items;
    private final Context mContext;

    public  class InterestingPointViewHolder extends RecyclerView.ViewHolder{
        public TextView order;
        public TextView name;
        public ImageView image;
        public final Context mContext;

        public InterestingPointViewHolder (View view, Context context){
            super(view);
            order = (TextView) view.findViewById(R.id.orderText);
            name = (TextView) view.findViewById(R.id.nameText);
            image = (ImageView) view.findViewById(R.id.interestingPointImage);
            mContext = context;
            LinearLayout bg = (LinearLayout) view.findViewById(R.id.action_interesting_point);
            bg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(InterestingPointAdapter.InterestingPointViewHolder.this.mContext, InterestingPointDetailActivity.class);
                    InterestingPointDataHolder.setData(items.get(getAdapterPosition()));
                    InterestingPointAdapter.InterestingPointViewHolder.this.mContext.startActivity(intent);
                }
            });
        }
    }

    public InterestingPointAdapter(List<InterestingPoint> items, Context context) {
        Collections.sort(items, new Comparator<InterestingPoint>() {
            @Override
            public int compare(InterestingPoint ipoint, InterestingPoint t1) {
                if (ipoint.order > t1.order) return 1;
                else if (ipoint.order == t1.order) return 0;
                else return -1;
            }
        });
        this.items = items;
        this.mContext = context;


    }

    public InterestingPointAdapter.InterestingPointViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.interesting_point_card_view, parent, false);
        return new InterestingPointViewHolder(v,mContext);
    }

    @Override
    public void onBindViewHolder(InterestingPointAdapter.InterestingPointViewHolder holder, int position) {
        holder.order.setText(String.valueOf(items.get(position).order));
        holder.name.setText(items.get(position).name);
        holder.image.setImageBitmap(items.get(position).image);
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    // set adapter filtered list
    public void setList(List<InterestingPoint> list) {
        this.items = list;
    }

}