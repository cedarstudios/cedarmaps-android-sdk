package com.cedarstudios.cedarmapssdk.tileprovider.tilesource;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.cedarstudios.cedarmapssdk.CedarMaps;
import com.cedarstudios.cedarmapssdk.CedarMapsException;
import com.cedarstudios.cedarmapssdk.CedarMapsFactory;
import com.cedarstudios.cedarmapssdk.CedarMapsTileLayerListener;
import com.cedarstudios.cedarmapssdk.auth.OAuth2Token;
import com.cedarstudios.cedarmapssdk.config.Configuration;
import com.cedarstudios.cedarmapssdk.utils.CedarMapsUtils;
import com.cedarstudios.cedarmapssdk.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class CedarMapsTileSourceInfo {

    private JSONObject tileJSON;
    private final static String baseUrl = "http://api.cedarmaps.com/v1/tiles/";
    private int mTileSize;
    private String mTileExtension;
    private CedarMapsTileLayerListener mTileLayerListener;
    private int mMinimumZoomLevel;
    private int mMaximumZoomLevel;
    private String mName;
    private String mDescription;
    private String mAttribution;
    private String mLegend;
    private IGeoPoint mCenter;
    private BoundingBoxE6 mBoundingBox;

    public CedarMapsTileSourceInfo(Context context, Configuration configuration) {
        mConfiguration = configuration;

        if (TextUtils.isEmpty(mConfiguration.getMapId())) {
            throw new NullPointerException("You should init configuration with a valid mapId");
        }
        if (TextUtils.isEmpty(mConfiguration.getOAuthClientId()) || TextUtils.isEmpty(mConfiguration.getOAuthClientSecret())) {
            throw new NullPointerException("You should init configuration with a valid client id and secret");
        }

        int density = context.getResources().getDisplayMetrics().densityDpi;
        mTileExtension = density >= DisplayMetrics.DENSITY_HIGH ? "@2x.png" : ".png";
        mTileSize = density >= DisplayMetrics.DENSITY_HIGH ? 512 : 256;

        init();
    }

    public int getMinimumZoomLevel() {
        return mMinimumZoomLevel;
    }

    public int getMaximumZoomLevel() {
        return mMaximumZoomLevel;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getAttribution() {
        return mAttribution;
    }

    public String getLegend() {
        return mLegend;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public IGeoPoint getCenter() {
        return mCenter;
    }

    public BoundingBoxE6 getBoundingBox() {
        return mBoundingBox;
    }

    private void init() {
        if (CedarMapsUtils.getAccessToken() == null) {
            new CedarMapsAuthenticateTask() {
                @Override
                protected void onPostExecute(Void aVoid) {
                    fetchBrandedJSONAndInit(getBrandedJSONURL());
                }
            }.execute();
        } else {
            fetchBrandedJSONAndInit(getBrandedJSONURL());
        }
    }

    private void initWithTileJSON(JSONObject aTileJSON) {
        this.setTileJSON((aTileJSON != null) ? aTileJSON : new JSONObject());
        if (aTileJSON != null) {
            mMinimumZoomLevel = getJSONFloat(this.tileJSON, "minzoom");
            mMaximumZoomLevel = getJSONFloat(this.tileJSON, "maxzoom");
            mName = this.tileJSON.optString("name");
            mDescription = this.tileJSON.optString("description");
            mAttribution = this.tileJSON.optString("attribution");
            mLegend = this.tileJSON.optString("legend");

            double[] center = getJSONDoubleArray(this.tileJSON, "center", 3);
            if (center != null) {
                mCenter = new GeoPoint(center[0], center[1], center[2]);
            }
            double[] bounds = getJSONDoubleArray(this.tileJSON, "bounds", 4);
            if (bounds != null) {
                mBoundingBox = new BoundingBoxE6(bounds[3], bounds[2], bounds[1], bounds[0]);
            }
        }
    }

    public Configuration getConfiguration() {
        return mConfiguration;
    }

    public JSONObject getTileJSON() {
        return tileJSON;
    }

    public void setTileJSON(JSONObject aTileJSON) {
        this.tileJSON = aTileJSON;
    }

    private int getJSONFloat(JSONObject JSON, String key) {
        int defaultValue = 0;
        if (JSON.has(key)) {
            try {
                return JSON.getInt(key);
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
                    mTileLayerListener.onPrepared(CedarMapsTileSourceInfo.this);
                }
            }
        }.execute(url);
    }

    private String getBrandedJSONURL() {
        String url = String.format(Locale.ENGLISH, mConfiguration.getAPIBaseURL()
                + "tiles/%s.json?access_token=%s", mConfiguration.getMapId(), CedarMapsUtils.getAccessToken());

        return url;
    }

    public String getMapId() {
        return mConfiguration.getMapId();
    }

    public String getAccessToken() {
        return CedarMapsUtils.getAccessToken();
    }

    public int getTitleSize() {
        return mTileSize;
    }

    public String getImageExtension() {
        return mTileExtension;
    }

    class RetrieveJSONTask extends AsyncTask<String, Void, JSONObject> {

        protected JSONObject doInBackground(String... urls) {
            InputStream in = null;
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = NetworkUtils.getHttpURLConnection(url);
                connection.connect();
                if (connection.getResponseCode() == 401) {
                    CedarMapsUtils.setAccessToken(null);
                    return null;
                }
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


    public String getCacheKey() {
        return mId;
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
                Log.e(getClass().getSimpleName(), "token:" + oAuth2Token.getAccessToken());
            } catch (CedarMapsException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}