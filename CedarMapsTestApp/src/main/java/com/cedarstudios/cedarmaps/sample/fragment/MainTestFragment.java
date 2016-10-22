package com.cedarstudios.cedarmaps.sample.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cedarstudios.cedarmaps.sample.Constants;
import com.cedarstudios.cedarmaps.sample.R;
import com.cedarstudios.cedarmapssdk.CedarMapsTileLayerListener;
import com.cedarstudios.cedarmapssdk.config.Configuration;
import com.cedarstudios.cedarmapssdk.config.ConfigurationBuilder;
import com.cedarstudios.cedarmapssdk.tileprovider.CedarMapTileProvider;
import com.cedarstudios.cedarmapssdk.tileprovider.tilesource.CedarMapsTileSource;
import com.cedarstudios.cedarmapssdk.tileprovider.tilesource.CedarMapsTileSourceInfo;
import com.cedarstudios.cedarmapssdk.view.MapView;

import org.osmdroid.util.GeoPoint;

public class MainTestFragment extends Fragment {

    protected MapView mMapView;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = (MapView) view.findViewById(R.id.mapView);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Configuration
                configuration = new ConfigurationBuilder()
                .setClientId(Constants.CLIENT_ID)
                .setClientSecret(Constants.CLIENT_SECRET)
                .setMapId(Constants.MAPID_CEDARMAPS_STREETS)
                .build();

        final CedarMapsTileSourceInfo cedarMapsTileSourceInfo = new CedarMapsTileSourceInfo(getContext(), configuration);
        cedarMapsTileSourceInfo.setTileLayerListener(new CedarMapsTileLayerListener() {
            @Override
            public void onPrepared(CedarMapsTileSourceInfo tileLayer) {

                CedarMapsTileSource cedarMapsTileSource = new CedarMapsTileSource(tileLayer);
                CedarMapTileProvider provider = new CedarMapTileProvider(getActivity().getApplicationContext(), cedarMapsTileSource);
                mMapView.setTileProvider(provider);
                mMapView.getController().setZoom(12);
                mMapView.getController().setCenter(new GeoPoint(35.6961, 51.4231));

                onMapLoaded();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mMapView.onDetach();
        mMapView = null;
    }

    protected void onMapLoaded() {

    }

}
