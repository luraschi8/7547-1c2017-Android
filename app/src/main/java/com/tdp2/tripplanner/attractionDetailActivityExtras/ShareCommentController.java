package com.tdp2.tripplanner.attractionDetailActivityExtras;

import android.content.Context;
import android.media.Rating;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tdp2.tripplanner.AttractionDetailActivity;
import com.tdp2.tripplanner.R;
import com.tdp2.tripplanner.dao.APIDAO;
import com.tdp2.tripplanner.modelo.Comment;

import org.json.JSONObject;

/**
 * Created by matias on 5/14/17.
 */

public class ShareCommentController implements View.OnClickListener, Response.Listener<JSONObject>, Response.ErrorListener {

    private EditText myComment;
    private Context context;
    private RatingBar myRating;
    private APIDAO dao;

    public ShareCommentController(Context context, EditText text, RatingBar bar, APIDAO dao) {
        this.myComment = text;
        this.context = context;
        this.myRating = bar;
        this.dao = dao;
    }

    @Override
    public void onClick(View view) {
        if (myComment.getText().equals("")) {
            Toast.makeText(context, R.string.missing_comment, Toast.LENGTH_SHORT).show();
            return;
        }
        Comment comentario = new Comment(myComment.getText().toString(), "USERNAME", "", myRating.getRating());
        dao.postComment(context, this, this, comentario);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e("ERROR", "Error posting comment");
    }

    @Override
    public void onResponse(JSONObject response) {
        Toast.makeText(context, R.string.thank_you, Toast.LENGTH_SHORT).show();
        return;
    }
}
