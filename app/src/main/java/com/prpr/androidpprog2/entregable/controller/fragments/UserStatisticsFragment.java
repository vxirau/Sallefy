package com.prpr.androidpprog2.entregable.controller.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.UserManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.renderer.RadarChartRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.TrackCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.UserCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.TrackManager;
import com.prpr.androidpprog2.entregable.model.Follow;
import com.prpr.androidpprog2.entregable.model.Genre;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.model.User;
import com.prpr.androidpprog2.entregable.model.UserToken;
import com.prpr.androidpprog2.entregable.utils.Constants;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class UserStatisticsFragment extends Fragment implements TrackCallback {


    private ArrayList<Track> myLikedTracks;
    private ArrayList<Track> myUploadedTracks;

    private RadarChart radarChart;
    private BarChart barChart;
    private ScrollView scrollView;
    private ArrayList<String> genres;
    private int[] values = {50, 800};
    private static final int GREEN_SALLEFY_COLOR = Color.rgb(0, 153, 51);
    private static final int[] SALLEFY_COLORS =  {GREEN_SALLEFY_COLOR, Color.rgb(0, 175, 32) , Color.rgb(0, 123, 76), Color.rgb(0, 127, 12), Color.rgb(0, 243, 50)};
    private TrackManager trackManager;

    private boolean hasBeenShown;

    HashMap<String, Integer> likedTracksHashMap = new HashMap<String, Integer>();
    HashMap<String, Integer> topPlayedTracksHashmap = new HashMap<String, Integer>();
    public UserStatisticsFragment() {
        // Required empty public constructor
        this.genres = new ArrayList<>();
        myLikedTracks = new ArrayList<>();
        myUploadedTracks = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_user_statistics, container, false);

        scrollView = view.findViewById(R.id.statisticsScrollview);

        //Radar Chart Top Listened Genres by User
        radarChart = (RadarChart) view.findViewById(R.id.radarChartTopListenedGenres);

        radarChart.getDescription().setEnabled(false);
        radarChart.setDragDecelerationFrictionCoef(0.30f);
        radarChart.animateY(750, Easing.EaseInOutCubic);
        radarChart.getLegend().setTextColor(Color.WHITE);

        radarChart.getYAxis().setTextColor(Color.WHITE);

        radarChart.getXAxis().setTextColor(Color.WHITE);
        radarChart.getXAxis().setTextSize(15);
        radarChart.getLegend().setEnabled(false);

        //Bar Chart Top Played Tracks uploaded from user
        barChart = (BarChart) view.findViewById(R.id.barChartTopPlayedTracks);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setMaxVisibleValueCount(50);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);
        barChart.getXAxis().setTextColor(Color.WHITE);
        barChart.getLegend().setEnabled(false);
        barChart.getDescription().setEnabled(false);


        //No animem la barchart fins que l'usuari l'ha pogut veure
        Rect scrollBounds = new Rect();
        scrollView.getHitRect(scrollBounds);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if(!hasBeenShown){
                    if (barChart.getLocalVisibleRect(scrollBounds)) {
                        barChart.animateY(1000, Easing.EaseInOutCubic);
                        hasBeenShown = true;
                    }
                }

            }
        });




        //Manager
        trackManager = new TrackManager(getContext());
        trackManager.getOwnLikedTracks(this);
        trackManager.getOwnTracks(this);


        return view;
    }

    private void createRadarChart( HashMap<String, Integer> tracks){
        RadarDataSet dataSet = new RadarDataSet(addDataValuesToRadarChart(tracks), "Top Listened Genres by User");
        dataSet.setColor(GREEN_SALLEFY_COLOR);


        RadarData data = new RadarData();
        data.addDataSet(dataSet);

        XAxis xAxis = radarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(genres));
        xAxis.setTextSize(10f);
        radarChart.setData(data);
        radarChart.invalidate();
    }

    private void createBarChart(HashMap<String, Integer> tracks){

        BarDataSet barDataSet = new BarDataSet(addDataValuesToBarChart(tracks), "Top Played Tracks From User");
        barDataSet.setColors(SALLEFY_COLORS);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(.5f);

        barChart.setData(barData);
    }

    private void sortTracksByLikes(ArrayList<Track> tracks){

        for(Track t : tracks) {
            for (int i = 0; i < t.getGenres().size(); i++) {
                String key = t.getGenres().get(i).getName();
                if (!likedTracksHashMap.containsKey(key)) {

                    likedTracksHashMap.put(key, 1);

                } else {

                    int count = likedTracksHashMap.get(key);
                    likedTracksHashMap.put(key, count + 1);

                }
            }
        }

        List<Map.Entry<String, Integer> > list = new LinkedList<>(likedTracksHashMap.entrySet());


        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        HashMap<String, Integer> sortedLikedTracksGenre = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> aa : list) {
            sortedLikedTracksGenre.put(aa.getKey(), aa.getValue());
        }

        for (Map.Entry<String,Integer> entry : sortedLikedTracksGenre.entrySet())
            System.out.println("Key = " + entry.getKey() +
                    ", Value = " + entry.getValue());
        createRadarChart(sortedLikedTracksGenre);

    }
    private void sortTracksByPlays(ArrayList<Track> tracks){

        for(Track t : tracks) {
            for (int i = 0; i < t.getPlays(); i++) {
                String key = t.getName();
                if (!likedTracksHashMap.containsKey(key)) {

                    likedTracksHashMap.put(key, t.getPlays());

                } else {
                    likedTracksHashMap.put(key, t.getPlays());
                }
            }
        }

        List<Map.Entry<String, Integer>> list = new LinkedList<>(likedTracksHashMap.entrySet());


        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        HashMap<String, Integer> sortedLikedTracksGenre = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> aa : list) {
            sortedLikedTracksGenre.put(aa.getKey(), aa.getValue());
        }

        for (Map.Entry<String,Integer> entry : sortedLikedTracksGenre.entrySet())
            System.out.println("Key = " + entry.getKey() +
                    ", Value = " + entry.getValue());
        createBarChart(sortedLikedTracksGenre);

    }
    private ArrayList<BarEntry> addDataValuesToBarChart( HashMap<String, Integer> sortedPlayedTracks){

        int count = 0;
        ArrayList<BarEntry> dataVals = new ArrayList<>();

        for (Map.Entry<String,Integer> entry : sortedPlayedTracks.entrySet()){
            dataVals.add(new BarEntry(entry.getValue().floatValue(), 40f));
            genres.add(entry.getKey());
            count++;
            if(count == 6){  //The top genres chart is limited to 5 genres
                return dataVals;
            }
        }
        return dataVals;
    }
    private ArrayList<RadarEntry> addDataValuesToRadarChart( HashMap<String, Integer> sortedLikedTracksGenre){

        int count = 0;
        ArrayList<RadarEntry> dataVals = new ArrayList<>();

        for (Map.Entry<String,Integer> entry : sortedLikedTracksGenre.entrySet()){
            dataVals.add(new RadarEntry(entry.getValue()));
            genres.add(entry.getKey());
            count++;
            if(count == 6){  //The top genres chart is limited to 5 genres
                return dataVals;
            }
        }
        return dataVals;
    }

    @Override
    public void onTracksReceived(List<Track> tracks) {

    }

    @Override
    public void onNoTracks(Throwable throwable) {

    }

    @Override
    public void onPersonalTracksReceived(List<Track> tracks) {
        this.myUploadedTracks = (ArrayList<Track>) tracks;
        sortTracksByPlays(myUploadedTracks);
    }

    @Override
    public void onPersonalLikedTracksReceived(List<Track> tracks) {
        this.myLikedTracks = (ArrayList) tracks;

        sortTracksByLikes(myLikedTracks);





    }

    @Override
    public void onUserTracksReceived(List<Track> tracks) {

    }

    @Override
    public void onCreateTrack(Track t) {

    }

    @Override
    public void onTopTracksRecieved(List<Track> tracks) {

    }

    @Override
    public void onNoTopTracks(Throwable throwable) {

    }

    @Override
    public void onTrackLiked(int id) {

    }

    @Override
    public void onTrackNotFound(Throwable throwable) {

    }

    @Override
    public void onTrackUpdated(Track body) {

    }

    @Override
    public void onTrackUpdateFailure(Throwable throwable) {

    }

    @Override
    public void onTrackDeleted(int id) {

    }

    @Override
    public void onTrackReceived(Track track) {

    }

    @Override
    public void onMyTracksFailure(Throwable throwable) {

    }

    @Override
    public void onFailure(Throwable throwable) {

    }


}
