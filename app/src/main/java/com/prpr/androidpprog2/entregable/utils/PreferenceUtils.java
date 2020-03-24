package com.prpr.androidpprog2.entregable.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prpr.androidpprog2.entregable.model.Track;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PreferenceUtils {

    private static String LOGIN_COLLECTION = "userPreferences";
    private static String TRACK_COLLECTION = "trackInformation";
    private static String KEY_USER = "userLogin";
    private static String KEY_PASSWORD = "password";
    private static String KEY_TRACK = "track";
    private static String KEY_ALL_TRACK = "track";


    public PreferenceUtils() {
    }

    public static boolean saveTrackIndex(Context context, int index){
        SharedPreferences prefs = context.getSharedPreferences(TRACK_COLLECTION, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(KEY_TRACK, String.valueOf(index));
        prefsEditor.apply();
        return true;
    }

    public static int getTrackIndex(Context context){
        SharedPreferences prefs = context.getSharedPreferences(TRACK_COLLECTION, Context.MODE_PRIVATE);
        return Integer.parseInt(Objects.requireNonNull(prefs.getString(KEY_TRACK, null)));
    }


    public static boolean saveAllTracks(Context context,ArrayList<Track> list){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(KEY_ALL_TRACK, json);
        editor.apply();
        return true;
    }

    public static ArrayList<Track> getAllTracks(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString(KEY_ALL_TRACK, null);
        Type type = new TypeToken<ArrayList<Track>>() {}.getType();
        return gson.fromJson(json, type);
    }



    public static boolean saveUser (Context context, String login) {
        SharedPreferences prefs = context.getSharedPreferences(LOGIN_COLLECTION, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(KEY_USER, login);
        prefsEditor.apply();
        return true;
    }

    public static String getUser (Context context) {
        SharedPreferences prefs = context.getSharedPreferences(LOGIN_COLLECTION, Context.MODE_PRIVATE);
        return prefs.getString(KEY_USER, null);
    }

    public static boolean savePassword (Context context, String userId) {
        SharedPreferences prefs = context.getSharedPreferences(LOGIN_COLLECTION, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(KEY_PASSWORD, userId);
        prefsEditor.apply();
        return true;
    }

    public static String getPassword (Context context) {
        SharedPreferences prefs = context.getSharedPreferences(LOGIN_COLLECTION, Context.MODE_PRIVATE);
        return prefs.getString(KEY_PASSWORD, null);
    }

    public static void resetValues(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(LOGIN_COLLECTION, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(KEY_PASSWORD, null);
        prefsEditor.putString(KEY_USER, null);
        prefsEditor.apply();
    }
}
