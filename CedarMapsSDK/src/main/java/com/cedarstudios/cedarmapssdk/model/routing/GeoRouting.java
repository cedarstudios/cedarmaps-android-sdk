package com.cedarstudios.cedarmapssdk.model.routing;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * The result of Routing (Direction/Distance) request
 */
public class GeoRouting implements Serializable {
    @SerializedName("routes")
    @Expose
    private List<Route> routes = null;

    /**
     *
     * @return Routes in a result.
     */
    @Nullable
    public List<Route> getRoutes() {
        return routes;
    }
}
