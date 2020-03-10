package com.prpr.androidpprog2.entregable.controller.restapi.callback;

import java.util.ArrayList;

import com.prpr.androidpprog2.entregable.model.Genre;
import com.prpr.androidpprog2.entregable.model.Track;

public interface GenreCallback extends FailureCallback {

    void onGenresReceive(ArrayList<Genre> genres);
    void onTracksByGenre(ArrayList<Track> tracks);
}
