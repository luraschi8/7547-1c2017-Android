package com.tdp2.tripplanner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.tdp2.tripplanner.modelo.UserInstance;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import static android.view.View.GONE;

public class ProfileActivity extends AppCompatActivity {


    TextView usernameField;
    ImageView userImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        this.findViewById(R.id.salir).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Se limpia la sesión del usuario.
                UserInstance.finishInstance(getApplicationContext());
            }
        });

        //todo: falta cargar informacion a los botones

        usernameField = ((TextView)findViewById(R.id.user_name));
        userImage = (ImageView)findViewById(R.id.user_image);
        usernameField.setText("Nombre : " + UserInstance.getInstance(getApplicationContext()).getName());
        URL url;
        try {
            new DownloadImageTask(userImage)
                    .execute(UserInstance.getInstance(getApplicationContext()).getPhotoURL());
        }catch (Exception ex) {
            userImage.setVisibility(GONE);
        }

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String urldisplay = params[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
