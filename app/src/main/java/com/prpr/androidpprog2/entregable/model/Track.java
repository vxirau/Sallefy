package com.prpr.androidpprog2.entregable.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

public class Track implements Serializable, Comparable<Track> {

    @SerializedName("color")
    private String color;

    @SerializedName("duration")
    private Integer duration;

    @SerializedName("genres")
    private List<Genre> genres;

    @SerializedName("id")
    private Integer id;

    @SerializedName("name")
    private String name;

    @SerializedName("owner")
    private User user;

    @SerializedName("released")
    private String released;

    @SerializedName("thumbnail")
    private String thumbnail;

    @SerializedName("url")
    private String url;

    private boolean selected = false;

    private boolean liked;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserLogin() {
        return user.getLogin();
    }

    public void setUserLogin(String userLogin) {
        user.setLogin(userLogin);
    }

    public String getReleased() {
        return released;
    }

    public void setReleased(String released) {
        this.released = released;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void print(){
        System.out.println("ID: " + this.id + "\nName: " + this.name + "\nUser: " + this.getUser().getLogin() + "\nFoto: " + this.getThumbnail() + "\nRelease: " + this.getReleased());
    }

    public static Comparator<Track> TrackDurationComparator
            = new Comparator<Track>() {

        public int compare(Track firstTrack, Track secondTrack) {

            Integer firstTrackDuration = firstTrack.getDuration();
            Integer secondTrackDuration = secondTrack.getDuration();

            if(firstTrackDuration != null && secondTrackDuration != null){
                return secondTrackDuration.compareTo(firstTrackDuration);
            }else{
                return 0;
            }


        }

    };

    public static Comparator<Track> TrackAscendentDurationComparator
            = new Comparator<Track>() {

        public int compare(Track firstTrack, Track secondTrack) {

            Integer firstTrackReleased = firstTrack.getDuration();
            Integer secondTrackReleased = secondTrack.getDuration();

            if(firstTrackReleased != null && secondTrackReleased != null){
                return firstTrackReleased.compareTo(secondTrackReleased);
            }else{
                return 0;
            }
        }
    };

    public static Comparator<Track> TrackDescendentDurationComparator
            = new Comparator<Track>() {

        public int compare(Track firstTrack, Track secondTrack) {

            Integer firstTrackDuration = firstTrack.getDuration();
            Integer secondTrackDuration = secondTrack.getDuration();

            if(firstTrackDuration != null && secondTrackDuration != null){
                return secondTrackDuration.compareTo(firstTrackDuration);
            }else{
                return 0;
            }
        }
    };

    public static Comparator<Track> TrackNameAscendentComparator
            = new Comparator<Track>() {

        public int compare(Track firstTrack, Track secondTrack) {

            String firstTrackReleased = firstTrack.getName().toUpperCase();
            String secondTrackReleased = secondTrack.getName().toUpperCase();

            if(firstTrackReleased != null && secondTrackReleased != null){
                return firstTrackReleased.compareTo(secondTrackReleased);
            }else{
                return 0;
            }
        }
    };

    public static Comparator<Track> TrackNameDescendentComparator
            = new Comparator<Track>() {

        public int compare(Track firstTrack, Track secondTrack) {

            String firstTrackReleased = firstTrack.getName().toUpperCase();
            String secondTrackReleased = secondTrack.getName().toUpperCase();

            if(firstTrackReleased != null && secondTrackReleased != null){
                return secondTrackReleased.compareTo(firstTrackReleased);
            }else{
                return 0;
            }
        }
    };

    public static Comparator<Track> TrackArtistNameAscendentComparator
            = new Comparator<Track>() {

        public int compare(Track firstTrack, Track secondTrack) {

            String firstTrackArtistName = firstTrack.getUser().getFirstName();
            String secondTrackArtistName = secondTrack.getUser().getFirstName();

            if(firstTrackArtistName != null && secondTrackArtistName != null){
                return firstTrackArtistName.compareTo(secondTrackArtistName);
            }else{
                return 0;
            }
        }
    };

    public static Comparator<Track> TrackArtistNameDescendentComparator
            = new Comparator<Track>() {

        public int compare(Track firstTrack, Track secondTrack) {

            String firstTrackArtistName = firstTrack.getUser().getFirstName();
            String secondTrackArtistName = secondTrack.getUser().getFirstName();

            if(firstTrackArtistName != null && secondTrackArtistName != null){
                return secondTrackArtistName.compareTo(firstTrackArtistName);
            }else{
                return 0;
            }
        }
    };



    @Override
    public int compareTo(Track track) {
        int compareDuration = ((Track) track).getDuration();

        //ascending order
        return this.duration - compareDuration;


    }



}