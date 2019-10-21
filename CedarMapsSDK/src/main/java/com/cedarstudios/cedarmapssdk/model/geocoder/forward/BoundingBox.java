
package com.cedarstudios.cedarmapssdk.model.geocoder.forward;

import androidx.annotation.Nullable;
import android.text.TextUtils;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mapbox.mapboxsdk.geometry.LatLng;

/**
 * The bounding box for a result of Forward Geocode request.
 */
public class BoundingBox implements Serializable
{

    @SerializedName("ne")
    @Expose
    private String ne;
    @SerializedName("sw")
    @Expose
    private String sw;

    /**
     * @return The north east coordinate of the bounding box.
     */
    @Nullable
    public LatLng getNorthEast() {
        if (!TextUtils.isEmpty(ne)) {
            if (ne.split(",").length == 2) {
                return new LatLng(Double.parseDouble(ne.split(",")[0]), Double.parseDouble(ne.split(",")[1]));
            }
        }
        return null;
    }

    /**
     * @return The south west coordinate of the bounding box.
     */
    @Nullable
    public LatLng getSouthWest() {
        if (!TextUtils.isEmpty(sw)) {
            if (sw.split(",").length == 2) {
                return new LatLng(Double.parseDouble(sw.split(",")[0]), Double.parseDouble(sw.split(",")[1]));
            }
        }
        return null;
    }
}
