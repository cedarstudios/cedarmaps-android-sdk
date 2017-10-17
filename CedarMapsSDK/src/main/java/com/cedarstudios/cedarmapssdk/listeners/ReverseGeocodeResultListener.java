package com.cedarstudios.cedarmapssdk.listeners;

import android.support.annotation.NonNull;
import com.cedarstudios.cedarmapssdk.model.geocoder.reverse.ReverseGeocode;

/**
 * The listener for obtaining the results of a Reverse Geocode request.
 */
public interface ReverseGeocodeResultListener {

    /**
     * This method is called on UiThread when the results are obtained successfully.
     * @param result The result of a Reverse Geocode request.
     */
    void onSuccess(@NonNull ReverseGeocode result);

    /**
     * This method is called on UiThread when obtaining the Reverse Geocode result is failed.
     * @param errorMessage The error message for the reason of failure.
     */
    void onFailure(@NonNull String errorMessage);
}
