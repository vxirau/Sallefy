package com.prpr.androidpprog2.entregable.controller.restapi.callback;

import com.prpr.androidpprog2.entregable.model.Heat;

import java.util.ArrayList;

public interface HeatTrackCallback  {

    void onHeatInfoRecieved(ArrayList<Heat> body);
    void onHeatInfoFailure();
    void onFailure();

}
