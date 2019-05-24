package com.cedarmaps.sdksampleapp.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarmaps.sdksampleapp.R;
import com.cedarstudios.cedarmapssdk.CedarMaps;
import com.cedarstudios.cedarmapssdk.CedarMapsStyle;
import com.cedarstudios.cedarmapssdk.CedarMapsStyleConfigurator;
import com.cedarstudios.cedarmapssdk.MapView;
import com.cedarstudios.cedarmapssdk.listeners.GeoRoutingResultListener;
import com.cedarstudios.cedarmapssdk.listeners.OnStyleConfigurationListener;
import com.cedarstudios.cedarmapssdk.model.routing.GeoRouting;
import com.cedarstudios.cedarmapssdk.model.routing.Route;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.LineManager;
import com.mapbox.mapboxsdk.plugins.annotation.LineOptions;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.utils.ColorUtils;

import java.util.ArrayList;

public class DirectionFragment extends Fragment {

    private static final String DEPARTURE_IMAGE = "DEPARTURE_IMAGE";
    private static final String DESTINATION_IMAGE = "DESTINATION_IMAGE";

    private MapView mMapView;
    private MapboxMap mMapboxMap;
    private LinearLayout hintLayout;
    private LinearLayout resultLayout;
    private ProgressBar progressBar;
    private TextView hintTextView;
    private TextView distanceTextView;
    private SymbolManager symbolManager;
    private LineManager lineManager;

    private ArrayList<Symbol> symbols = new ArrayList<>();

    public DirectionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_direction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = view.findViewById(R.id.mapView);
        hintLayout = view.findViewById(R.id.direction_hint_layout);
        resultLayout = view.findViewById(R.id.direction_result_layout);
        progressBar = view.findViewById(R.id.direction_progress_bar);
        hintTextView = view.findViewById(R.id.direction_hint_text_view);
        distanceTextView = view.findViewById(R.id.direction_distance_text_view);

        view.findViewById(R.id.direction_reset_button).setOnClickListener(v -> resetToInitialState());

        mMapView.onCreate(savedInstanceState);

        mMapView.getMapAsync(mapboxMap -> {
            mMapboxMap = mapboxMap;

            CedarMapsStyleConfigurator.configure(
                    CedarMapsStyle.VECTOR_LIGHT, new OnStyleConfigurationListener() {
                        @SuppressWarnings("ConstantConditions")
                        @Override
                        public void onSuccess(Style.Builder styleBuilder) {
                            mapboxMap.setStyle(styleBuilder, style -> {
                                style.addImage(DEPARTURE_IMAGE, ContextCompat.getDrawable(getContext(), R.drawable.cedarmaps_marker_icon_start));
                                style.addImage(DESTINATION_IMAGE, ContextCompat.getDrawable(getContext(), R.drawable.cedarmaps_marker_icon_end));
                                symbolManager = new SymbolManager(mMapView, mMapboxMap, style);

                                lineManager = new LineManager(mMapView, mMapboxMap, style);
                            });
                        }

                        @Override
                        public void onFailure(@NonNull String errorMessage) {
                            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });

            mMapboxMap.setMaxZoomPreference(17);
            mMapboxMap.setMinZoomPreference(6);

            mMapboxMap.addOnMapClickListener(latLng -> {
                if (symbols.size() == 0) {
                    addMarkerToMapViewAtPosition(latLng, DEPARTURE_IMAGE);
                } else if (symbols.size() == 1) {
                    addMarkerToMapViewAtPosition(latLng, DESTINATION_IMAGE);
                    computeDirection(symbols.get(0).getLatLng(), symbols.get(1).getLatLng());
                }
                return true;
            });
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
                        if (result.getRoutes() == null) {
                            return;
                        }
                        Route route = result.getRoutes().get(0);
                        Double distance = route.getDistance();
                        if (distance == null) {
                            return;
                        }
                        if (distance > 1000) {
                            distance = distance / 1000.0;
                            distance = (double)Math.round(distance * 100d) / 100d;
                            distanceTextView.setText(String.format("%s Km", distance));
                        } else  {
                            distance = (double)Math.round(distance);
                            distanceTextView.setText(String.format("%sm", distance));
                        }

                        if (route.getGeometry() == null || route.getGeometry().getCoordinates() == null) {
                            return;
                        }
                        ArrayList<LatLng> coordinates = new ArrayList<>(route.getGeometry().getCoordinates());

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
        if (mMapboxMap == null || getContext() == null) {
            return;
        }
        LineOptions options = new LineOptions()
                .withLatLngs(coordinates)
                .withLineWidth(6f)
                .withLineColor(ColorUtils.colorToRgbaString(ContextCompat.getColor(getContext(), R.color.colorPrimary)))
                .withLineOpacity(0.9f);
        lineManager.create(options);

        mMapboxMap.easeCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150), 1000);
    }

    private void resetToInitialState() {
        symbolManager.deleteAll();
        lineManager.deleteAll();
        symbols.clear();

        resultLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        hintTextView.setVisibility(View.VISIBLE);
        hintLayout.setVisibility(View.VISIBLE);
    }

    private void addMarkerToMapViewAtPosition(LatLng coordinate, String imageName) {
        if (mMapboxMap != null && getContext() != null) {
            SymbolOptions options = new SymbolOptions()
                    .withLatLng(coordinate)
                    .withIconOffset(new Float[]{0f, -22f})
                    .withIconImage(imageName);
            symbols.add(symbolManager.create(options));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
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
    public void onDestroyView() {
        super.onDestroyView();
        if (symbolManager != null) {
            symbolManager.onDestroy();
        }
        mMapView.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mMapView = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }
}
