package com.cedarstudios.cedarmapssdk;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.cedarstudios.cedarmapssdk.listeners.AccessTokenListener;
import com.cedarstudios.cedarmapssdk.listeners.OnTilesConfigured;
import com.mapbox.mapboxsdk.Mapbox;

final class TileConfigurator {

    static CedarMaps prepare(@Nullable final OnTilesConfigured completionHandler) {
        final Context context = AuthenticationManager.getInstance().getContext();

        Handler handler = new Handler(Looper.getMainLooper());
        if (context == null) {
            if (completionHandler != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        completionHandler.onFailure("Context is not set. Please call 'setContext' method on CedarMaps.getInstance()");
                    }
                });
            }
        } else {
            Mapbox.getInstance(context, Constants.INITIAL_TOKEN);

            AuthenticationManager.getInstance().getAccessToken(new AccessTokenListener() {
                @Override
                public void onSuccess(@NonNull String accessToken) {
                    Mapbox.setAccessToken("pk." + accessToken);
                    if (completionHandler != null) {
                        completionHandler.onSuccess();
                    }
                }

                @Override
                public void onFailure(@NonNull String error) {
                    if (completionHandler != null) {
                        completionHandler.onFailure(error);
                    }
                }
            });
        }
        return CedarMaps.getInstance();
    }
}
