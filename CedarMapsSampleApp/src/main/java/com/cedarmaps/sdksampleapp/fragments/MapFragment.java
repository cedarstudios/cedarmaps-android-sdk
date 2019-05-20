package com.cedarmaps.sdksampleapp.fragments;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.cedarmaps.sdksampleapp.Constants;
import com.cedarmaps.sdksampleapp.R;
import com.cedarstudios.cedarmapssdk.listeners.OnStyleConfigurationListener;
import com.cedarstudios.cedarmapssdk.mapbox.MapView;
import com.cedarstudios.cedarmapssdk.mapbox.StyleConfigurator;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.lang.ref.WeakReference;
import java.util.List;

import static android.os.Looper.getMainLooper;

public class MapFragment extends Fragment implements PermissionsListener {

    private MapView mMapView;
    private MapboxMap mMapboxMap;
    private LocationEngine mLocationEngine = null;
    private long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    private MapFragmentLocationCallback callback = new MapFragmentLocationCallback(this);

    private PermissionsManager mPermissionsManager;

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

            StyleConfigurator.configure(
                    com.cedarstudios.cedarmapssdk.mapbox.Style.VECTOR_LIGHT, new OnStyleConfigurationListener() {
                @Override
                public void onSuccess(com.mapbox.mapboxsdk.maps.Style.Builder styleBuilder) {
                    mapboxMap.setStyle(styleBuilder, style -> {
                        //Add marker to map
                        addMarkerToMapViewAtPosition(Constants.VANAK_SQUARE);

                        if (PermissionsManager.areLocationPermissionsGranted(getActivity())) {
                            enableLocationComponent(style);
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull String errorMessage) {
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });

            mMapboxMap.setMaxZoomPreference(18);
            mMapboxMap.setMinZoomPreference(6);
            mMapboxMap.setCameraPosition(
                    new CameraPosition.Builder()
                            .target(Constants.VANAK_SQUARE)
                            .zoom(15)
                            .build());

            //Set a touch event listener on the map
            mMapboxMap.addOnMapClickListener(point -> {
//                removeAllMarkersFromMapView();
                addMarkerToMapViewAtPosition(point);
                return true;
            });

            setupCurrentLocationButton();
        });
    }

    //Add a marker to the map
    private void addMarkerToMapViewAtPosition(LatLng coordinate) {
        if (mMapboxMap != null && mMapboxMap.getStyle() != null) {
            com.mapbox.mapboxsdk.maps.Style style = mMapboxMap.getStyle();

            String markerIconId = "marker-icon-id";

            if (style.getImage(markerIconId) == null) {
                style.addImage(markerIconId,
                        BitmapFactory.decodeResource(
                                getResources(), R.drawable.cedarmaps_marker_icon_default));
            }

            String sourceId = "source-id";

            GeoJsonSource geoJsonSource;
            if (style.getSource(sourceId) == null) {
                geoJsonSource = new GeoJsonSource(sourceId);
                style.addSource(geoJsonSource);
            } else {
                geoJsonSource = (GeoJsonSource)style.getSource(sourceId);
            }
            if (geoJsonSource == null) {
                return;
            }
            style.removeSource(geoJsonSource);

            List<Feature> features = geoJsonSource.querySourceFeatures(null);
            Feature feature = Feature.fromGeometry(
                    Point.fromLngLat(coordinate.getLongitude(), coordinate.getLatitude()));
            if (features.isEmpty()) {
                geoJsonSource.setGeoJson(feature);
            } else {
                features.add(feature);
                geoJsonSource.setGeoJson(FeatureCollection.fromFeatures(features));
            }

            style.addSource(geoJsonSource);

            String symbolLayerId = "layer-id";
            style.removeLayer(symbolLayerId);

            SymbolLayer symbolLayer = new SymbolLayer(symbolLayerId, sourceId);
            symbolLayer.withProperties(
                    PropertyFactory.iconImage(markerIconId)
            );
            style.addLayer(symbolLayer);
        }
    }

    //Clear all markers on the map
    private void removeAllMarkersFromMapView() {
        if (mMapboxMap != null && mMapboxMap.getStyle() != null) {
            com.mapbox.mapboxsdk.maps.Style style = mMapboxMap.getStyle();

            String markerIconId = "marker-icon-id";
            String sourceId = "source-id";
            String symbolLayerId = "layer-id";

            style.removeLayer(symbolLayerId);
            style.removeSource(sourceId);
            style.removeImage(markerIconId);
        }
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
            if (mMapboxMap.getStyle() != null) {
                enableLocationComponent(mMapboxMap.getStyle());
            }

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
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (getActivity() == null) {
            return;
        }
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getActivity())) {

            LocationComponent locationComponent = mMapboxMap.getLocationComponent();

            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(getActivity(), loadedMapStyle)
                            .useDefaultLocationEngine(false)
                            .build();

            locationComponent.activateLocationComponent(locationComponentActivationOptions);
            locationComponent.setLocationComponentEnabled(true);

            initializeLocationEngine();
        } else {
            mPermissionsManager = new PermissionsManager(this);
            mPermissionsManager.requestLocationPermissions(getActivity());
        }
    }

    @SuppressWarnings( {"MissingPermission"})
    private void initializeLocationEngine() {
        if (getActivity() == null) {
            return;
        }

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

        mLocationEngine.requestLocationUpdates(request, callback, getMainLooper());
        mLocationEngine.getLastLocation(callback);
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
    public void onDestroyView() {
        super.onDestroyView();
        if (mLocationEngine != null) {
            mLocationEngine.removeLocationUpdates(callback);
        }
        mMapView.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mMapView = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPermissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(getActivity(), R.string.location_is_needed_to_function, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            if (mMapboxMap.getStyle() != null) {
                enableLocationComponent(mMapboxMap.getStyle());
                toggleCurrentLocationButton();
            }
        } else {
            Toast.makeText(getActivity(), R.string.location_is_needed_to_function, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    private static class MapFragmentLocationCallback implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<MapFragment> fragmentWeakReference;

        MapFragmentLocationCallback(MapFragment fragment) {
            fragmentWeakReference = new WeakReference<>(fragment);
        }

        /* The LocationEngineCallback interface's method which fires when the device's location has changed.
        *
        * @param result the LocationEngineResult object which has the last known location within it.
        */
        @Override
        public void onSuccess(LocationEngineResult result) {
            MapFragment fragment = fragmentWeakReference.get();

            if (fragment != null) {
                Location location = result.getLastLocation();

                if (location == null) {
                    return;
                }

                if (fragment.mMapboxMap != null && result.getLastLocation() != null) {
                    fragment.mMapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());
                }
            }
        }

        @Override
        public void onFailure(@NonNull Exception exception) {
            Log.d("LocationChange", exception.getLocalizedMessage());
        }
    }
}
