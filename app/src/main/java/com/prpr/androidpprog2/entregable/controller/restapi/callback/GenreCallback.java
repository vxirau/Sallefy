package com.prpr.androidpprog2.entregable.controller.restapi.callback;

import com.prpr.androidpprog2.entregable.model.Genre;
import com.prpr.androidpprog2.entregable.model.Track;

import java.util.ArrayList;

public interface GenreCallback extends FailureCallback {

    void onGenresReceive(ArrayList<Genre> genres);
    void onTracksByGenre(ArrayList<Track> tracks);
    void onGenreSelected(Genre genere);
    void onGenreCreated(Genre data);
    void onAllGenreFailure(Throwable throwable);
    void onGenreCreateFailure(Throwable throwable);
}
