package com.tdp2.tripplanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.tdp2.tripplanner.LoginManager.SocialLoginManager;
import com.tdp2.tripplanner.modelo.SocialUser;

import org.json.JSONObject;

/**
 * Pantalla de login que permite al usuario loguearse mediante Facebook.
 */
public class LoginActivity extends AppCompatActivity {

    // Facebook Sing In
    private CallbackManager callbackManager;
    LoginButton loggingButton;
    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);


        setContentView(R.layout.activity_login);

        // Facebook sign-in
        loggingButton = (LoginButton) findViewById(R.id.login_facebook_button);
        loggingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginByFacebook();
            }
        });

    }



    private void loginByFacebook() {
        SocialLoginManager.getInstance(this).login2();
                /*.facebook()
                .login() //Se inicializa la actividad de login oculta que se encarga de los estados de respuesta de Facebook.
                .subscribe((socialUser) -> {
                            Log.d(TAG, "userId: " + socialUser.get_id());
                            Log.d(TAG, "photoUrl: " + socialUser.getPhotoURL());
                            Log.d(TAG, "accessToken: " + socialUser.getToken());
                            Log.d(TAG, "name: " + socialUser.profile.name);
                            Log.d(TAG, "email: " + socialUser.profile.email);
                            Log.d(TAG, "pageLink: " + socialUser.profile.pageLink);
                        },
                        error -> {
                            Log.d(TAG, "error: " + error.getMessage());
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Error while accesing with FB",
                                    Toast.LENGTH_SHORT).show();
                        });*/
    }
}

