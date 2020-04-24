package com.prpr.androidpprog2.entregable.model.DB;

import android.content.Context;
import android.util.Log;

import io.objectbox.BoxStore;
import io.objectbox.android.BuildConfig;

public class ObjectBox {
    private static BoxStore boxStore;

    public static void init(Context context) {
        if(boxStore==null)
        boxStore = MyObjectBox.builder().androidContext(context.getApplicationContext()).build();

        if (BuildConfig.DEBUG) {
            Log.d("SALLEFY:", String.format("Using ObjectBox %s (%s)",
                    BoxStore.getVersion(), BoxStore.getVersionNative()));
        }
    }

    public static BoxStore get() { return boxStore; }
}
