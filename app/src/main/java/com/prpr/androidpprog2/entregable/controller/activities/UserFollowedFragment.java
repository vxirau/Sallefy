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

    private ArrayList<User> allUsers;
    boolean itIsFollowed;
    private ArrayList<User> followedUsers;

    private EditText etSearchFollowed;

    private UserManager userManager;
    private RecyclerView mRecyclerView;

    public UserFollowedFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_followed, container, false);
        this.allUsers = new ArrayList<>();
        this.followedUsers = new ArrayList<>();

        userManager = new UserManager();
        userManager.getAllUsers(this);

        for(int i = 0; i < allUsers.size(); i++){
            userManager.userIsFollowed(allUsers.get(i).getLogin(), this);
            if(itIsFollowed) followedUsers.add(allUsers.get(i));
        }

        mRecyclerView = (RecyclerView) view.findViewById(R.id.userPlaylistsRecyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        UserAdapter adapter = new UserAdapter(getContext(), null);
        adapter.setUserCallback(this);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter);


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
        this.allUsers = (ArrayList) users;
    }

    @Override
    public void onUserIsFollowed(boolean isFollowed) {
        this.itIsFollowed = isFollowed;
    }

    @Override
    public void onUserIsFollowedFail(Throwable throwable) {

    }

    @Override
    public void onAllUsersFail(Throwable throwable) {

    }

    @Override
    public void onFailure(Throwable throwable) {

    }
}
