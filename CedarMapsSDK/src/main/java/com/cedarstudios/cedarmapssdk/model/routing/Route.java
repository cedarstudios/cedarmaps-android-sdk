package com.cedarstudios.cedarmapssdk.model.routing;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A Route shows direction and distance info
 */
public class Route implements Serializable
{
    @SerializedName("bbox")
    @Expose
    private List<Double> bbox = null;
    @SerializedName("distance")
    @Expose
    private Double distance;
    @SerializedName("geometry")
    @Expose
    private Geometry geometry;
    @SerializedName("time")
    @Expose
    private Integer time;
    @SerializedName("instructions")
    @Expose
    private List<Instruction> instructions = null;
    /**
     *
     * @return Boundary for a route.
     */
    @Nullable
    public LatLngBounds getBoundingBox() {
        if (bbox != null && bbox.size() == 4) {
            ArrayList<LatLng> list = new ArrayList<>();
            list.add(new LatLng(bbox.get(1), bbox.get(0)));
            list.add(new LatLng(bbox.get(3), bbox.get(2)));

            return new LatLngBounds.Builder().includes(list).build();
        }

        return null;
    }

    /**
     *
     * @return Total distance in a route in meters
     */
    @Nullable
    public Double getDistance() {
        return distance;
    }

    /**
     *
     * @return The geometry info for a route. (GeoJSON coordinates)
     */
    @Nullable
    public Geometry getGeometry() {
        return geometry;
    }

    /**
     *
     * @return ETA for a route in seconds
     */
    @Nullable
    public Integer getTime() {
        return time;
    }


    /**
     *
     * @return Turn by turn instructions for a route
     */
    @Nullable
    public List<Instruction> getInstructions() {
        return instructions;
    }
}
