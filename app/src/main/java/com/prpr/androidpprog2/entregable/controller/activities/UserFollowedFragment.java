package com.prpr.androidpprog2.entregable.controller.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prpr.androidpprog2.entregable.R;
import com.prpr.androidpprog2.entregable.controller.adapters.TrackListAdapter;
import com.prpr.androidpprog2.entregable.controller.adapters.UserAdapter;
import com.prpr.androidpprog2.entregable.controller.adapters.UserFollowedAdapter;
import com.prpr.androidpprog2.entregable.controller.adapters.UserPlaylistAdapter;
import com.prpr.androidpprog2.entregable.controller.restapi.callback.UserCallback;
import com.prpr.androidpprog2.entregable.controller.restapi.manager.UserManager;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.model.User;
import com.prpr.androidpprog2.entregable.model.UserToken;
import com.prpr.androidpprog2.entregable.utils.Constants;

import java.util.ArrayList;
import java.util.List;


public class UserFollowedFragment extends Fragment implements UserCallback {

    private ArrayList<User> followedUsers;

    private EditText etSearchFollowed;

    private UserManager userManager;
    private RecyclerView mRecyclerView;

    private FloatingActionButton btnSettingsPlaylists;

    public UserFollowedFragment() {
        // Required empty public constructor
        this.followedUsers = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_followed, container, false);

        /*btnSettingsPlaylists = (FloatingActionButton) view.findViewById(R.id.configUsersFollowedButton);
        btnSettingsPlaylists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });*/

        etSearchFollowed = (EditText) view.findViewById(R.id.search_user_users_followed);
        etSearchFollowed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(R.id.usersFollowedRecyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        UserFollowedAdapter adapter = new UserFollowedAdapter(getContext(), null);
        adapter.setUserCallback(this);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(adapter);

        userManager = new UserManager(getContext());
        userManager.getFollowedUsers(this);


        return view;
    }

    private void filter(String text){
        ArrayList<User> filteredUsers = new ArrayList<>();

        for(User u : followedUsers){
            if(u.getLogin().toLowerCase().contains(text.toLowerCase())){
                filteredUsers.add(u);
            }
        }
        mRecyclerView.setAdapter(new UserFollowedAdapter(getContext(), filteredUsers));
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
    public void onUserUpdated(User user) {

    }


    @Override
    public void onTopUsersRecieved(List<User> body) {

    }

    @Override
    public void onUserSelected(User user) {
        Intent intent = new Intent(getContext(), InfoArtistaActivity.class);
        intent.putExtra("User", user);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, Constants.NETWORK.LOGIN_OK);
    }

    @Override
    public void onAllUsersSuccess(List<User> users) {

    }

    @Override
    public void onFollowedUsersSuccess(List<User> users) {
        this.followedUsers = (ArrayList) users;
        UserFollowedAdapter userFollowedAdapter = new UserFollowedAdapter(getContext(), this.followedUsers);
        userFollowedAdapter.setUserCallback(this);
        mRecyclerView.setAdapter(userFollowedAdapter);
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
