package com.cedarstudios.cedarmapssdk;

import com.cedarstudios.cedarmapssdk.tileprovider.CedarMapsTileLayer;

public interface CedarMapsTileLayerListener {

    void onPrepared(CedarMapsTileLayer tileLayer);
}
