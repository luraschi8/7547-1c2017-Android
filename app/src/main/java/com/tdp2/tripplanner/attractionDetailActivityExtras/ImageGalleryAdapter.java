package com.tdp2.tripplanner.attractionDetailActivityExtras;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tdp2.tripplanner.AttractionDetailActivity;
import com.tdp2.tripplanner.R;

import java.util.ArrayList;

/**
 * Created by matias on 4/14/17.
 */

public class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.ImageViewHolder> {

    private ArrayList<Bitmap> imageList;

    public ImageGalleryAdapter(ArrayList<Bitmap> images) {
        this.imageList = images;
    }

    @Override
    public ImageGalleryAdapter.ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.attraction_image_gallery_card_view, parent, false);

        return new ImageGalleryAdapter.ImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ImageGalleryAdapter.ImageViewHolder holder, int position) {
        Bitmap image = imageList.get(position);
        holder.thumbnail.setImageBitmap(image);
    }

    // set adapter filtered list
    public void setList(ArrayList<Bitmap> list) {
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
