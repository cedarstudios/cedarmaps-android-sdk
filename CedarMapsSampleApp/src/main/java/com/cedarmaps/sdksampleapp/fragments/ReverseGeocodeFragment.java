package com.cedarmaps.sdksampleapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cedarmaps.sdksampleapp.R;
import com.cedarstudios.cedarmapssdk.CedarMaps;
import com.cedarstudios.cedarmapssdk.listeners.OnStyleConfigurationListener;
import com.cedarstudios.cedarmapssdk.MapView;
import com.cedarstudios.cedarmapssdk.listeners.ReverseGeocodeResultListener;
import com.cedarstudios.cedarmapssdk.CedarMapsStyle;
import com.cedarstudios.cedarmapssdk.CedarMapsStyleConfigurator;
import com.cedarstudios.cedarmapssdk.model.geocoder.reverse.ReverseGeocode;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.maps.MapboxMap;

public class ReverseGeocodeFragment extends Fragment {

    private MapView mMapView;
    private MapboxMap mMapboxMap;
    private ProgressBar mProgressBar;
    private AppCompatTextView mTextView;

    public ReverseGeocodeFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_reverse_geocode, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTextView = view.findViewById(R.id.reverse_geocode_textView);
        mMapView = view.findViewById(R.id.mapView);
        mProgressBar = view.findViewById(R.id.reverse_geocode_progressBar);
        mMapView = view.findViewById(R.id.mapView);

        mMapView.onCreate(savedInstanceState);

        mMapView.getMapAsync(mapboxMap -> {
            mMapboxMap = mapboxMap;

            CedarMapsStyleConfigurator.configure(CedarMapsStyle.VECTOR_DARK, new OnStyleConfigurationListener() {
                @Override
                public void onSuccess(@NonNull com.mapbox.mapboxsdk.maps.Style.Builder styleBuilder) {
                    mapboxMap.setStyle(styleBuilder);
                }

                @Override
                public void onFailure(@NonNull String errorMessage) {
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });

            mMapboxMap.setMaxZoomPreference(17);
            mMapboxMap.setMinZoomPreference(6);

            reverseGeocode(mapboxMap.getCameraPosition());

            mMapboxMap.addOnCameraIdleListener(() -> reverseGeocode(mMapboxMap.getCameraPosition()));
        });
    }

    private void reverseGeocode(CameraPosition position) {

        if (TextUtils.isEmpty(mTextView.getText())) {
            mTextView.setVisibility(View.GONE);
        } else {
            mTextView.setVisibility(View.VISIBLE);
        }
        mProgressBar.setVisibility(View.VISIBLE);

        CedarMaps.getInstance().reverseGeocode(
                position.target,
                new ReverseGeocodeResultListener() {
                    @Override
                    public void onSuccess(@NonNull ReverseGeocode result) {
                        mProgressBar.setVisibility(View.GONE);
                        mTextView.setVisibility(View.VISIBLE);

                        mTextView.setText(fullAddressForItem(result));
                    }

                    @Override
                    public void onFailure(@NonNull String errorMessage) {
                        mProgressBar.setVisibility(View.GONE);
                        mTextView.setVisibility(View.VISIBLE);

                        mTextView.setText(getString(R.string.parse_error));
                    }
                });
    }

    private String fullAddressForItem(ReverseGeocode item) {
        String result = "";

        if (!TextUtils.isEmpty(item.getProvince())) {
            result += (getString(R.string.province) + " " + item.getProvince());
        }

        if (!TextUtils.isEmpty(item.getCity())) {
            if (TextUtils.isEmpty(result)) {
                result = item.getCity();
            } else {
                result = result + getString(R.string.comma) + " " + item.getCity();
            }
        }

        if (!TextUtils.isEmpty(item.getLocality())) {
            if (TextUtils.isEmpty(result)) {
                result = item.getLocality();
            } else {
                result = result + getString(R.string.comma) + " " + item.getLocality();
            }
        }

        if (!TextUtils.isEmpty(item.getAddress())) {
            if (TextUtils.isEmpty(result)) {
                result = item.getAddress();
            } else {
                result = result + getString(R.string.comma) + " " + item.getAddress();
            }
        }

        if (!TextUtils.isEmpty(item.getPlace())) {
            if (TextUtils.isEmpty(result)) {
                result = item.getPlace();
            } else {
                result = result + getString(R.string.comma) + " " + item.getPlace();
            }
        }

        return result;
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
