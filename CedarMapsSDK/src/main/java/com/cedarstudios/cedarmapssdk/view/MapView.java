package com.cedarstudios.cedarmapssdk.view;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;

import com.cedarstudios.cedarmapssdk.tileprovider.CedarMapTileProvider;
import com.cedarstudios.cedarmapssdk.tileprovider.tilesource.CedarMapsTileSource;

import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.MapTileProviderBase;

public class MapView extends org.osmdroid.views.MapView {

    protected MapView(Context context, MapTileProviderBase tileProvider, Handler tileRequestCompleteHandler, AttributeSet attrs) {
        super(context, tileProvider, tileRequestCompleteHandler, attrs);
    }

    public MapView(Context context, AttributeSet attrs) {
        this(context, new FakeTileProvider(), null, attrs);
    }

    public MapView(Context context) {
        super(context);
    }

    public MapView(Context context, MapTileProviderBase aTileProvider) {
        super(context, aTileProvider);
    }

    public MapView(Context context, MapTileProviderBase aTileProvider, Handler tileRequestCompleteHandler) {
        super(context, aTileProvider, tileRequestCompleteHandler);
    }

    @Override
    public void setTileProvider(MapTileProviderBase base) {
        super.setTileProvider(base);

        if (base instanceof CedarMapTileProvider) {
            CedarMapsTileSource cedarMapsTileSource = (CedarMapsTileSource) base.getTileSource();
            setScrollableAreaLimit(cedarMapsTileSource.getBoundingBox());
            invalidate();
            setMinZoomLevel(cedarMapsTileSource.getMinimumZoomLevel());
            setMaxZoomLevel(cedarMapsTileSource.getMaximumZoomLevel());
            setMultiTouchControls(true);
        }
    }

    static class FakeTileProvider extends MapTileProviderBase {

        public FakeTileProvider() {
            super(new CedarMapsTileSource());
        }

        @Override
        public Drawable getMapTile(MapTile pTile) {
            return null;
        }

        @Override
        public void detach() {

        }

        @Override
        public int getMinimumZoomLevel() {
            return 0;
        }

        @Override
        public int getMaximumZoomLevel() {
            return 0;
        }
    }
}
