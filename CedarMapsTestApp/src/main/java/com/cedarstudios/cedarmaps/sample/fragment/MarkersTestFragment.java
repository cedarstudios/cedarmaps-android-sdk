package com.cedarstudios.cedarmaps.sample.fragment;

import com.cedarstudios.cedarmaps.sample.Constants;
import com.cedarstudios.cedarmaps.sample.MainActivity;
import com.cedarstudios.cedarmaps.sample.R;
import com.cedarstudios.cedarmapssdk.tileprovider.CedarMapsTileLayer;
import com.mapbox.mapboxsdk.api.ILatLng;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Icon;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.views.MapController;
import com.mapbox.mapboxsdk.views.MapView;
import com.mapbox.mapboxsdk.views.MapViewListener;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MarkersTestFragment extends Fragment implements MapViewListener {

    private MapView mapView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = (MapView) view.findViewById(R.id.mapView);

        SharedPreferences pref = getActivity().getSharedPreferences(MainActivity.PREF_NAME,
                Context.MODE_PRIVATE);
        String accessToken = pref.getString(MainActivity.PREF_ID_ACCESS_TOKEN, "");
        mapView.setAccessToken(accessToken);

        CedarMapsTileLayer cedarMapsTileLayer = new CedarMapsTileLayer(Constants.MAPID_CEDARMAPS_STREETS);
        mapView.setTileSource(cedarMapsTileLayer);

        mapView.setCenter(new LatLng(35.703859, 51.408037));
        mapView.setZoom(14);

        LatLng position = new LatLng(35.709086, 51.401471);
        addMarker(position);

        position = new LatLng(35.699781, 51.397565);
        addMarker(position);

        position = new LatLng(35.705636, 51.414174);
        addMarker(position);

        position = new LatLng(35.698631, 51.407693);
        addMarker(position);

        mapView.setMapViewListener(this);

        return view;
    }

    public void addMarker(LatLng position) {
        Marker marker = new Marker(mapView, "", "", position);
        marker.setIcon(new Icon(getActivity(), Icon.Size.SMALL, "marker-stroked", "FF0000"));
        mapView.addMarker(marker);
    }

    @Override
    public void onShowMarker(MapView mapView, Marker marker) {

    }

    @Override
    public void onHideMarker(MapView mapView, Marker marker) {

    }

    @Override
    public void onTapMarker(MapView mapView, Marker marker) {
        MapController mapController = new MapController(mapView);
        mapController.animateTo(marker.getPoint());
    }

    @Override
    public void onLongPressMarker(MapView mapView, Marker marker) {

    }

    @Override
    public void onTapMap(MapView mapView, ILatLng iLatLng) {

    }

    @Override
    public void onLongPressMap(MapView mapView, ILatLng iLatLng) {

    }
}
