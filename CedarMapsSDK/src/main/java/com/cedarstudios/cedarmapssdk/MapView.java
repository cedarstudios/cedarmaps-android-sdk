package com.cedarstudios.cedarmapssdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;

import java.util.Locale;

public class MapView extends com.mapbox.mapboxsdk.maps.MapView {

    private BroadcastReceiver mBroadcastReceiver = null;

    public MapView(@NonNull Context context) {
        super(context);
        setupBroadcastReceiver();
        setupStyleURL();
    }

    public MapView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupBroadcastReceiver();
        setupStyleURL();
    }

    public MapView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupBroadcastReceiver();
        setupStyleURL();
    }

    public MapView(@NonNull Context context, @Nullable MapboxMapOptions options) {
        super(context, options);
        setupBroadcastReceiver();
        setupStyleURL();
    }

    private void setupBroadcastReceiver() {
        if (mBroadcastReceiver == null) {
            mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    setupStyleURL();
                }
            };

            LocalBroadcastManager.getInstance(Mapbox.getApplicationContext())
                    .registerReceiver(mBroadcastReceiver, new IntentFilter(AuthenticationManager.ACCESS_TOKEN_READY_INTENT));
        }
    }

    private void setupStyleURL() {
        String url = String.format(Locale.ENGLISH,
                AuthenticationManager.getInstance().getAPIBaseURL()
                + "tiles/light.json?access_token=%s",
                Mapbox.getAccessToken());
        this.setStyleUrl(url);
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(Mapbox.getApplicationContext()).unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }
}
