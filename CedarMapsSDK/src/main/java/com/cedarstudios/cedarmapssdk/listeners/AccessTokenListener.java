package com.cedarstudios.cedarmapssdk.listeners;

import android.support.annotation.NonNull;

/**
 * The listener for obtaining the Access Token needed in using CedarMaps API.
 */
public interface AccessTokenListener {
    /**
     * This method is called on UiThread when the Access Token is obtained successfully.
     * @param accessToken The Access Token needed in CedarMaps APIs.
     */
    void onSuccess(@NonNull String accessToken);

    /**
     * This method is called on UiThread when obtaining the Access Token is failed.
     * @param errorMessage The error message for the reason of failure.
     */
    void onFailure(@NonNull String errorMessage);
}
