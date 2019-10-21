package com.cedarstudios.cedarmapssdk.model.routing;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The geometry info of obtained route.
 */
public class Geometry implements Serializable {
    @SerializedName("coordinates")
    @Expose
    private List<List<Double>> coordinates = null;
    @SerializedName("type")
    @Expose
    private String type;

    /**
     *
     * @return Coordinates of turn by turn direction
     */
    @Nullable
    public List<LatLng> getCoordinates() {
        ArrayList<LatLng> result = new ArrayList<>();

        for (int i = 0; i < coordinates.size(); i++) {
            List<Double> item = coordinates.get(i);
            result.add(new LatLng(item.get(1), item.get(0)));
        }

        return result;
    }

    private String getType() {
        return type;
    }
}
