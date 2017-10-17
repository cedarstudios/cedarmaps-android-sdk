package com.cedarstudios.cedarmapssdk.listeners;

import android.support.annotation.NonNull;

/**
 * The listener for notifying the result of configuring the CedarMap tiles for using in a MapView.
 */
public interface OnTilesConfigured {
    /**
     * This method is called on UiThread when the tiles are configured successfully.
     */
    void onSuccess();

    /**
     * This method is called on UiThread when configuring the tiles is failed.
     * @param errorMessage The error message for the reason of failure.
     */
    void onFailure(@NonNull String errorMessage);
}
