package com.cedarstudios.cedarmapssdk;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

import com.cedarstudios.cedarmapssdk.listeners.AccessTokenListener;
import com.cedarstudios.cedarmapssdk.listeners.OnStyleConfigurationListener;
import com.mapbox.mapboxsdk.maps.Style;

public final class CedarMapsStyleConfigurator {

    public static void configure(final CedarMapsStyle style, final OnStyleConfigurationListener completionHandler) {
        final Handler handler = new Handler(Looper.getMainLooper());

        AuthenticationManager.getInstance().getAccessToken(new AccessTokenListener() {
            @Override
            public void onSuccess(@NonNull final String accessToken) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        String url = style.urlString() + "?access_token=" + accessToken;
                        Style.Builder builder = new Style.Builder().fromUri(url);
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
