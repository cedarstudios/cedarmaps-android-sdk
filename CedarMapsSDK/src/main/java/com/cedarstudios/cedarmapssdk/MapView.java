package com.cedarstudios.cedarmapssdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;

import com.cedarstudios.cedarmapssdk.listeners.AccessTokenListener;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;

import java.util.Locale;
import java.util.Map;

public class MapView extends com.mapbox.mapboxsdk.maps.MapView {

    private BroadcastReceiver mBroadcastReceiver = null;
    private String currentStyle;

    public MapView(@NonNull Context context) {
        super(context);
        setupBroadcastReceiver();
        setDefaultStyleURL();
    }

    public MapView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupBroadcastReceiver();
        setDefaultStyleURL();
    }

    public MapView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupBroadcastReceiver();
        setDefaultStyleURL();
    }

    public MapView(@NonNull Context context, @Nullable MapboxMapOptions options) {
        super(context, options);
        setupBroadcastReceiver();
        setDefaultStyleURL();
    }

    private void setupBroadcastReceiver() {
        if (mBroadcastReceiver == null) {
            mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (currentStyle == null) {
                        setDefaultStyleURL();
                    } else {
                        setStyleUrl(currentStyle);
                    }
                }
            };

            LocalBroadcastManager.getInstance(Mapbox.getApplicationContext())
                    .registerReceiver(mBroadcastReceiver, new IntentFilter(AuthenticationManager.ACCESS_TOKEN_READY_INTENT));
        }
    }

    @Override
    public void setStyleUrl(@NonNull final String url) {
        if (currentStyle != null && currentStyle.equals(url)) {
            return;
        }
        currentStyle = url;
        if (url.contains("access_token")) {
            super.setStyleUrl(url);
        } else {
            AuthenticationManager.getInstance().getAccessToken(new AccessTokenListener() {
                @Override
                public void onSuccess(@NonNull String accessToken) {
                    String urlWithToken = String.format(Locale.ENGLISH,"%s?access_token=%s", url, accessToken);
                    MapView.super.setStyleUrl(urlWithToken);
                }

                @Override
                public void onFailure(@NonNull String errorMessage) {

                }
            });
        }
    }

    private void setDefaultStyleURL() {
        AuthenticationManager.getInstance().getAccessToken(new AccessTokenListener() {
            @Override
            public void onSuccess(@NonNull String accessToken) {
                String url = String.format(Locale.ENGLISH,
                        AuthenticationManager.getInstance().getAPIBaseURL()
                                + "styles/cedarmaps.light.json?access_token=%s",
                        accessToken);
                setStyleUrl(url);
            }

            @Override
            public void onFailure(@NonNull String errorMessage) {

            }
        });
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(Mapbox.getApplicationContext()).unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }
}
