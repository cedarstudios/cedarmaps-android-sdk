
package com.cedarstudios.cedarmapssdk.model.geocoder.forward;

import android.support.annotation.Nullable;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mapbox.mapboxsdk.geometry.LatLng;

/**
 *
 * Geometric location of a Forward Geocode result.
 */
public class Location implements Serializable
{

    @SerializedName("bb")
    @Expose
    private BoundingBox boundingBox;

    @SerializedName("center")
    @Expose
    private String center;

    /**
     *
     * @return The center coordinate of a Forward Geocode result.
     */
    @Nullable
    public LatLng getCenter() {
        if (center == null) {
            return null;
        }

        String[] latlng =  center.split(",");
        if (latlng.length == 2) {
            double latitude = Double.parseDouble(latlng[0]);
            double longitude = Double.parseDouble(latlng[1]);

            return new LatLng(latitude, longitude);
        } else {
            return null;
        }
    }

    /**
     *
     * @return The bounding box of a Forward Geocode result.
     */
    @Nullable
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }
}
