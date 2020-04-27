package com.prpr.androidpprog2.entregable.model.DB;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prpr.androidpprog2.entregable.model.Playlist;
import com.prpr.androidpprog2.entregable.model.Track;
import com.prpr.androidpprog2.entregable.model.User;

import java.lang.reflect.Type;
import java.util.ArrayList;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class SavedCache {

    @Id(assignable = true) public long id;
    public String oldToken;
    public String allPlaylists;
    public String popularPlaylists;
    public String topUsers;
    public String followerPlaylists;
    public String myPlaylists;
    public String mySongs;
    public String myFollowed;
    public String userInfo;
    public String user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOldToken() {
        return oldToken;
    }

    public void setOldToken(String oldToken) {
        this.oldToken = oldToken;
    }

    public String getAllPlaylists() {
        return allPlaylists;
    }

    public void setAllPlaylists(String allPlaylists) {
        this.allPlaylists = allPlaylists;
    }

    public String getPopularPlaylists() {
        return popularPlaylists;
    }

    public void setPopularPlaylists(String popularPlaylists) {
        this.popularPlaylists = popularPlaylists;
    }

    public User retrieveUser(){
        Gson gson = new Gson();
        Type type = new TypeToken<User>() {}.getType();
        return gson.fromJson(this.user, type);
    }

    public void saveUser(User user){
        Gson gson = new Gson();
        this.user = gson.toJson(user);
    }

    public ArrayList<Playlist> retrieveAllPlaylists() {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Playlist>>() {}.getType();
        return gson.fromJson(this.allPlaylists, type);
    }

    public void saveAllPlaylists(ArrayList<Playlist> allPlaylists) {
        Gson gson = new Gson();
        this.allPlaylists = gson.toJson(allPlaylists);
    }

    public ArrayList<Playlist> retrievePopularPlaylists() {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Playlist>>() {}.getType();
        return gson.fromJson(this.popularPlaylists, type);
    }

    public void savePopularPlaylists(ArrayList<Playlist> allPlaylists) {
        Gson gson = new Gson();
        this.popularPlaylists = gson.toJson(allPlaylists);
    }

    public String getTopUsers() {
        return topUsers;
    }

    public void setTopUsers(String topUsers) {
        this.topUsers = topUsers;
    }

    public String getFollowerPlaylists() {
        return followerPlaylists;
    }

    public void setFollowerPlaylists(String followerPlaylists) {
        this.followerPlaylists = followerPlaylists;
    }

    public String getMyPlaylists() {
        return myPlaylists;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }




    public void setMyPlaylists(String myPlaylists) {
        this.myPlaylists = myPlaylists;
    }

    public String getMySongs() {
        return mySongs;
    }

    public void setMySongs(String mySongs) {
        this.mySongs = mySongs;
    }

    public String getMyFollowed() {
        return myFollowed;
    }

    public void setMyFollowed(String myFollowed) {
        this.myFollowed = myFollowed;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }
}
