package com.prpr.androidpprog2.entregable.controller.fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.UserManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
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
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.renderer.RadarChartRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import android.support.v4.app.*;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.activities.EditSongActivity;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.HeatTrackCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.TrackCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.UserCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.TrackManager;
import com.prpr.androidpprog2.entregable.controller.restapi.service.UserTokenService;
import com.prpr.androidpprog2.entregable.model.Follow;
import com.prpr.androidpprog2.entregable.model.Genre;
import com.prpr.androidpprog2.entregable.model.Heat;
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


public class UserStatisticsFragment extends Fragment implements TrackCallback, OnMapReadyCallback, HeatTrackCallback {


    private ArrayList<Track> myLikedTracks;
    private ArrayList<Track> myUploadedTracks;
    private ArrayList<String> mapOptions;

    private Spinner llistaSongs;
    private ArrayAdapter<String> adapter;

    private PieChart pieChart;
    private BarChart barChart;
    private ScrollView scrollView;
    private ArrayList<String> genres;
    private int[] values = {50, 800};
    private static final int GREEN_SALLEFY_COLOR = Color.rgb(0, 153, 51);
    private static final int[] SALLEFY_COLORS =  {GREEN_SALLEFY_COLOR, Color.rgb(0, 175, 32) , Color.rgb(0, 123, 76), Color.rgb(0, 127, 12), Color.rgb(0, 243, 50)};
    private TrackManager trackManager;
    private GoogleMap map;
    private boolean hasBeenShown;

    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;

    HashMap<String, Integer> likedTracksHashMap = new HashMap<String, Integer>();
    HashMap<String, Integer> topPlayedGenresHashmap = new HashMap<String, Integer>();
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

        //Pie Chart Top Listened Genres by User
        pieChart = (PieChart) view.findViewById(R.id.pieChart);

        llistaSongs = (Spinner) view.findViewById(R.id.trackNames);
        llistaSongs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                TrackManager.getInstance(getContext()).getTrackLocations(myUploadedTracks.get(i).getId(), UserStatisticsFragment.this);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.45f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.rgb(25, 27, 30));
        pieChart.animateX(1200);
        pieChart.getLegend().setEnabled(false);
        pieChart.setTransparentCircleRadius(55f);

        pieChart.setNoDataText("");
        //pieChart.setHole


        //Bar Chart Top Played Tracks uploaded from user
        barChart = (BarChart) view.findViewById(R.id.barChartTopPlayedTracks);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(false);
        barChart.setMaxVisibleValueCount(50);
        barChart.setPinchZoom(true);
        barChart.setDrawGridBackground(false);
        barChart.getXAxis().setTextColor(Color.WHITE);
        barChart.getAxisLeft().setTextColor(Color.WHITE);
        barChart.getAxisRight().setTextColor(Color.WHITE);
        barChart.getLegend().setTextColor(Color.WHITE);
        barChart.getDescription().setTextColor(Color.WHITE);
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


        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        ImageView ivMapTransparent = (ImageView) view.findViewById(R.id.ivMapTransparent);
        ivMapTransparent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        scrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });

        //Manager
        trackManager = new TrackManager(getContext());

        trackManager.getOwnLikedTracks(this);
        trackManager.getOwnTracks(this);


        return view;
    }

    //TOP LISTENED GENRES
    private void createPieChart( HashMap<String, Integer> tracks){
        PieDataSet dataSet = new PieDataSet(addValuesToPieChart(tracks), "Top Listened Genres by User");
        dataSet.setColors(SALLEFY_COLORS);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        PieData data = new PieData();
        data.addDataSet(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.WHITE);
        pieChart.setData(data);
    }
    private void sortGenresByPlays(ArrayList<Track> tracks){

        for(Track t : tracks) {
            for (int i = 0; i < t.getPlays(); i++) {
                String key = t.getName();
                if (!topPlayedGenresHashmap.containsKey(key)) {

                    topPlayedGenresHashmap.put(key, t.getPlays());

                } else {
                    topPlayedGenresHashmap.put(key, t.getPlays());
                }
            }
        }

        List<Map.Entry<String, Integer>> list = new LinkedList<>(topPlayedGenresHashmap.entrySet());


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
        createBarChart(sortedLikedTracksGenre);

    }
    private ArrayList<PieEntry> addValuesToPieChart( HashMap<String, Integer> sortedLikedTracksGenre){

        int count = 0;
        ArrayList<PieEntry> dataVals = new ArrayList<>();

        for (Map.Entry<String,Integer> entry : sortedLikedTracksGenre.entrySet()){
            dataVals.add(new PieEntry(entry.getValue(), entry.getKey()));
            genres.add(entry.getKey());
            count++;
            if(count == 6){  //The top genres chart is limited to 5 genres
                return dataVals;
            }
        }
        return dataVals;
    }

    //TOP LIKED TRACKS
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

        createPieChart(sortedLikedTracksGenre);

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




    @Override
    public void onTracksReceived(List<Track> tracks) {

    }

    @Override
    public void onNoTracks(Throwable throwable) {

    }

    @Override
    public void onPersonalTracksReceived(List<Track> tracks) {
        this.myUploadedTracks = (ArrayList<Track>) tracks;
        TrackManager.getInstance(getContext()).getTrackLocations(myUploadedTracks.get(0).getId(), this);

        sortGenresByPlays(myUploadedTracks);

        ArrayList<String> allNames = new ArrayList<>();
        for(Track t : myUploadedTracks){
            allNames.add(t.getId() + " - " + t.getName());
        }
        mapOptions = allNames;
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, mapOptions){
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                return setCentered(super.getView(position, convertView, parent));
            }

            private View setCentered(View view)
            {
                TextView textView = (TextView)view.findViewById(android.R.id.text1);
                textView.setGravity(Gravity.CENTER);
                return view;
            }
        };
        llistaSongs.setAdapter(adapter);
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


    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                scrollView.requestDisallowInterceptTouchEvent(true);
            }
        });

        try {

            boolean success = map.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.map_style));

        } catch (Resources.NotFoundException e) {

        }
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setScrollGesturesEnabled(true);

    }


    @Override
    public void onHeatInfoRecieved(ArrayList<Heat> body) {
        if(body!=null && body.size()>0){
            map.clear();
            if(mOverlay!=null){
                mOverlay.remove();
            }
            map.resetMinMaxZoomPreference();

            int[] colors = {
                    Color.rgb(102, 225, 0),
                    Color.rgb(255, 0, 0)
            };

            float[] startPoints = {
                    0.2f, 1f
            };

            Gradient gradient = new Gradient(colors, startPoints);

            List<LatLng> list = new ArrayList<>();
            for(Heat h : body){
                LatLng location = new LatLng(h.getLatitude(), h.getLongitude());
                list.add(location);
            }

            mProvider = new HeatmapTileProvider.Builder().data(list).gradient(gradient).build();

            mOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
            mProvider.setRadius(50);
            map.setMaxZoomPreference(14.0f);
        }

    }

    @Override
    public void onHeatInfoFailure() {

    }

    @Override
    public void onFailure() {

    }
}
