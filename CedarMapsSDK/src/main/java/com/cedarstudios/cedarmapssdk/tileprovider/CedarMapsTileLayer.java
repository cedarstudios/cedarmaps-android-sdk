package com.cedarstudios.cedarmapssdk.tileprovider;

import com.cedarstudios.cedarmapssdk.CedarMaps;
import com.cedarstudios.cedarmapssdk.CedarMapsConstants;
import com.cedarstudios.cedarmapssdk.CedarMapsException;
import com.cedarstudios.cedarmapssdk.CedarMapsFactory;
import com.cedarstudios.cedarmapssdk.CedarMapsTileLayerListener;
import com.cedarstudios.cedarmapssdk.auth.OAuth2Token;
import com.cedarstudios.cedarmapssdk.config.Configuration;
import com.cedarstudios.cedarmapssdk.utils.CedarMapsUtils;
import com.mapbox.mapboxsdk.geometry.BoundingBox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.tileprovider.MapTile;
import com.mapbox.mapboxsdk.tileprovider.MapTileCache;
import com.mapbox.mapboxsdk.tileprovider.tilesource.TileLayer;
import com.mapbox.mapboxsdk.tileprovider.tilesource.WebSourceTileLayer;
import com.mapbox.mapboxsdk.util.NetworkUtils;
import com.mapbox.mapboxsdk.util.constants.UtilConstants;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.UUID;

public class CedarMapsTileLayer extends WebSourceTileLayer {

    private JSONObject tileJSON;

    private Cache cache;

    private String mAurl;

    private CedarMapsTileLayerListener mTileLayerListener;

    private CedarMapsTileLayer(final String pId, final String url, final boolean enableSSL) {
        super(pId, url, enableSSL);

        File cacheDir =
                new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());
        try {
            cache = NetworkUtils.getCache(cacheDir, 1024);
        } catch (Exception e) {
            Log.e(TAG, "Cache creation failed.", e);
        }
    }

    public CedarMapsTileLayer(Configuration configuration) {
        this(configuration.getMapId(), configuration.getMapId(), false);
        mConfiguration = configuration;

        if (TextUtils.isEmpty(mConfiguration.getMapId())) {
            throw new NullPointerException("You should init configuration with a valid mapId");
        }
        if (TextUtils.isEmpty(mConfiguration.getOAuthClientId()) || TextUtils
                .isEmpty(mConfiguration.getOAuthClientSecret())) {
            throw new NullPointerException(
                    "You should init configuration with a valid client id and secret");
        }

        init();
    }

    private void init() {
        if (CedarMapsUtils.getAccessToken() == null) {
            new CedarMapsAuthenticateTask() {
                @Override
                protected void onPostExecute(Void aVoid) {
                    CedarMapsTileLayer.super.initialize(mId, mAurl, mEnableSSL);
                    fetchBrandedJSONAndInit(getBrandedJSONURL());
                }
            }.execute();
        } else {
            CedarMapsTileLayer.super.initialize(mId, mAurl, mEnableSSL);
            fetchBrandedJSONAndInit(getBrandedJSONURL());
        }
    }

    private void initWithTileJSON(JSONObject aTileJSON) {
        this.setTileJSON((aTileJSON != null) ? aTileJSON : new JSONObject());
        if (aTileJSON != null) {
            if (this.tileJSON.has("tiles")) {
                try {
                    this.setURL(this.tileJSON.getJSONArray("tiles")
                            .getString(0)
                            .replace(".png", "{2x}.png"));
                } catch (JSONException e) {
                    Log.e(TAG, "Couldn't set tile url", e);
                }
            }
            mMinimumZoomLevel = getJSONFloat(this.tileJSON, "minzoom");
            mMaximumZoomLevel = getJSONFloat(this.tileJSON, "maxzoom");
            mName = this.tileJSON.optString("name");
            mDescription = this.tileJSON.optString("description");
            mAttribution = this.tileJSON.optString("attribution");
            mLegend = this.tileJSON.optString("legend");

            double[] center = getJSONDoubleArray(this.tileJSON, "center", 3);
            if (center != null) {
                mCenter = new LatLng(center[0], center[1], center[2]);
            }
            double[] bounds = getJSONDoubleArray(this.tileJSON, "bounds", 4);
            if (bounds != null) {
                mBoundingBox = new BoundingBox(bounds[3], bounds[2], bounds[1], bounds[0]);
            }
        }
        if (UtilConstants.DEBUGMODE) {
            Log.d(TAG, "TileJSON " + this.tileJSON.toString());
        }
    }

    public JSONObject getTileJSON() {
        return tileJSON;
    }

    public void setTileJSON(JSONObject aTileJSON) {
        this.tileJSON = aTileJSON;
    }

    private float getJSONFloat(JSONObject JSON, String key) {
        float defaultValue = 0;
        if (JSON.has(key)) {
            try {
                return (float) JSON.getDouble(key);
            } catch (JSONException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private double[] getJSONDoubleArray(JSONObject JSON, String key, int length) {
        double[] defaultValue = null;
        if (JSON.has(key)) {
            try {
                boolean valid = false;
                double[] result = new double[length];
                Object value = JSON.get(key);
                if (value instanceof JSONArray) {
                    JSONArray array = ((JSONArray) value);
                    if (array.length() == length) {
                        for (int i = 0; i < array.length(); i++) {
                            result[i] = array.getDouble(i);
                        }
                        valid = true;
                    }
                } else {
                    String[] array = JSON.getString(key).split(",");
                    if (array.length == length) {
                        for (int i = 0; i < array.length; i++) {
                            result[i] = Double.parseDouble(array[i]);
                        }
                        valid = true;
                    }
                }
                if (valid) {
                    return result;
                }
            } catch (JSONException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    byte[] readFully(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int count; (count = in.read(buffer)) != -1; ) {
            out.write(buffer, 0, count);
        }
        return out.toByteArray();
    }

    private void fetchBrandedJSONAndInit(String url) {
        new RetrieveJSONTask() {
            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                initWithTileJSON(jsonObject);
                if (mTileLayerListener != null) {
                    mTileLayerListener.onPrepared(CedarMapsTileLayer.this);
                }
            }
        }.execute(url);
    }

    private String getBrandedJSONURL() {
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

    class RetrieveJSONTask extends AsyncTask<String, Void, JSONObject> {

        protected JSONObject doInBackground(String... urls) {
            InputStream in = null;
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = NetworkUtils.getHttpURLConnection(url, cache);
                in = connection.getInputStream();
                byte[] response = readFully(in);
                String result = new String(response, "UTF-8");
                return new JSONObject(result);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error closing InputStream: " + e.toString());
                }
            }
        }
    }

    private static final String TAG = "TileJsonTileLayer";

    private String mId;

    private Configuration mConfiguration;

    @Deprecated
    public CedarMapsTileLayer(String mapId) {
        super(mapId, mapId, false);
        throw new IllegalStateException("You should not use CedarMapsTileLayer(mapId) any more");
    }

    @Override
    protected void initialize(final String pId, final String aUrl, final boolean enableSSL) {
        mId = pId;
        mAurl = aUrl;
        mEnableSSL = enableSSL;
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

    public String getCacheKey() {
        return mId;
    }

    @Override
    public Bitmap getBitmapFromURL(MapTile mapTile, String url, MapTileCache aCache) {
        if (!url.startsWith("http")) {
            return null;
        }

        Bitmap bitmap = super.getBitmapFromURL(mapTile, url, aCache);
        if (bitmap != null) {
            return bitmap;
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (Exception ignored) {
        }
        if (response != null && response.code() == 401) {
            CedarMapsFactory factory = new CedarMapsFactory(mConfiguration);
            CedarMaps cedarMaps = factory.getInstance();
            try {
                OAuth2Token oAuth2Token = cedarMaps.getOAuth2Token();
                CedarMapsUtils.setAccessToken(oAuth2Token.getAccessToken());

                init(); // load tile urls again

                return super.getBitmapFromURL(mapTile, url, aCache);
            } catch (CedarMapsException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void setTileLayerListener(CedarMapsTileLayerListener listener) {
        mTileLayerListener = listener;
    }

    class CedarMapsAuthenticateTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            CedarMapsFactory factory = new CedarMapsFactory(mConfiguration);
            CedarMaps cedarMaps = factory.getInstance();
            try {
                OAuth2Token oAuth2Token = cedarMaps.getOAuth2Token();
                CedarMapsUtils.setAccessToken(oAuth2Token.getAccessToken());
            } catch (CedarMapsException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


        }
    }

}