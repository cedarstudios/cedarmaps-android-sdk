package com.cedarstudios.cedarmapssdk;

import com.cedarstudios.cedarmapssdk.tileprovider.tilesource.CedarMapsTileSourceInfo;

public interface CedarMapsTileLayerListener {

    void onPrepared(CedarMapsTileSourceInfo tileLayer);
}
