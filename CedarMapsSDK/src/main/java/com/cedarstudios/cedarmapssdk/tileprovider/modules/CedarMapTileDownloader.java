package com.cedarstudios.cedarmapssdk.tileprovider.modules;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import com.cedarstudios.cedarmapssdk.CedarMaps;
import com.cedarstudios.cedarmapssdk.CedarMapsException;
import com.cedarstudios.cedarmapssdk.CedarMapsFactory;
import com.cedarstudios.cedarmapssdk.auth.OAuth2Token;
import com.cedarstudios.cedarmapssdk.tileprovider.constants.CedarMapTileProviderConstants;
import com.cedarstudios.cedarmapssdk.tileprovider.tilesource.CedarMapsTileSource;
import com.cedarstudios.cedarmapssdk.utils.CedarMapsUtils;

import org.osmdroid.api.IMapView;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.MapTileRequestState;
import org.osmdroid.tileprovider.modules.IFilesystemCache;
import org.osmdroid.tileprovider.modules.INetworkAvailablityCheck;
import org.osmdroid.tileprovider.modules.MapTileDownloader;
import org.osmdroid.tileprovider.tilesource.BitmapTileSourceBase;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.util.StreamUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

public class CedarMapTileDownloader extends MapTileDownloader {


    private final IFilesystemCache mFilesystemCache;

    private final AtomicReference<OnlineTileSourceBase> mTileSource = new AtomicReference<>();

    private final INetworkAvailablityCheck mNetworkAvailablityCheck;


    public CedarMapTileDownloader(ITileSource pTileSource, IFilesystemCache pFilesystemCache, INetworkAvailablityCheck pNetworkAvailablityCheck) {
        super(pTileSource, pFilesystemCache, pNetworkAvailablityCheck);
        mFilesystemCache = pFilesystemCache;
        mNetworkAvailablityCheck = pNetworkAvailablityCheck;
        setCedarTileSource(pTileSource);
    }

    @Override
    public ITileSource getTileSource() {
        return mTileSource.get();
    }

    public void setCedarTileSource(final ITileSource tileSource) {
        super.setTileSource(tileSource);

        // We are only interested in OnlineTileSourceBase tile sources
        if (tileSource instanceof OnlineTileSourceBase) {
            mTileSource.set((OnlineTileSourceBase) tileSource);
        } else {
            // Otherwise shut down the tile downloader
            mTileSource.set(null);
        }
    }

    @Override
    protected Runnable getTileLoader() {
        return new CedarTileLoader();
    }

    protected class CedarTileLoader extends TileLoader {
        @Override
        public Drawable loadTile(MapTileRequestState aState) throws CantContinueException {
            OnlineTileSourceBase tileSource = mTileSource.get();
            if (tileSource == null) {
                return null;
            }

            InputStream in = null;
            OutputStream out = null;
            HttpURLConnection c = null;
            final MapTile tile = aState.getMapTile();

            try {

                if (mNetworkAvailablityCheck != null
                        && !mNetworkAvailablityCheck.getNetworkAvailable()) {
                    if (CedarMapTileProviderConstants.DEBUGMODE) {
                        Log.d(IMapView.LOGTAG, "Skipping " + getName() + " due to NetworkAvailabliltyCheck.");
                    }
                    return null;
                }

                final String tileURLString = tileSource.getTileURLString(tile);

                if (CedarMapTileProviderConstants.DEBUGMODE) {
                    Log.d(IMapView.LOGTAG, "Downloading Maptile from url: " + tileURLString);
                }

                if (TextUtils.isEmpty(tileURLString)) {
                    return null;
                }

                c = (HttpURLConnection) new URL(tileURLString).openConnection();
                c.setUseCaches(true);
                c.setRequestProperty(CedarMapTileProviderConstants.USER_AGENT, CedarMapTileProviderConstants.getUserAgentValue());
                c.connect();


                // Check to see if we got success

                if (c.getResponseCode() != 200) {
                    Log.w(IMapView.LOGTAG, "Problem downloading MapTile: " + tile + " HTTP response: " + c.getResponseMessage());

                    if (c.getResponseCode() == 401) {
                        CedarMapsFactory factory = new CedarMapsFactory(((CedarMapsTileSource) mTileSource.get()).getConfiguration());
                        CedarMaps cedarMaps = factory.getInstance();
                        try {
                            OAuth2Token oAuth2Token = cedarMaps.getOAuth2Token();
                            CedarMapsUtils.setAccessToken(oAuth2Token.getAccessToken());
                            ((CedarMapsTileSource) mTileSource.get()).setAccessToken(oAuth2Token.getAccessToken());

                            loadTile(aState);

                        } catch (CedarMapsException e) {
                            e.printStackTrace();
                        }
                    }
                    return null;
                }


                in = c.getInputStream();

                final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
                out = new BufferedOutputStream(dataStream, StreamUtils.IO_BUFFER_SIZE);
                Date dateExpires = new Date(System.currentTimeMillis() + MAX_CACHED_TILE_AGE);
                final String expires = c.getHeaderField(CedarMapTileProviderConstants.HTTP_EXPIRES_HEADER);
                if (expires != null && expires.length() > 0) {
                    try {
                        dateExpires = CedarMapTileProviderConstants.HTTP_HEADER_SDF.parse(expires);
                    } catch (Exception ex) {
                        if (DEBUG)
                            Log.d(IMapView.LOGTAG, "Unable to parse expiration tag for tile, using default, server returned " + expires, ex);
                    }
                }
                tile.setExpires(dateExpires);
                StreamUtils.copy(in, out);
                out.flush();
                final byte[] data = dataStream.toByteArray();
                final ByteArrayInputStream byteStream = new ByteArrayInputStream(data);

                // Save the data to the filesystem cache
                if (mFilesystemCache != null) {
                    mFilesystemCache.saveFile(tileSource, tile, byteStream);
                    byteStream.reset();
                }
                final Drawable result = tileSource.getDrawable(byteStream);

                return result;
            } catch (final UnknownHostException e) {
                // no network connection so empty the queue
                Log.w(IMapView.LOGTAG, "UnknownHostException downloading MapTile: " + tile + " : " + e);
                throw new CantContinueException(e);
            } catch (final BitmapTileSourceBase.LowMemoryException e) {
                // low memory so empty the queue
                Log.w(IMapView.LOGTAG, "LowMemoryException downloading MapTile: " + tile + " : " + e);
                throw new CantContinueException(e);
            } catch (final FileNotFoundException e) {
                Log.w(IMapView.LOGTAG, "Tile not found: " + tile + " : " + e);
            } catch (final IOException e) {
                Log.w(IMapView.LOGTAG, "IOException downloading MapTile: " + tile + " : " + e);
            } catch (final Throwable e) {
                Log.e(IMapView.LOGTAG, "Error downloading MapTile: " + tile, e);
            } finally {
                StreamUtils.closeStream(in);
                StreamUtils.closeStream(out);
                try {
                    c.disconnect();
                } catch (Exception ex) {
                }
            }

            return null;
        }
    }
}
