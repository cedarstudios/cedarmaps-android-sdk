package com.cedarstudios.cedarmaps.sample.fragment;

import com.cedarstudios.cedarmaps.sample.Constants;
import com.cedarstudios.cedarmaps.sample.R;
import com.cedarstudios.cedarmapssdk.CedarMapsTileLayerListener;
import com.cedarstudios.cedarmapssdk.config.Configuration;
import com.cedarstudios.cedarmapssdk.config.ConfigurationBuilder;
import com.cedarstudios.cedarmapssdk.tileprovider.CedarMapsTileLayer;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.ItemizedIconOverlay;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.views.MapView;

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

        final MapView mapView = (MapView) view.findViewById(R.id.mapView);

        Configuration
                configuration = new ConfigurationBuilder()
                .setClientId(Constants.CLIENT_ID)
                .setClientSecret(Constants.CLIENT_SECRET)
                .setMapId(Constants.MAPID_CEDARMAPS_STREETS)
                .build();

        final CedarMapsTileLayer cedarMapsTileLayer = new CedarMapsTileLayer(configuration);
        cedarMapsTileLayer.setTileLayerListener(new CedarMapsTileLayerListener() {
            @Override
            public void onPrepared(CedarMapsTileLayer tileLayer) {
                mapView.setTileSource(tileLayer);

                mapView.setZoom(15);
                mapView.setCenter(new LatLng(35.762734, 51.432126));

                ArrayList<Marker> markers = new ArrayList<>();

                markers.add(new Marker(mapView, getString(R.string.haghani_metro), "",
                        new LatLng(35.759926, 51.432512)));
                markers.add(new Marker(mapView, getString(R.string.third_street), "",
                        new LatLng(35.762329, 51.429722)));
                markers.add(new Marker(mapView, getString(R.string.haghani_way), "",
                        new LatLng(35.759055, 51.427362)));
                markers.add(new Marker(mapView, getString(R.string.tabrizian), "",
                        new LatLng(35.762538, 51.435173)));

                for (Marker marker : markers) {
                    marker.setMarker(getResources().getDrawable(R.drawable.ic_location_on));
                }

                mapView.addItemizedOverlay(new ItemizedIconOverlay(getActivity(), markers,
                        new ItemizedIconOverlay.OnItemGestureListener<Marker>() {
                            @Override
                            public boolean onItemSingleTapUp(int i, Marker marker) {
                                Toast.makeText(getActivity(), marker.getTitle(), Toast.LENGTH_SHORT)
                                        .show();
                                return true;
                            }

                            @Override
                            public boolean onItemLongPress(int i, Marker marker) {
                                Toast.makeText(getActivity(), marker.getTitle(), Toast.LENGTH_LONG)
                                        .show();
                                return true;
                            }
                        }));
            }
        });

        return view;
    }
}
