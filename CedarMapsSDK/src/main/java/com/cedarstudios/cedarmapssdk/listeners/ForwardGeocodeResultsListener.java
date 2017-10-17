package com.cedarstudios.cedarmapssdk.listeners;

import android.support.annotation.NonNull;
import com.cedarstudios.cedarmapssdk.model.geocoder.forward.ForwardGeocode;
import java.util.List;

/**
 * The listener for obtaining the results of a Forward Geocode request.
 */
public interface ForwardGeocodeResultsListener {
    /**
     * This method is called on UiThread when the results are obtained successfully.
     * @param results The list of ForwardGeocode result items.
     */
    void onSuccess(@NonNull List<ForwardGeocode> results);

    /**
     * This method is called on UiThread when obtaining the Forward Geocode result is failed.
     * @param errorMessage The error message for the reason of failure.
     */
    void onFailure(@NonNull String errorMessage);
}
