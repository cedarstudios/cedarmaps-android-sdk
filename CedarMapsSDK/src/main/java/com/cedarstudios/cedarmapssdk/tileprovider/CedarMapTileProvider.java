package com.cedarstudios.cedarmapssdk.tileprovider;

import android.content.Context;

import com.cedarstudios.cedarmapssdk.tileprovider.modules.CedarMapTileAssetsProvider;
import com.cedarstudios.cedarmapssdk.tileprovider.modules.CedarMapTileDownloader;
import com.cedarstudios.cedarmapssdk.tileprovider.modules.CedarMapTileFileArchiveProvider;
import com.cedarstudios.cedarmapssdk.tileprovider.modules.CedarMapTileFilesystemProvider;
import com.cedarstudios.cedarmapssdk.tileprovider.modules.CedarTileWriter;

import org.osmdroid.tileprovider.IRegisterReceiver;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.modules.INetworkAvailablityCheck;
import org.osmdroid.tileprovider.modules.NetworkAvailabliltyCheck;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;

public class CedarMapTileProvider extends MapTileProviderBasic {

    public CedarMapTileProvider(Context pContext, ITileSource pTileSource) {
        this(new SimpleRegisterReceiver(pContext), new NetworkAvailabliltyCheck(pContext), pTileSource, pContext);
    }

    public CedarMapTileProvider(IRegisterReceiver pRegisterReceiver, INetworkAvailablityCheck aNetworkAvailablityCheck, ITileSource pTileSource, Context pContext) {
        super(pRegisterReceiver, aNetworkAvailablityCheck, pTileSource, pContext);

        overrideTitleProviderList(pRegisterReceiver, aNetworkAvailablityCheck, pTileSource, pContext);
    }

    private void overrideTitleProviderList(IRegisterReceiver pRegisterReceiver, INetworkAvailablityCheck aNetworkAvailablityCheck, ITileSource pTileSource, Context pContext) {
        mTileProviderList.clear();

        final CedarTileWriter tileWriter = new CedarTileWriter();

        final CedarMapTileAssetsProvider assetsProvider = new CedarMapTileAssetsProvider(pRegisterReceiver, pContext.getAssets(), pTileSource);
        mTileProviderList.add(assetsProvider);

        final CedarMapTileFilesystemProvider fileSystemProvider = new CedarMapTileFilesystemProvider(pRegisterReceiver, pTileSource);
        mTileProviderList.add(fileSystemProvider);

        final CedarMapTileFileArchiveProvider archiveProvider = new CedarMapTileFileArchiveProvider(pRegisterReceiver, pTileSource);
        mTileProviderList.add(archiveProvider);

        final CedarMapTileDownloader downloaderProvider = new CedarMapTileDownloader(pTileSource, tileWriter, aNetworkAvailablityCheck);
        mTileProviderList.add(downloaderProvider);
    }
}
