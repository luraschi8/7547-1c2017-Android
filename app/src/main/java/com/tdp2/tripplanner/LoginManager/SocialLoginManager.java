package com.tdp2.tripplanner.LoginManager;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.facebook.FacebookSdk;
import com.tdp2.tripplanner.CitySelectionActivity;
import com.tdp2.tripplanner.FbLoginHiddenActivity;
import com.tdp2.tripplanner.modelo.SocialUser;

import rx.Observable;
import rx.subjects.PublishSubject;


/**
 * Created by Casa on 13/5/2017.
 */

public class SocialLoginManager {

    private static final String ERROR = "You must choose a social platform.";

    @SuppressLint("StaticFieldLeak")
    private static SocialLoginManager instance;
    private PublishSubject<SocialUser> userEmitter;
    private Context appContext;
    private boolean withProfile = true;
    private String clientId;

    private SocialLoginManager(Context context) {
        appContext = context.getApplicationContext();
    }

    public static synchronized SocialLoginManager getInstance(Context context) {
        if (instance == null) {
            instance = new SocialLoginManager(context);
        }
        return instance;
    }

    @Deprecated
    public SocialLoginManager withProfile() {
        this.withProfile = true;
        return this;
    }

    public SocialLoginManager withProfile(boolean withProfile) {
        this.withProfile = withProfile;
        return this;
    }

    public SocialLoginManager facebook() {
        return this;
    }


    public static void init(Application application) {
        FacebookSdk.sdkInitialize(application.getApplicationContext());
    }

    public void login2() {
        appContext.startActivity(getIntent());
    }

    public Observable<SocialUser> login() {
        userEmitter = PublishSubject.create();
        appContext.startActivity(getIntent());
        return userEmitter;
    }

    public Intent getIntent() {
        Intent intent = new Intent(appContext, FbLoginHiddenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;

    }

    public boolean isWithProfile() {
        return this.withProfile;
    }

    public void onLoginSuccess(SocialUser socialUser) {
        if (userEmitter != null) {
            SocialUser copy = new SocialUser(socialUser);
            userEmitter.onNext(copy);
            userEmitter.onCompleted();

        }
        Intent intent = new Intent(appContext, CitySelectionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        appContext.startActivity(intent);
    }

    public void onLoginError(Throwable throwable) {
        if (userEmitter != null) {
            Throwable copy = new Throwable(throwable);
            userEmitter.onError(copy);
        }
    }

    public void onLoginCancel() {
        if (userEmitter != null) {
            userEmitter.onCompleted();
        }
    }


}
