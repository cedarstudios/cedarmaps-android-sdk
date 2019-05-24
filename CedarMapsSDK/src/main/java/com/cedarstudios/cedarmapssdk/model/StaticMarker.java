package com.cedarstudios.cedarmapssdk.model;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.cedarstudios.cedarmapssdk.listeners.StaticMapImageResultListener;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;

/**
 * The wrapper class whose instances are used for showing markers on the static map.
 * @see com.cedarstudios.cedarmapssdk.CedarMaps#staticMap(int, int, int, LatLng, ArrayList, StaticMapImageResultListener)
 */
public final class StaticMarker {

    @NonNull
    private LatLng coordinate;

    @Nullable
    private Uri markerUri;

    /**
     *
     * @param coordinate The coordinate of the marker
     * @param markerUri The remote address of the image you want to use for the marker
     */
    public StaticMarker(@NonNull LatLng coordinate, @Nullable Uri markerUri) {
        this.coordinate = coordinate;
        this.markerUri = markerUri;
    }

    @NonNull
    public LatLng getCoordinate() {
        return coordinate;
    }

    @Nullable
    public Uri getMarkerUri() {
        return markerUri;
    }
}
