package com.prpr.androidpprog2.entregable.controller.callbacks;

import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import java.util.Map;

public class CloudinaryCallback implements UploadCallback {

    @Override
    public void onStart(String requestId) {
        // your code here
    }
    @Override
    public void onProgress(String requestId, long bytes, long totalBytes) {
        // example code starts here
        Double progress = (double) bytes/totalBytes;
        // post progress to app UI (e.g. progress bar, notification)
        // example code ends here
    }
    @Override
    public void onSuccess(String requestId, Map resultData) {
        String url = (String) resultData.get("url");
        System.out.println(url);
    }
    @Override
    public void onError(String requestId, ErrorInfo error) {
        // your code here
    }
    @Override
    public void onReschedule(String requestId, ErrorInfo error) {
        // your code here
    }
}
