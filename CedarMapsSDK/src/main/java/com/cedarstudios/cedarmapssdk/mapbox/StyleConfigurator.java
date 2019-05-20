package com.cedarstudios.cedarmapssdk.mapbox;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.cedarstudios.cedarmapssdk.listeners.AccessTokenListener;
import com.cedarstudios.cedarmapssdk.listeners.OnStyleConfigurationListener;

public final class StyleConfigurator {

    public static void configure(final Style style, final OnStyleConfigurationListener completionHandler) {
        final Handler handler = new Handler(Looper.getMainLooper());

        AuthenticationManager.getInstance().getAccessToken(new AccessTokenListener() {
            @Override
            public void onSuccess(@NonNull final String accessToken) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        String url = style.urlString() + "?access_token=" + accessToken;
                        com.mapbox.mapboxsdk.maps.Style.Builder builder =
                                new com.mapbox.mapboxsdk.maps.Style.Builder().fromUrl(url);
                        completionHandler.onSuccess(builder);
                    }
                });
            }

            @Override
            public void onFailure(@NonNull final String errorMessage) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        completionHandler.onFailure(errorMessage);
                    }
                });
            }
        });
    }

}
