package com.prpr.androidpprog2.entregable.controller.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.UserManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
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


    private ArrayList<Track> myTracks;

    private RadarChart radarChart;
    private ArrayList<String> genres;
    private int[] values = {50, 800};
    private static final int SALLEFY_COLOR = Color.rgb(0, 153, 51);
    private TrackManager trackManager;

    HashMap<String, Integer> likedTracksHashMap = new HashMap<String, Integer>();

    public UserStatisticsFragment() {
        // Required empty public constructor
        this.genres = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_statistics, container, false);
        radarChart = (RadarChart) view.findViewById(R.id.radarChart);

        myTracks = new ArrayList<>();



        radarChart.getDescription().setEnabled(false);
        radarChart.setDragDecelerationFrictionCoef(0.30f);
        radarChart.animateY(750, Easing.EaseInOutCubic);
        radarChart.getLegend().setTextColor(Color.WHITE);

        radarChart.getYAxis().setTextColor(Color.WHITE);

        radarChart.getXAxis().setTextColor(Color.WHITE);
        radarChart.getXAxis().setTextSize(15);
        radarChart.getLegend().setEnabled(false);

        trackManager = new TrackManager(getContext());
        trackManager.getOwnLikedTracks(this);



        return view;
    }

    private void createChart( HashMap<String, Integer> tracks){
        RadarDataSet dataSet = new RadarDataSet(addDataValuesToChart(tracks), "Top Listened Genres by User");
        dataSet.setColor(SALLEFY_COLOR);


        RadarData data = new RadarData();
        data.addDataSet(dataSet);

        XAxis xAxis = radarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(genres));
        xAxis.setTextSize(10f);
        radarChart.setData(data);
        radarChart.invalidate();
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
        createChart(sortedLikedTracksGenre);

    }

    private ArrayList<RadarEntry> addDataValuesToChart( HashMap<String, Integer> sortedLikedTracksGenre){

        int count = 0;
        ArrayList<RadarEntry> dataVals = new ArrayList<>();

        for (Map.Entry<String,Integer> entry : sortedLikedTracksGenre.entrySet()){
            dataVals.add(new RadarEntry(entry.getValue()));
            genres.add(entry.getKey());
            count++;
            if(count == 5){  //The top genres chart is limited to 5 genres
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

    }

    @Override
    public void onPersonalLikedTracksReceived(List<Track> tracks) {
        this.myTracks = (ArrayList) tracks;

        sortTracksByLikes(myTracks);





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
    public void onFailure(Throwable throwable) {

    }


}
