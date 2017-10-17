package com.cedarmaps.sdksampleapp.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarmaps.sdksampleapp.R;
import com.cedarstudios.cedarmapssdk.CedarMaps;
import com.cedarstudios.cedarmapssdk.MapView;
import com.cedarstudios.cedarmapssdk.listeners.GeoRoutingResultListener;
import com.cedarstudios.cedarmapssdk.model.routing.GeoRouting;
import com.cedarstudios.cedarmapssdk.model.routing.Route;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.ArrayList;

public class DirectionFragment extends Fragment {

    private MapView mMapView;
    private MapboxMap mMapboxMap;
    private Button resetButton;
    private LinearLayout hintLayout;
    private LinearLayout resultLayout;
    private ProgressBar progressBar;
    private TextView hintTextView;
    private TextView distanceTextView;

    private ArrayList<Marker> markers = new ArrayList<Marker>();

    public DirectionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_direction, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = (MapView) view.findViewById(R.id.mapView);
        resetButton = (Button) view.findViewById(R.id.direction_reset_button);
        hintLayout = (LinearLayout) view.findViewById(R.id.direction_hint_layout);
        resultLayout = (LinearLayout) view.findViewById(R.id.direction_result_layout);
        progressBar = (ProgressBar) view.findViewById(R.id.direction_progress_bar);
        hintTextView = (TextView) view.findViewById(R.id.direction_hint_text_view);
        distanceTextView = (TextView) view.findViewById(R.id.direction_distance_text_view);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMapboxMap.clear();
                markers.clear();

                resultLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                hintTextView.setVisibility(View.VISIBLE);
                hintLayout.setVisibility(View.VISIBLE);
            }
        });

        mMapView.onCreate(savedInstanceState);

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mMapboxMap = mapboxMap;

                mMapboxMap.setMaxZoomPreference(17);
                mMapboxMap.setMinZoomPreference(6);

                mMapboxMap.setOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {
                        if (markers.size() == 0) {
                            addMarkerToMapViewAtPosition(latLng, R.drawable.cedarmaps_marker_icon_start);
                        } else if (markers.size() == 1) {
                            addMarkerToMapViewAtPosition(latLng, R.drawable.cedarmaps_marker_icon_end);
                            computeDirection(markers.get(0).getPosition(), markers.get(1).getPosition());
                        }
                    }
                });
            }
        });

    }

    private void computeDirection(LatLng departure, LatLng destination) {
        progressBar.setVisibility(View.VISIBLE);
        hintTextView.setVisibility(View.GONE);
        progressBar.animate();

        CedarMaps.getInstance().direction(departure, destination,
                new GeoRoutingResultListener() {
                    @Override
                    public void onSuccess(@NonNull GeoRouting result) {
                        progressBar.clearAnimation();

                        Route route = result.getRoutes().get(0);
                        Double distance = route.getDistance();
                        if (distance > 1000) {
                            distance = distance / 1000.0;
                            distance = (double)Math.round(distance * 100d) / 100d;
                            distanceTextView.setText("" + distance + " Km");
                        } else  {
                            distance = (double)Math.round(distance);
                            distanceTextView.setText("" + distance + "m");
                        }

                        ArrayList<LatLng> coordinates = new ArrayList<LatLng>();
                        for (int i = 0; i < route.getGeometry().getCoordinates().size(); i++) {
                            coordinates.add(route.getGeometry().getCoordinates().get(i));
                        }

                        drawCoordinatesInBound(coordinates, route.getBoundingBox());

                        hintLayout.setVisibility(View.GONE);
                        resultLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(@NonNull String error) {
                        progressBar.clearAnimation();
                        resetToInitialState();
                        Toast.makeText(getActivity(),
                                getString(R.string.direction_receiving_failed) + "\n" + error,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void drawCoordinatesInBound(ArrayList<LatLng> coordinates, LatLngBounds bounds) {
        if (mMapboxMap == null) {
            return;
        }
        mMapboxMap.addPolyline(new PolylineOptions()
                .addAll(coordinates)
                .color(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                .width(6)
                .alpha((float) 0.9));

        mMapboxMap.easeCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150), 1000);
    }

    private void resetToInitialState() {
        mMapboxMap.clear();
        markers.clear();

        resultLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        hintTextView.setVisibility(View.VISIBLE);
        hintLayout.setVisibility(View.VISIBLE);
    }

    private void addMarkerToMapViewAtPosition(LatLng coordinate, int markerImageID) {
        if (mMapboxMap != null) {
            Marker marker = mMapboxMap.addMarker(new MarkerViewOptions()
                    .position(coordinate)
                    .icon(IconFactory.getInstance(getContext()).fromResource(markerImageID))
            );
            markers.add(marker);
        }
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
