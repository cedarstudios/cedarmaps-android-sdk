package com.cedarstudios.cedarmaps.sample.fragment;

import com.cedarstudios.cedarmaps.sample.Constants;
import com.cedarstudios.cedarmaps.sample.MainActivity;
import com.cedarstudios.cedarmaps.sample.R;
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

        SharedPreferences pref = getActivity().getSharedPreferences(MainActivity.PREF_NAME,
                Context.MODE_PRIVATE);
        String accessToken = pref.getString(MainActivity.PREF_ID_ACCESS_TOKEN, "");
        MapView mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.setAccessToken(accessToken);

        CedarMapsTileLayer cedarMapsTileLayer = new CedarMapsTileLayer(Constants.MAPID_CEDARMAPS_STREETS);
        mapView.setTileSource(cedarMapsTileLayer);

        mapView.setCenter(new LatLng(35.6961, 51.4231)); // center of tehran
        mapView.setZoom(12);

        return view;
    }

}
