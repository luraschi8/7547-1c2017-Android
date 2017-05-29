package com.tdp2.tripplanner.helpers;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.tdp2.tripplanner.R;
import com.tdp2.tripplanner.dao.APIDAO;
import com.tdp2.tripplanner.modelo.Attraction;
import com.tdp2.tripplanner.modelo.UserInstance;

/**
 * Created by matias on 5/28/17.
 */

public class FavController implements View.OnClickListener {

    private Context context;
    private Attraction attraction;
    private ImageView buttonToggle;
    private APIDAO apidao;

    public FavController (Context context, Attraction attraction, ImageView buttonToggle){
        this.context = context;
        this.attraction = attraction;
        this.buttonToggle = buttonToggle;
        this.apidao = new APIDAO();
    }

    @Override
    public void onClick(View view) {
        if(attraction.isFavorite()) unfavClick(view);
        else favClick(view);
    }

    private void favClick(View view){
        if(UserInstance.getInstance(context) == null) {
            Toast.makeText(context, R.string.have_to_log, Toast.LENGTH_SHORT).show();
            return;
        }
        AddToFavoritesResponseListener listener = new AddToFavoritesResponseListener(context, buttonToggle, attraction);
        apidao.saveToFavorites(context, listener, listener, attraction.getId());
    }

    private void unfavClick(View view){
        RemoveFavoriteResponseListener listener = new RemoveFavoriteResponseListener(context, buttonToggle, attraction);
        apidao.removeFavorite(context, listener, listener, attraction.getId());
    }
}
