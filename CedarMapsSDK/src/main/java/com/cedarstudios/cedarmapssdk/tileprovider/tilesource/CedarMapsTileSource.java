package com.cedarstudios.cedarmapssdk.tileprovider.tilesource;

import com.cedarstudios.cedarmapssdk.config.Configuration;

import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.util.BoundingBoxE6;

public class CedarMapsTileSource extends OnlineTileSourceBase {

    private String mMapId = "";
    private String mAccessToken;
    private CedarMapsTileSourceInfo mTileSourceInfo;

    public CedarMapsTileSource() { // only for early initialization. should use CedarMapsTileSource with params
        super("", 11, 18, 256, ".png", new String[]{});
    }

    public CedarMapsTileSource(CedarMapsTileSourceInfo tileLayer) {
        super(tileLayer.getName(), tileLayer.getMinimumZoomLevel(), tileLayer.getMaximumZoomLevel(),
                tileLayer.getTitleSize(), tileLayer.getImageExtension(), new String[]{tileLayer.getBaseUrl()});
        mMapId = tileLayer.getMapId();
        mAccessToken = tileLayer.getAccessToken();
        mTileSourceInfo = tileLayer;
    }

    @Override
    public String getTileURLString(final MapTile aMapTile) {
        String url = getBaseUrl() +
                mMapId +
                "/" +
                aMapTile.getZoomLevel() +
                "/" +
                aMapTile.getX() +
                "/" +
                aMapTile.getY() +
                mTileSourceInfo.getImageExtension() +
                "?access_token=" + mAccessToken;
        return url;
    }

    public BoundingBoxE6 getBoundingBox() {
        return mTileSourceInfo.getBoundingBox();
    }

    public Configuration getConfiguration() {
        return mTileSourceInfo.getConfiguration();
    }

    public void setAccessToken(String accessToken) {
        this.mAccessToken = accessToken;
    }
}