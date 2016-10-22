package com.cedarstudios.cedarmaps.sample.fragment;

import android.support.v4.content.ContextCompat;

import com.cedarstudios.cedarmaps.sample.R;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

public class MarkersTestFragment extends MainTestFragment {
    @Override
    protected void onMapLoaded() {

        mMapView.getController().setZoom(14);
        mMapView.getController().setCenter(new GeoPoint(35.703859, 51.408037));

        GeoPoint position = new GeoPoint(35.709086, 51.401471);
        addMarker(position);

        position = new GeoPoint(35.699781, 51.397565);
        addMarker(position);

        position = new GeoPoint(35.705636, 51.414174);
        addMarker(position);

        position = new GeoPoint(35.698631, 51.407693);
        addMarker(position);
    }

    private void addMarker(GeoPoint position) {
        Marker marker = new Marker(mMapView);
        marker.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.marker_default));
        marker.setPosition(position);
        mMapView.getOverlays().add(marker);
    }
}
