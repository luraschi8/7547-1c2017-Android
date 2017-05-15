package com.tdp2.tripplanner.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by matias on 5/15/17.
 */

public class LocaleHandler {

    private static String LANGUAGE_KEY = "currentLanguage";
    private static String DEFAULT_LANGUAGE = "ES"; //Default Language should go here.
    private static String FILE_NAME = "language";

    public static void updateLocaleSettings(Context context) {
        String languageToLoad  = LocaleHandler.loadLanguageSelection(context); // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config,
                context.getResources().getDisplayMetrics());
    }

    public static void saveLanguageSelection(Context context, String language) {
        SharedPreferences languagepref = context.getSharedPreferences(FILE_NAME ,MODE_PRIVATE);
        SharedPreferences.Editor editor = languagepref.edit();
        String code = LocaleHandler.languageToCode(language);
        editor.putString(LANGUAGE_KEY, code );
        editor.apply();
    }

    public static String loadLanguageSelection(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(FILE_NAME , MODE_PRIVATE);
        return preferences.getString(LANGUAGE_KEY, DEFAULT_LANGUAGE);
    }

    public static String languageToCode(String language) {
        switch (language) {
            case "Español":
                return "ES";
            case "English":
                return "EN";
            default:
                return "";
        }
    }

    public static String codeToLanguage(String code) {
        switch (code) {
            case "ES":
                return "Español";
            case "EN":
                return "English";
            default:
                return "";
        }
    }
}
