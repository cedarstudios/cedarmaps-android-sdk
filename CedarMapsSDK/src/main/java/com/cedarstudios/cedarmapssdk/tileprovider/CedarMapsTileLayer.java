package com.cedarstudios.cedarmapssdk.tileprovider;

import com.cedarstudios.cedarmapssdk.CedarMapsConstants;
import com.cedarstudios.cedarmapssdk.utils.CedarMapsUtils;
import com.mapbox.mapboxsdk.tileprovider.tilesource.TileJsonTileLayer;
import com.mapbox.mapboxsdk.tileprovider.tilesource.TileLayer;

import android.text.TextUtils;

import java.util.Locale;

public class CedarMapsTileLayer extends TileJsonTileLayer {

    private String mId;

    public CedarMapsTileLayer(String mapId) {
        super(mapId, mapId, false);
    }

    @Override
    protected void initialize(String pId, String aUrl, boolean enableSSL) {
        mId = pId;
        super.initialize(pId, aUrl, enableSSL);
    }

    @Override
    public TileLayer setURL(final String aUrl) {
        if (!TextUtils.isEmpty(aUrl) && !aUrl.toLowerCase(Locale.US).contains("http://") && !aUrl
                .toLowerCase(Locale.US).contains("https://")) {
            super.setURL(
                    CedarMapsConstants.CEDARMAPS_BASE_URL_V1 + "tiles/" + aUrl
                            + "/{z}/{x}/{y}.png?access_token="
                            + CedarMapsUtils.getAccessToken());
        } else {
            super.setURL(aUrl);
        }
        return this;
    }

    @Override
    protected String getBrandedJSONURL() {
        String url = String
                .format(Locale.ENGLISH, CedarMapsConstants.CEDARMAPS_BASE_URL_V1
                        + "tiles/%s.json?access_token=%s&secure=1", mId,
                        CedarMapsUtils.getAccessToken());
        if (!mEnableSSL) {
            url = url.replace("https://", "http://");
            url = url.replace("&secure=1", "");
        }

        return url;
    }

    public String getCacheKey() {
        return mId;
    }
}