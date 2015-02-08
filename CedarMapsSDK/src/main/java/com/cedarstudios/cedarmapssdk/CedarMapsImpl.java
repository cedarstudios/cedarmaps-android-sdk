package com.cedarstudios.cedarmapssdk;

import com.cedarstudios.cedarmapssdk.auth.Authorization;
import com.cedarstudios.cedarmapssdk.auth.NullAuthorization;
import com.cedarstudios.cedarmapssdk.auth.OAuth2Authorization;
import com.cedarstudios.cedarmapssdk.config.Configuration;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;


class CedarMapsImpl extends CedarMapsBaseImpl implements CedarMaps {

    CedarMapsImpl(Configuration conf, Authorization auth) {
        super(conf, auth);
    }

    @Override
    public JSONObject geocode(String searchTerm) throws CedarMapsException {
        return geocode(searchTerm, null);
    }

    @Override
    public JSONObject geocode(String searchTerm, String city) throws CedarMapsException {
        return geocode(searchTerm, city, Double.NaN, Double.NaN);
    }

    @Override
    public JSONObject geocode(String searchTerm, String city, double lat, double lng)
            throws CedarMapsException {
        return geocode(searchTerm, city, lat, lng, -1);
    }

    @Override
    public JSONObject geocode(String searchTerm, String city, double lat, double lng,
            long distance) throws CedarMapsException {
        return geocode(searchTerm, city, lat, lng, distance, 30);
    }

    @Override
    public JSONObject geocode(String searchTerm, String city, double lat, double lng,
            long distance, int limit) throws CedarMapsException {
        String term;

        if (TextUtils.isEmpty(conf.getMapId())) {
            throw new CedarMapsException(new NullPointerException(
                    "mapId is null. please provide a mapId in configuration"));
        }

        try {
            term = URLEncoder.encode(searchTerm, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new CedarMapsException(e);
        }
        String url = String
                .format(Locale.ENGLISH, conf.getRestBaseURL() + "geocode/%s/%s.json",
                        conf.getMapId(), term);

        url += String.format(Locale.ENGLISH, "?limit=%s", limit);

        if (!TextUtils.isEmpty(city)) {
            url += String.format(Locale.ENGLISH, "&city=%s", city);
        }
        if (!Double.valueOf(lat).isNaN() && !Double.valueOf(lng).isNaN()) {
            url += String.format(Locale.ENGLISH, "&location=%1$s,%2$s", lat, lng);
        }
        if (distance != -1) {
            url += String.format(Locale.ENGLISH, "&distance=%s", distance);
        }

        try {
            return new JSONObject(getDataFromAPI(url));
        } catch (JSONException e) {
            throw new CedarMapsException(e);
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Override
    public JSONObject geocode(double lat, double lng) throws CedarMapsException {
        String url = String.format(Locale.ENGLISH,
                conf.getRestBaseURL() + "geocode/%1$s/%2$s,%3$s.json", conf.getMapId(), lat, lng);

        if (TextUtils.isEmpty(conf.getMapId())) {
            throw new CedarMapsException(new NullPointerException(
                    "mapId is null. please provide a mapId in configuration"));
        }

        try {
            return new JSONObject(getDataFromAPI(url));
        } catch (JSONException e) {
            throw new CedarMapsException(e);
        }
    }

    private String getDataFromAPI(String url) throws CedarMapsException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + conf.getOAuth2AccessToken())
                .build();

        Response response;
        String responseString;
        try {
            response = client.newCall(request).execute();
            responseString = response.body().string();
        } catch (Exception e) {
            throw new CedarMapsException(e);
        }

        if (response.code() == 400) {
            throw new CedarMapsException("Invalid Request. missing parameter.", response);
        } else if (response.code() == 401) {
            throw new CedarMapsException("OAuth2 Bearer Token failed.", response);
        } else if (response.code() == 500) {
            throw new CedarMapsException("Internal Error", response);
        }
        return responseString;
    }


    @Override
    public void setOAuthClient(String clientId, String clientSecret) {
        if (null == clientId) {
            throw new NullPointerException("client key is null");
        }
        if (null == clientSecret) {
            throw new NullPointerException("client secret is null");
        }
        if (auth instanceof NullAuthorization) {
            OAuth2Authorization oauth2 = new OAuth2Authorization(conf);
            oauth2.setOAuthClient(clientId, clientSecret);
            auth = oauth2;
        } else if (auth instanceof OAuth2Authorization) {
            throw new IllegalStateException("client id/secret pair already set.");
        }
    }
}
