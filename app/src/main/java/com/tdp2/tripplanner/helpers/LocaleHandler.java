package com.tdp2.tripplanner.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by matias on 5/15/17.
 */

public class LocaleHandler {

    private static String LANGUAGE_KEY = "currentLanguage";
    private static String DEFAULT_LANGUAGE = "EN";
    private static String FILE_NAME = "language";

    public static void saveLanguageSelection(Context context, String language) {
        SharedPreferences languagepref = context.getSharedPreferences(FILE_NAME ,MODE_PRIVATE);
        SharedPreferences.Editor editor = languagepref.edit();
        editor.putString(LANGUAGE_KEY, language );
        editor.apply();
    }

    public static String loadLanguageSelection(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(FILE_NAME , MODE_PRIVATE);
        return preferences.getString(LANGUAGE_KEY, DEFAULT_LANGUAGE);
    }
}
