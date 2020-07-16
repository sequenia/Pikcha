package com.sequenia.photo.repository;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Временное хранилище
 */
public class Repository {

    private static final String APP_PREF = "PHOTOS";

    private static final String PREF_PHOTO_PATH = "PHOTO_PATH";
    private static final String PREF_SOURCE_TYPE_NAME = "SOURCE_TYPE_NAME";

    private static SharedPreferences init(Context context) {
        return context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
    }

    public static void savePath(Context context, String path) {
        SharedPreferences sp = init(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(PREF_PHOTO_PATH, path);
        ed.apply();
    }

    public static String getPath(Context context) {
        return init(context).getString(PREF_PHOTO_PATH, null);
    }

    public static void removePath(Context context) {
        SharedPreferences sp = init(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.remove(PREF_PHOTO_PATH);
        ed.apply();
    }

    public static void saveSourceTypeName(Context context, String value) {
        SharedPreferences sp = init(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(PREF_SOURCE_TYPE_NAME, value);
        ed.apply();
    }

    public static String getSourceTypeName(Context context) {
        return init(context).getString(PREF_SOURCE_TYPE_NAME, null);
    }

    public static void removeSourceTypeName(Context context) {
        SharedPreferences sp = init(context);
        SharedPreferences.Editor ed = sp.edit();
        ed.remove(PREF_SOURCE_TYPE_NAME);
        ed.apply();
    }
}
