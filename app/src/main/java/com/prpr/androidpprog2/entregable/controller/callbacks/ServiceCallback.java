package com.prpr.androidpprog2.entregable.controller.callbacks;

import com.prpr.androidpprog2.entregable.model.Track;

public interface ServiceCallback {
    void onSeekBarUpdate(int progress, int duration);
    void updateUI(Track t, boolean playing, int position, int duration);
}
