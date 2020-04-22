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
import com.prpr.androidpprog2.entregable.controller.restapi.callback.UserCallback;
import com.prpr.androidpprog2.entregable.model.Follow;
import com.prpr.androidpprog2.entregable.model.User;
import com.prpr.androidpprog2.entregable.model.UserToken;
import com.prpr.androidpprog2.entregable.utils.Constants;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class UserStatisticsFragment extends Fragment implements UserCallback {


    private RadarChart radarChart;
    private String[] genres = {"Pop", "Electronic", "Rock", "Hardcore", "Chill"};
    private int[] values = {50, 800};
    private static final int SALLEFY_COLOR = Color.rgb(0, 153, 51);
    private UserManager userManager;
    public UserStatisticsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_statistics, container, false);
        radarChart = (RadarChart) view.findViewById(R.id.radarChart);
        radarChart.getDescription().setEnabled(false);
        radarChart.setDragDecelerationFrictionCoef(0.99f);
        radarChart.animateY(750, Easing.EaseInOutCubic);
        radarChart.getLegend().setTextColor(Color.WHITE);

        radarChart.getYAxis().setTextColor(Color.WHITE);

        radarChart.getXAxis().setTextColor(Color.WHITE);
        radarChart.getXAxis().setTextSize(15);
        radarChart.getLegend().setEnabled(false);



        RadarDataSet dataSet = new RadarDataSet(dataValues(), "Top Listened Genres by User");
        dataSet.setColor(SALLEFY_COLOR);
        RadarData data = new RadarData();
        data.addDataSet(dataSet);

        XAxis xAxis = radarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(genres));

        radarChart.setData(data);
        radarChart.invalidate();

        //TODO-> implementar getLikedTracks a usermanager
        //userManager.
        return view;
    }

    private ArrayList<RadarEntry> dataValues(){
        ArrayList<RadarEntry> dataVals = new ArrayList<>();
        dataVals.add(new RadarEntry(4));
        dataVals.add(new RadarEntry(7));
        dataVals.add(new RadarEntry(1));
        dataVals.add(new RadarEntry(5));
        dataVals.add(new RadarEntry(9));
        return dataVals;
    }
    @Override
    public void onLoginSuccess(UserToken userToken) {

    }

    @Override
    public void onLoginFailure(Throwable throwable) {

    }

    @Override
    public void onRegisterSuccess() {

    }

    @Override
    public void onRegisterFailure(Throwable throwable) {

    }

    @Override
    public void onUserInfoReceived(User userData) {

    }

    @Override
    public void onUserUpdated(User body) {

    }

    @Override
    public void onAccountSaved(User body) {

    }

    @Override
    public void onTopUsersRecieved(List<User> body) {

    }

    @Override
    public void onUserUpdateFailure(Throwable throwable) {

    }

    @Override
    public void onUserSelected(User user) {

    }

    @Override
    public void onAllUsersSuccess(List<User> users) {

    }

    @Override
    public void onFollowedUsersSuccess(List<User> users) {

    }

    @Override
    public void onAllUsersFail(Throwable throwable) {

    }

    @Override
    public void onFollowedUsersFail(Throwable throwable) {

    }

    @Override
    public void onFollowSuccess(Follow body) {

    }

    @Override
    public void onAccountSavedFailure(Throwable throwable) {

    }

    @Override
    public void onFollowFailure(Throwable throwable) {

    }

    @Override
    public void onCheckSuccess(Follow body) {

    }

    @Override
    public void onCheckFailure(Throwable throwable) {

    }

    @Override
    public void onFailure(Throwable throwable) {

    }
}
