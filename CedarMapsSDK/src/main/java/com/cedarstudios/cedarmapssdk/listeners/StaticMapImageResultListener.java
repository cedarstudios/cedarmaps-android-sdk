package com.cedarstudios.cedarmapssdk.listeners;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;

/**
 * The listener for obtaining the results of a Static Map Image request.
 */
public interface StaticMapImageResultListener {

    /**
     * This method is called on UiThread when the result is obtained successfully.
     * @param bitmap The bitmap for the requested static map image.
     */
    void onSuccess(@NonNull Bitmap bitmap);

    /**
     * This method is called on UiThread when obtaining the Static Map Image result is failed.
     * @param errorMessage The error message for the reason of failure.
     */
    void onFailure(@NonNull String errorMessage);
}
