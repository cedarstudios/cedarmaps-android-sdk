package com.cedarstudios.cedarmapssdk.listeners;

import android.support.annotation.NonNull;
import com.cedarstudios.cedarmapssdk.model.routing.GeoRouting;

/**
 * The listener for obtaining the results of a GeoRouting request (Direction and Distance).
 */
public interface GeoRoutingResultListener {

    /**
     * This method is called on UiThread when the results are obtained successfully.
     * @param result The result of a GeoRouting request.
     */
    void onSuccess(@NonNull GeoRouting result);

    /**
     * This method is called on UiThread when obtaining the GeoRouting result is failed.
     * @param errorMessage The error message for the reason of failure.
     */
    void onFailure(@NonNull String errorMessage);
}
