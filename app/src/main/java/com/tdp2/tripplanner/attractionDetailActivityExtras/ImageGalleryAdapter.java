package com.tdp2.tripplanner.attractionDetailActivityExtras;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tdp2.tripplanner.FullscreenImageViewActivity;
import com.tdp2.tripplanner.R;
import com.tdp2.tripplanner.VideoPlayerActivity;

import java.util.ArrayList;

/**
 * Created by matias on 4/14/17.
 */

public class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.ImageViewHolder> {

    private ArrayList<GalleryContent> imageList;
    private Context mContext;

    public ImageGalleryAdapter(ArrayList<GalleryContent> images, Context context) {
        this.imageList = images;
        this.mContext = context;
    }

    @Override
    public ImageGalleryAdapter.ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.attraction_image_gallery_card_view, parent, false);

        return new ImageGalleryAdapter.ImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ImageGalleryAdapter.ImageViewHolder holder, final int position) {
        final Bitmap image = imageList.get(position).getImage();
        String type = imageList.get(position).getType();
        holder.thumbnail.setImageBitmap(image);
        if (type.equals("img")) {
            holder.thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, FullscreenImageViewActivity.class);
                    intent.putExtra("POSITION", position);
                    mContext.startActivity(intent);
                }
            });
        } else if (type.equals("vid")) {
            holder.thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, VideoPlayerActivity.class);
                    intent.putExtra("URL", imageList.get(position).getUrl());
                    mContext.startActivity(intent);
                }
            });
        }
    }

    // set adapter filtered list
    public void setList(ArrayList<GalleryContent> list) {
        this.imageList = list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }


    public class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView thumbnail;

        public ImageViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.image_gallery);
        }
    }
}
