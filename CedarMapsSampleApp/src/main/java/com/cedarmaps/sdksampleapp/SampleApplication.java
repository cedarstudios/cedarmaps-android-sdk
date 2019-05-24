package com.cedarmaps.sdksampleapp;

import android.app.Application;

import com.cedarstudios.cedarmapssdk.CedarMaps;
import com.cedarstudios.cedarmapssdk.model.MapID;

@SuppressWarnings("WeakerAccess")
public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        CedarMaps.getInstance()
                .setClientID(getResources().getString(R.string.client_id))
                .setClientSecret(getResources().getString(R.string.client_secret))
                .setContext(this)
                .setMapID(MapID.MIX);
    }
}
