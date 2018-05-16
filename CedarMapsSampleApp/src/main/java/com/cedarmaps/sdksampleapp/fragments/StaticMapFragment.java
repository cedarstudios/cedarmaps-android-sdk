package com.cedarmaps.sdksampleapp.fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cedarmaps.sdksampleapp.R;
import com.cedarstudios.cedarmapssdk.CedarMaps;
import com.cedarstudios.cedarmapssdk.Dimension;
import com.cedarstudios.cedarmapssdk.listeners.StaticMapImageResultListener;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;

public class StaticMapFragment extends Fragment {

    private Button createMapButton;
    private ImageView mapImageView;
    private ProgressBar progressBar;
    private TextView howToTextView;

    private EditText latitude;
    private EditText longitude;
    private EditText zoomLevel;

    public StaticMapFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_static_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        createMapButton = view.findViewById(R.id.static_map_create_button);
        mapImageView = view.findViewById(R.id.static_map_image_view);
        progressBar = view.findViewById(R.id.static_map_progress_bar);
        howToTextView = view.findViewById(R.id.static_map_hint);
        latitude = view.findViewById(R.id.edit_text_center_latitude);
        longitude = view.findViewById(R.id.edit_text_center_longitude);
        zoomLevel = view.findViewById(R.id.edit_text_zoom_level);

        createMapButton.setOnClickListener(v -> {

            if (TextUtils.isEmpty(latitude.getText()) ||
                    TextUtils.isEmpty(longitude.getText()) ||
                    TextUtils.isEmpty(zoomLevel.getText())) {
                Toast.makeText(getContext(), R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
                return;
            }

            v.setEnabled(false);

            progressBar.animate();
            progressBar.setVisibility(View.VISIBLE);
            howToTextView.setVisibility(View.INVISIBLE);

            LatLng coordinate = new LatLng(Double.parseDouble(latitude.getText().toString()), Double.parseDouble(longitude.getText().toString()));
            ArrayList<CedarMaps.StaticMarker> markers = new ArrayList<>();
            markers.add(new CedarMaps.StaticMarker(coordinate, null));

            CedarMaps.getInstance().staticMap(
                    new Dimension(mapImageView.getWidth(), mapImageView.getHeight()),
                    Integer.parseInt(zoomLevel.getText().toString()),
                    coordinate,
                    markers,
                    new StaticMapImageResultListener() {
                        @Override
                        public void onSuccess(@NonNull Bitmap result) {
                            progressBar.clearAnimation();
                            progressBar.setVisibility(View.INVISIBLE);
                            v.setEnabled(true);

                            mapImageView.setImageBitmap(result);
                        }

                        @Override
                        public void onFailure(@NonNull String errorMessage) {
                            progressBar.clearAnimation();
                            progressBar.setVisibility(View.INVISIBLE);
                            howToTextView.setVisibility(View.VISIBLE);
                            howToTextView.setText(R.string.map_download_failed);
                            v.setEnabled(true);

                            Log.e("StaticMapFragment", errorMessage);
                        }
                    });
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }
}
