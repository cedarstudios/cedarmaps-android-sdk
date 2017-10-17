package com.cedarmaps.sdksampleapp.fragments;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cedarmaps.sdksampleapp.Constants;
import com.cedarmaps.sdksampleapp.R;

import com.cedarstudios.cedarmapssdk.MapView;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.MyBearingTracking;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

public class MapFragment extends Fragment {

    private MapView mMapView;
    private MapboxMap mMapboxMap;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = (MapView) view.findViewById(R.id.mapView);

        mMapView.onCreate(savedInstanceState);

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mMapboxMap = mapboxMap;

                mMapboxMap.setMaxZoomPreference(17);
                mMapboxMap.setMinZoomPreference(6);

                //Move map to a certain position
                animateToCoordinate(Constants.VANAK_SQUARE, 16);

                //Add marker to map
                addMarkerToMapViewAtPosition(Constants.VANAK_SQUARE);

                //Set a touch event listener on the map
                mMapboxMap.setOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {
                        removeAllMarkersFromMapView();
                        addMarkerToMapViewAtPosition(latLng);
                    }
                });

                setupCurrentLocationButton();
            }
        });
    }

    //Add a marker to the map
    private void addMarkerToMapViewAtPosition(LatLng coordinate) {
        if (mMapboxMap != null) {
            mMapboxMap.addMarker(new MarkerViewOptions()
                    .position(coordinate)
            );
        }
    }

    //Clear all markers on the map
    private void removeAllMarkersFromMapView() {
        mMapboxMap.clear();
    }

    private void animateToCoordinate(LatLng coordinate, int zoomLevel) {
        CameraPosition position = new CameraPosition.Builder()
                .target(coordinate)
                .zoom(zoomLevel)
                .build();
        mMapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
    }

    private void setupCurrentLocationButton() {
        FloatingActionButton fb = (FloatingActionButton) getView().findViewById(R.id.showCurrentLocationButton);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasLocationPermissions()) {
                    mMapboxMap.setMyLocationEnabled(true);
                }
                switch (mMapboxMap.getTrackingSettings().getMyBearingTrackingMode()) {
                    case MyBearingTracking.NONE:
                        Location location = mMapboxMap.getMyLocation();
                        if (location != null) {
                            animateToCoordinate(new LatLng(location.getLatitude(),location.getLongitude()), 17);
                            mMapboxMap.getTrackingSettings().setMyBearingTrackingMode(MyBearingTracking.GPS);
                        }
                        break;
                    case MyBearingTracking.GPS:
                        mMapboxMap.getTrackingSettings().setMyBearingTrackingMode(MyBearingTracking.COMPASS);
                        break;
                    case MyBearingTracking.COMPASS:
                        mMapboxMap.getTrackingSettings().setMyBearingTrackingMode(MyBearingTracking.NONE);
                        break;
                }
            }
        });
    }

    private boolean hasLocationPermissions() {
        //Request Location Permission
        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.PERMISSION_LOCATION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();

    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mMapView = null;
    }

}
