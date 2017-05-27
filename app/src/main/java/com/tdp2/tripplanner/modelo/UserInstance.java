package com.tdp2.tripplanner.modelo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.facebook.login.LoginManager;
import com.google.android.gms.iid.InstanceID;
import com.google.gson.Gson;
import com.tdp2.tripplanner.CitySelectionActivity;
import com.tdp2.tripplanner.LoginActivity;
import com.tdp2.tripplanner.ProfileActivity;
import com.tdp2.tripplanner.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

public class UserInstance {

    private static SocialUser userProfile;
    private static String deviceID;

    public static void finishInstance(final Context context) {
        LoginManager.getInstance().logOut();
        removeUserProfile(context);
    }

    public static String getDeviceID(Context context){
        if (deviceID == null) {
            deviceID = InstanceID.getInstance(context).getId();
        }
        return deviceID;
    }

    public static String getCountry(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return manager.getSimCountryIso();
    }

    public static SocialUser getInstance(final Context context) {
        return userProfile;
    }

    public static void removeUserProfile(final Context context) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.loginToken), Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove(context.getString(R.string.userProfile));

        editor.apply();
        userProfile = null;

        UserInstance.securityRedirect(context);
    }

    /**
     * Redirect inicial de la aplicación a la ventana de selección de ciudades.
     * Se podría cargar la información del usuario en este punto si se deseara.
     * @param context
     */
    public static void securityRedirect(final Context context) {


        final SocialUser socialUser = getSocialUser(context);

        if (socialUser != null && socialUser.getToken() != null) {
            Log.i(context.getClass().getName(), "User already exists.");
        } else {
            Log.i(context.getClass().getName(), "User doesn't exist.");
        }

        final Intent intent = new Intent(context, CitySelectionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Recupera la sesión del usuario junto con la información mínima del mismo.
     * @param context de la aplicación
     * @return el Usuario logueado.
     */
    private static SocialUser getSocialUser(final Context context) {

        final SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.loginToken), Context.MODE_PRIVATE);

        final Gson gson = new Gson();
        final String profile = sharedPreferences.getString(context.getString(R.string.userProfile), null);
        userProfile = gson.fromJson(profile, SocialUser.class);
        return userProfile;
    }

    /**
     * Método utilizado con el fin de guardar la sesión del usuario dentro de la aplicación.
     * @param context contexto de la aplicación.
     * @param socialUser
     */
    public static void setSocialUser(final Context context, final SocialUser socialUser) {

        //SharedPreferences es utilizado para guardar la sesión del usuario en la aplicación.
        final SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.loginToken), Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        final Gson gson = new Gson();
        final String json = gson.toJson(socialUser);
        editor.putString(context.getString(R.string.userProfile), json);

        editor.apply();

        userProfile = socialUser;
    }

    /**
     * Configura la ventana de redirección a la que dirige el botón, dependiendo de si existe o no
     * un usuario logueado en la aplicación.
     * En caso de que no exista usuario logueado, redirigirá a la ventana de inicio de sesión.
     * En caso de que exista, redirigirá a la ventana de perfil del usuario logueado.
     * @param applicationContext
     */
    public static void loginRedirect(Context applicationContext) {

        final SocialUser socialUser = getSocialUser(applicationContext);
        if (socialUser != null && socialUser.getToken() != null) {
            Log.i(applicationContext.getClass().getName(), "User already exists.");
            final Intent intent = new Intent(applicationContext, ProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            applicationContext.startActivity(intent);
        } else {
            Log.i(applicationContext.getClass().getName(), "User doesn't exist.");
            final Intent intent = new Intent(applicationContext, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            applicationContext.startActivity(intent);
        }

    }

    public static JSONObject toJSON(Context context) {
        JSONObject data = new JSONObject();
        try {
            data.put("idAndroid", UserInstance.getDeviceID(context));
            data.put("pais", UserInstance.getCountry(context));
            String username = "";
            String socialID = "";
            if (UserInstance.getInstance(context) != null) {
                username = UserInstance.getInstance(context).getName();
                socialID = UserInstance.getInstance(context).get_id();
            }
            data.put("nombre", username);
            data.put("idRedSocial", socialID);
        } catch (JSONException e) {
            Log.e("JSON ERROR", "build user access json:" + e.toString());
            return null;
        }
        return data;
    }
}
