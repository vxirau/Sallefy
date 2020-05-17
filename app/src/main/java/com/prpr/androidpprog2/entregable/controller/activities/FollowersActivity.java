package com.prpr.androidpprog2.entregable.controller.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.adapters.UserFollowedAdapter;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.UserCallback;
import com.prpr.androidpprog2.entregable.model.Follow;
import com.prpr.androidpprog2.entregable.model.User;
import com.prpr.androidpprog2.entregable.model.UserToken;
import com.prpr.androidpprog2.entregable.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class FollowersActivity extends AppCompatActivity implements UserCallback {

    private ArrayList<User> followers;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        followers = (ArrayList<User>) getIntent().getSerializableExtra("followers");

        mRecyclerView = (RecyclerView) findViewById(R.id.recycle);
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        UserFollowedAdapter adapter = new UserFollowedAdapter(this, followers);
        adapter.setUserCallback(this);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter);
    }



    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
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
        Intent intent = new Intent(this, InfoArtistaActivity.class);
        intent.putExtra("User", user);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
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
    public void onTopUsersFailure(Throwable throwable) {

    }

    @Override
    public void onFollowedUsersFailure(Throwable t) {

    }

    @Override
    public void onFollowersRecieved(ArrayList<User> body) {

    }

    @Override
    public void onFollowersFailed(Throwable throwable) {

    }

    @Override
    public void onFollowersFailure(Throwable throwable) {

    }

    @Override
    public void onFailure(Throwable throwable) {

    }
}
