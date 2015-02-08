package com.cedarstudios.cedarmaps.sample.fragment;

import com.cedarstudios.cedarmaps.sample.Constants;
import com.cedarstudios.cedarmaps.sample.MainActivity;
import com.cedarstudios.cedarmaps.sample.R;
import com.cedarstudios.cedarmapssdk.tileprovider.CedarMapsTileLayer;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Icon;
import com.mapbox.mapboxsdk.overlay.ItemizedIconOverlay;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.views.MapView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

public class ItemizedIconOverlayTestFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        MapView mapView = (MapView) view.findViewById(R.id.mapView);

        SharedPreferences pref = getActivity().getSharedPreferences(MainActivity.PREF_NAME,
                Context.MODE_PRIVATE);
        String accessToken = pref.getString(MainActivity.PREF_ID_ACCESS_TOKEN, "");
        mapView.setAccessToken(accessToken);

        CedarMapsTileLayer cedarMapsTileLayer = new CedarMapsTileLayer(Constants.MAPID_CEDARMAPS_STREETS);
        mapView.setTileSource(cedarMapsTileLayer);

        mapView.setCenter(new LatLng(35.763269, 51.431954));
        mapView.setZoom(15);

        ArrayList<Marker> markers = new ArrayList<Marker>();
        markers.add(new Marker(mapView, getString(R.string.haghani_metro), null,
                new LatLng(35.759926, 51.432512)));
        markers.add(new Marker(mapView, getString(R.string.third_street), null,
                new LatLng(35.762329, 51.429722)));
        markers.add(new Marker(mapView, getString(R.string.haghani_way), null,
                new LatLng(35.759055, 51.427362)));
        markers.add(new Marker(mapView, getString(R.string.tabrizian), null,
                new LatLng(35.762538, 51.435173)));

        for (Marker marker : markers) {
            marker.setIcon(new Icon(getActivity(), Icon.Size.MEDIUM, "marker-stroked", "#068a0a"));
        }

        mapView.addItemizedOverlay(new ItemizedIconOverlay(getActivity(), markers,
                new ItemizedIconOverlay.OnItemGestureListener<Marker>() {
                    @Override
                    public boolean onItemSingleTapUp(int i, Marker marker) {
                        Toast.makeText(getActivity(), marker.getTitle(), Toast.LENGTH_SHORT).show();
                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(int i, Marker marker) {
                        Toast.makeText(getActivity(), marker.getTitle(), Toast.LENGTH_LONG).show();
                        return true;
                    }
                }));

        return view;
    }
}
