package com.tdp2.tripplanner.attractionDetailActivityExtras;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.tdp2.tripplanner.R;
import com.tdp2.tripplanner.modelo.Comment;

import java.util.ArrayList;

/**
 * Created by matias on 5/12/17.
 */

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    private ArrayList<Comment> commentsList;

    public CommentsAdapter() {
        this.commentsList = new ArrayList<Comment>();
    }

    @Override
    public CommentsAdapter.CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_card_view, parent, false);

        return new CommentsAdapter.CommentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CommentsAdapter.CommentViewHolder holder, final int position) {
        holder.ratingBar.setRating(this.commentsList.get(position).getRating());
        holder.usernameTextview.setText(this.commentsList.get(position).getUsername());
        holder.dateTextView.setText(this.commentsList.get(position).getDateAndTime());
        holder.commentTextView.setText(this.commentsList.get(position).getComment());
    }

    public void setList(ArrayList<Comment> list) {
        Integer curSize = this.getItemCount();
        this.commentsList.addAll(list);
        this.notifyItemRangeInserted(curSize, this.getItemCount() - 1);
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        private RatingBar ratingBar;
        private TextView usernameTextview;
        private TextView dateTextView;
        private TextView commentTextView;

        public CommentViewHolder(View view) {
            super(view);
            ratingBar = (RatingBar) view.findViewById(R.id.comment_rating_bar);
            usernameTextview = (TextView) view.findViewById(R.id.comment_user);
            dateTextView = (TextView) view.findViewById(R.id.comment_fecha_hora);
            commentTextView = (TextView) view.findViewById(R.id.comment_text);
        }

    }
}
