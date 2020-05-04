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
    public String allMyPlaylists;
    public String topPlaylists;
    public String topUsers;
    public String followingPlaylists;
    public String myPlaylists;
    public String mySongs;
    public String myFollowed;
    public String userInfo;
    public String user;
    public String username;
    public String password;

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

    public String gettopPlaylists() {
        return topPlaylists;
    }

    public void settopPlaylists(String topPlaylists) {
        this.topPlaylists = topPlaylists;
    }

    public String getAllMyPlaylists() {
        return allMyPlaylists;
    }

    public void setAllMyPlaylists(String allMyPlaylists) {
        this.allMyPlaylists = allMyPlaylists;
    }
    
    
    public ArrayList<Playlist> retrieveAllMyPlaylists() {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Playlist>>() {}.getType();
        return gson.fromJson(this.allMyPlaylists, type);
    }

    public void saveAllMyPlaylists(ArrayList<Playlist> allPlayMylists) {
        Gson gson = new Gson();
        this.allMyPlaylists = gson.toJson(allPlaylists);
    }

    public ArrayList<Playlist> retrieveMyUserPlaylists() {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Playlist>>() {}.getType();
        return gson.fromJson(this.myPlaylists, type);
    }

    public void saveMyUserPlaylists(ArrayList<Playlist> allPlayMylists) {
        Gson gson = new Gson();
        this.myPlaylists = gson.toJson(allPlaylists);
    }

    public User retrieveUser(){
        Gson gson = new Gson();
        Type type = new TypeToken<User>() {}.getType();
        return gson.fromJson(this.user, type);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public ArrayList<Playlist> retrievetopPlaylists() {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Playlist>>() {}.getType();
        return gson.fromJson(this.topPlaylists, type);
    }

    public void savetopPlaylists(ArrayList<Playlist> allPlaylists) {
        Gson gson = new Gson();
        this.topPlaylists = gson.toJson(allPlaylists);
    }

    public ArrayList<Playlist> retrieveFollowingPlaylists() {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Playlist>>() {}.getType();
        return gson.fromJson(this.followingPlaylists, type);
    }

    public void saveFollowingPlaylists(ArrayList<Playlist> allPlaylists) {
        Gson gson = new Gson();
        this.followingPlaylists = gson.toJson(allPlaylists);
    }

    public String getTopUsers() {
        return topUsers;
    }

    public void setTopUsers(String topUsers) {
        this.topUsers = topUsers;
    }

    public String getFollowerPlaylists() {
        return followingPlaylists;
    }

    public void setFollowerPlaylists(String followerPlaylists) {
        followingPlaylists = followerPlaylists;
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
