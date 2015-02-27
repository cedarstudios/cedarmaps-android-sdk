package com.cedarstudios.cedarmaps.sample.fragment;

import com.cedarstudios.cedarmaps.sample.Constants;
import com.cedarstudios.cedarmaps.sample.MainActivity;
import com.cedarstudios.cedarmaps.sample.R;
import com.cedarstudios.cedarmapssdk.CedarMaps;
import com.cedarstudios.cedarmapssdk.CedarMapsTileLayerListener;
import com.cedarstudios.cedarmapssdk.config.Configuration;
import com.cedarstudios.cedarmapssdk.config.ConfigurationBuilder;
import com.cedarstudios.cedarmapssdk.tileprovider.CedarMapsTileLayer;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainTestFragment extends Fragment {

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

                mapView.setZoom(12);
                mapView.setCenter(new LatLng(35.6961, 51.4231)); // center of tehran
            }
        });

        return view;
    }

}
