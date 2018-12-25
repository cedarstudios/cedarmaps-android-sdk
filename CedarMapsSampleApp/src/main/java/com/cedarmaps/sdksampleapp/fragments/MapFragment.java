package com.cedarmaps.sdksampleapp.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.cedarmaps.sdksampleapp.Constants;
import com.cedarmaps.sdksampleapp.R;
import com.cedarstudios.cedarmapssdk.MapView;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

public class MapFragment extends Fragment implements LocationEngineListener {

    private MapView mMapView;
    private MapboxMap mMapboxMap;
    private LocationEngine mLocationEngine = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = view.findViewById(R.id.mapView);

        mMapView.onCreate(savedInstanceState);

        mMapView.getMapAsync(mapboxMap -> {
            mMapboxMap = mapboxMap;

            mMapboxMap.setMaxZoomPreference(17);
            mMapboxMap.setMinZoomPreference(6);
            mMapboxMap.setCameraPosition(
                    new CameraPosition.Builder()
                            .target(Constants.VANAK_SQUARE)
                            .zoom(15)
                            .build());

            if (PermissionsManager.areLocationPermissionsGranted(getActivity())) {
                enableLocationComponent();
            }

            //Add marker to map
            addMarkerToMapViewAtPosition(Constants.VANAK_SQUARE);

            //Set a touch event listener on the map
            mMapboxMap.addOnMapClickListener(point -> {
                removeAllMarkersFromMapView();
                addMarkerToMapViewAtPosition(point);
            });

            setupCurrentLocationButton();
        });
    }

    //Add a marker to the map
    private void addMarkerToMapViewAtPosition(LatLng coordinate) {
        if (mMapboxMap != null) {
            mMapboxMap.addMarker(new MarkerOptions().position(coordinate));
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

    @SuppressLint("MissingPermission")
    private void setupCurrentLocationButton() {
        FloatingActionButton fb = getView().findViewById(R.id.showCurrentLocationButton);
        fb.setOnClickListener(v -> {
            enableLocationComponent();

            toggleCurrentLocationButton();
        });
    }

    @SuppressLint("MissingPermission")
    private void toggleCurrentLocationButton() {
        if (!mMapboxMap.getLocationComponent().isLocationComponentEnabled()) {
            return;
        }
        Location location = mMapboxMap.getLocationComponent().getLastKnownLocation();
        if (location != null) {
            animateToCoordinate(new LatLng(location.getLatitude(),location.getLongitude()), 16);
        }

        switch (mMapboxMap.getLocationComponent().getRenderMode()) {
            case RenderMode.NORMAL:
                mMapboxMap.getLocationComponent().setRenderMode(RenderMode.COMPASS);
                break;
            case RenderMode.GPS:
                mMapboxMap.getLocationComponent().setRenderMode(RenderMode.NORMAL);
                break;
            case RenderMode.COMPASS:
                mMapboxMap.getLocationComponent().setRenderMode(RenderMode.NORMAL);
                break;
        }
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent() {
        if (getActivity() == null) {
            return;
        }
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getActivity())) {
            // Create a location engine instance
            initializeLocationEngine();

            mMapboxMap.getLocationComponent().activateLocationComponent(getActivity());
            mMapboxMap.getLocationComponent().setLocationComponentEnabled(true);
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.PERMISSION_LOCATION_REQUEST_CODE);
        }
    }

    @SuppressWarnings( {"MissingPermission"})
    private void initializeLocationEngine() {
        mLocationEngine = new LocationEngineProvider(getContext()).obtainBestLocationEngineAvailable();
        mLocationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        mLocationEngine.activate();

        mLocationEngine.addLocationEngineListener(this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    public void onStart() {
        super.onStart();
        if (PermissionsManager.areLocationPermissionsGranted(getActivity())) {
            if (mLocationEngine != null) {
                mLocationEngine.activate();
            }
        }
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mLocationEngine != null) {
            mLocationEngine.removeLocationUpdates();
            mLocationEngine.removeLocationEngineListener(this);
        }
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
    public void onDestroyView() {
        super.onDestroyView();
        mMapView.onDestroy();
        if (mLocationEngine != null) {
            mLocationEngine.deactivate();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mMapView = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSION_LOCATION_REQUEST_CODE:
                if (!(grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED)) {
                    Toast.makeText(getActivity(), R.string.location_is_needed_to_function, Toast.LENGTH_LONG).show();
                } else {
                    enableLocationComponent();
                    toggleCurrentLocationButton();
                }
                break;
            default:
                break;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected() {
        if (mLocationEngine != null && PermissionsManager.areLocationPermissionsGranted(getActivity())) {
            mLocationEngine.requestLocationUpdates();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }
}
