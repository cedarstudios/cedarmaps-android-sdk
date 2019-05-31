package com.cedarstudios.cedarmapssdk.listeners;

import android.support.annotation.NonNull;

import com.mapbox.mapboxsdk.maps.Style;

/**
 * The listener for notifying the result of generating CedarMaps styles for using in a MapView.
 */
public interface OnStyleConfigurationListener {
    /**
     * This method is called on UiThread when the tiles are configured successfully.
     */
    void onSuccess(@NonNull Style.Builder styleBuilder);

    /**
     * This method is called on UiThread when configuring the tiles is failed.
     * @param errorMessage The error message for the reason of failure.
     */
    void onFailure(@NonNull String errorMessage);
}
