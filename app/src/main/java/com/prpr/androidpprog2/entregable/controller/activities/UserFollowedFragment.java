package com.prpr.androidpprog2.entregable.controller.activities;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.adapters.UserAdapter;
import com.prpr.androidpprog2.entregable.controller.adapters.UserPlaylistAdapter;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.UserCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.UserManager;
import com.prpr.androidpprog2.entregable.model.User;
import com.prpr.androidpprog2.entregable.model.UserToken;

import java.util.ArrayList;
import java.util.List;


public class UserFollowedFragment extends Fragment implements UserCallback {

    private ArrayList<User> followedUsers;

    private EditText etSearchFollowed;

    private UserManager userManager;
    private RecyclerView mRecyclerView;

    public UserFollowedFragment() {
        // Required empty public constructor
        this.followedUsers = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_followed, container, false);


        mRecyclerView = (RecyclerView) view.findViewById(R.id.usersFollowedRecyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        UserAdapter adapter = new UserAdapter(getContext(), null);
        adapter.setUserCallback(this);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter);

        userManager = new UserManager(getContext());
        userManager.getFollowedUsers(this);


        return view;
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
    public void onUsernameUpdated(User user) {

    }

    @Override
    public void onEmailUpdated(User user) {

    }

    @Override
    public void onTopUsersRecieved(List<User> body) {

    }

    @Override
    public void onUserSelected(User user) {

    }

    @Override
    public void onAllUsersSuccess(List<User> users) {

    }

    @Override
    public void onFollowedUsersSuccess(List<User> users) {
        this.followedUsers = (ArrayList) users;
        UserAdapter userAdapter = new UserAdapter(getContext(), this.followedUsers);
        userAdapter.setUserCallback(this);
        mRecyclerView.setAdapter(userAdapter);
    }


    @Override
    public void onAllUsersFail(Throwable throwable) {

    }

    @Override
    public void onFollowedUsersFail(Throwable throwable) {

    }

    @Override
    public void onFailure(Throwable throwable) {

    }
}
