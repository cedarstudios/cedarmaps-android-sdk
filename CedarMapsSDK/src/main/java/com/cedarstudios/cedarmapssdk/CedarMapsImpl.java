package com.cedarstudios.cedarmapssdk;

import android.text.TextUtils;
import android.util.Pair;

import com.cedarstudios.cedarmapssdk.auth.Authorization;
import com.cedarstudios.cedarmapssdk.auth.NullAuthorization;
import com.cedarstudios.cedarmapssdk.auth.OAuth2Authorization;
import com.cedarstudios.cedarmapssdk.config.Configuration;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IGeoPoint;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


class CedarMapsImpl extends CedarMapsBaseImpl implements CedarMaps {

    CedarMapsImpl(Configuration conf, Authorization auth) {
        super(conf, auth);
    }

    @Override
    public JSONObject geocode(String searchTerm) throws CedarMapsException {
        return geocode(searchTerm, null);
    }

    @Override
    public JSONObject geocode(String searchTerm, String type) throws CedarMapsException {
        return geocode(searchTerm, type, 30);
    }

    @Override
    public JSONObject geocode(String searchTerm, String type, int limit) throws CedarMapsException {
        return geocode(searchTerm, null, -1, null, null, type, limit);
    }

    @Override
    public JSONObject geocode(String searchTerm, IGeoPoint location, float distance) throws CedarMapsException {
        return geocode(searchTerm, location, distance, null, null, null, 30);
    }

    @Override
    public JSONObject geocode(String searchTerm, IGeoPoint location, float distance, String type) throws CedarMapsException {
        return geocode(searchTerm, location, distance, null, null, type, 30);
    }

    @Override
    public JSONObject geocode(String searchTerm, IGeoPoint location, float distance, String type, int limit) throws CedarMapsException {
        return geocode(searchTerm, location, distance, null, null, type, limit);
    }

    @Override
    public JSONObject geocode(String searchTerm, IGeoPoint location, float distance, IGeoPoint ne, IGeoPoint sw, String type, int limit)
            throws CedarMapsException {
        String term;

        if (TextUtils.isEmpty(conf.getMapId())) {
            throw new CedarMapsException(new NullPointerException("mapId is null. please provide a mapId in configuration"));
        }

        try {
            term = URLEncoder.encode(searchTerm, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new CedarMapsException(e);
        }
        String url = String.format(Locale.ENGLISH, conf.getAPIBaseURL() + "geocode/%s/%s.json", conf.getMapId(), term);

        url += String.format(Locale.ENGLISH, "?limit=%s", limit);

        if (location != null) {
            url += String.format(Locale.ENGLISH, "&location=%1$s,%2$s&distance=%3$s", location.getLatitude(), location.getLongitude(), distance);
        }

        if (ne != null) {
            url += String.format(Locale.ENGLISH, "&ne=%1$s,%2$s", ne.getLatitude(), ne.getLongitude());
        }

        if (sw != null) {
            url += String.format(Locale.ENGLISH, "&sw=%1$s,%2$s", sw.getLatitude(), sw.getLongitude());
        }

        if (!TextUtils.isEmpty(type)) {
            url += String.format(Locale.ENGLISH, "&type=%s", type);
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
        if (TextUtils.isEmpty(conf.getMapId())) {
            throw new CedarMapsException(new NullPointerException("mapId is null. please provide a mapId in configuration"));
        }

        String url = String.format(Locale.ENGLISH, conf.getAPIBaseURL() + "geocode/%1$s/%2$s,%3$s.json", conf.getMapId(), lat, lng);

        try {
            return new JSONObject(getDataFromAPI(url));
        } catch (JSONException e) {
            throw new CedarMapsException(e);
        }
    }

    @Override
    public JSONObject distance(IGeoPoint location1, IGeoPoint location2) throws CedarMapsException {
        if (TextUtils.isEmpty(conf.getMapId())) {
            throw new CedarMapsException(new NullPointerException("mapId is null. please provide a mapId in configuration"));
        }

        String url = String.format(Locale.ENGLISH, conf.getAPIBaseURL() + "distance/%1$s/%2$s,%3$s;%4$s,%5$s", conf.getMapId(),
                location1.getLatitude(), location1.getLongitude(), location2.getLatitude(), location2.getLongitude());

        try {
            return new JSONObject(getDataFromAPI(url));
        } catch (JSONException e) {
            throw new CedarMapsException(e);
        }
    }

    @Override
    public JSONObject distance(Pair<IGeoPoint, IGeoPoint>[] locationPairs) throws CedarMapsException {
        if (TextUtils.isEmpty(conf.getMapId())) {
            throw new CedarMapsException(new NullPointerException("mapId is null. please provide a mapId in configuration"));
        }

        String pairs = "";
        String delimiter = "";
        for (Pair<IGeoPoint, IGeoPoint> locationPair : locationPairs) {
            pairs += delimiter + String.format(Locale.ENGLISH, "%1$s,%2$s;%3$s,%4$s", locationPair.first.getLatitude(),
                    locationPair.first.getLongitude(), locationPair.second.getLatitude(), locationPair.second.getLatitude());
            delimiter = "/";
        }

        String url = String.format(Locale.ENGLISH, conf.getAPIBaseURL() + "distance/%1$s/%2$s", conf.getMapId(), pairs);

        try {
            return new JSONObject(getDataFromAPI(url));
        } catch (JSONException e) {
            throw new CedarMapsException(e);
        }
    }

    @Override
    public JSONObject locality(String city) throws CedarMapsException {
        String url = String.format(Locale.ENGLISH, conf.getAPIBaseURL() + "locality/%s.json", city);

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
