package com.tdp2.tripplanner;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.FacebookSdk;
import com.tdp2.tripplanner.modelo.UserInstance;

/**
 * Clase inicializadora de la aplicación.
 * Debería cargar la información del usuario.
 * En una primera instancia lo único que hace es levantar al usuario cargado en el sistema.
 */
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FacebookSdk.sdkInitialize(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                UserInstance.securityRedirect(getApplicationContext());
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
