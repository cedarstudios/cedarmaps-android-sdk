package com.cedarmaps.sdksampleapp;

import android.app.Application;

import com.cedarstudios.cedarmapssdk.CedarMaps;

public class SampleApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        CedarMaps.getInstance()
                .setClientID(Constants.CLIENT_ID)
                .setClientSecret(Constants.CLIENT_SECRET)
                .setContext(this)
                .setMapID("cedarmaps.mix");
    }
}
