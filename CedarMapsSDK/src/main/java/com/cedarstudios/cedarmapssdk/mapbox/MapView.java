package com.cedarstudios.cedarmapssdk.mapbox;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.cedarstudios.cedarmapssdk.R;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;

public class MapView extends com.mapbox.mapboxsdk.maps.MapView {

    public MapView(@NonNull Context context) {
        super(context);
    }

    public MapView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MapView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MapView(@NonNull Context context, @Nullable MapboxMapOptions options) {
        super(context, options);
    }

    @Override
    protected void initialize(@NonNull Context context, @NonNull MapboxMapOptions options) {
        super.initialize(context, options);

        ImageView logoView = findViewById(com.mapbox.mapboxsdk.R.id.logoView);
        logoView.setImageResource(R.drawable.attribution_logo);

        ImageView attrView = findViewById(com.mapbox.mapboxsdk.R.id.attributionView);
        attrView.setClickable(false);
        attrView.setEnabled(false);
        attrView.setAlpha(0f);
    }
}
