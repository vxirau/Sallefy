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
import java.util.PrimitiveIterator;

public class PreferenceUtils {

    private static String LOGIN_COLLECTION = "userPreferences";
    private static String TRACK_COLLECTION = "trackInformation";
    private static String PLAYLIST_ORDER = "playlistOrder";
    private static String KEY_USER = "userLogin";
    private static String KEY_PASSWORD = "password";
    private static String KEY_TRACK = "track";
    private static String KEY_PLY_ID = "id";
    private static String KEY_SHUFFLE = "shuffle";
    private static String KEY_CURRENT_TRACK = "activeAudio";
    private static String KEY_ALL_TRACK = "track";
    private static String KEY_PLY_ORD = "order";
    private static String KEY_LST_PLY_ID = "lastPlaylist";

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

    public static boolean saveTrack(Context context,Track t){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(t);
        editor.putString(KEY_CURRENT_TRACK, json);
        editor.apply();
        return true;
    }

    public static Track getTrack(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString(KEY_CURRENT_TRACK, null);
        Type type = new TypeToken<Track>() {}.getType();
        return gson.fromJson(json, type);
    }


    public static boolean saveShuffle(Context context, boolean shuf){
        SharedPreferences prefs = context.getSharedPreferences(TRACK_COLLECTION, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(KEY_SHUFFLE, String.valueOf(shuf));
        prefsEditor.apply();
        return true;
    }

    public static boolean getShuffle(Context context){
        SharedPreferences prefs = context.getSharedPreferences(TRACK_COLLECTION, Context.MODE_PRIVATE);
        return Boolean.parseBoolean(prefs.getString(KEY_SHUFFLE, null));
    }


    public static boolean savePlayID(Context context, int index){
        SharedPreferences prefs = context.getSharedPreferences(TRACK_COLLECTION, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(KEY_PLY_ID, String.valueOf(index));
        prefsEditor.apply();
        return true;
    }

    public static int getPlayID(Context context){
        SharedPreferences prefs = context.getSharedPreferences(TRACK_COLLECTION, Context.MODE_PRIVATE);
        return Integer.parseInt(Objects.requireNonNull(prefs.getString(KEY_PLY_ID, null)));
    }

    public static boolean saveLastPlaylistID(Context context, int index){
        SharedPreferences prefs = context.getSharedPreferences(PLAYLIST_ORDER, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(KEY_LST_PLY_ID, String.valueOf(index));
        prefsEditor.apply();
        return true;
    }

    public static int getLastPlaylistID(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PLAYLIST_ORDER, Context.MODE_PRIVATE);
        if (prefs.getString(KEY_LST_PLY_ID, null) == null){
            return -1;
        }
        return Integer.parseInt(Objects.requireNonNull(prefs.getString(KEY_LST_PLY_ID, null)));
    }

    public static boolean savePlaylistOrder(Context context, int orderType){
        SharedPreferences prefs = context.getSharedPreferences(PLAYLIST_ORDER, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(KEY_PLY_ORD, String.valueOf(orderType));
        prefsEditor.apply();
        return true;
    }

    public static int getPlaylistOrder(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PLAYLIST_ORDER, Context.MODE_PRIVATE);
        if (prefs.getString(KEY_PLY_ORD, null) == null){
            return -1;
        }
        return Integer.parseInt(Objects.requireNonNull(prefs.getString(KEY_PLY_ORD, null)));
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
