package com.cedarstudios.cedarmapssdk;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.cedarstudios.cedarmapssdk.listeners.AccessTokenListener;
import com.cedarstudios.cedarmapssdk.listeners.ForwardGeocodeResultsListener;
import com.cedarstudios.cedarmapssdk.listeners.GeoRoutingResultListener;
import com.cedarstudios.cedarmapssdk.listeners.OnTilesConfigured;
import com.cedarstudios.cedarmapssdk.listeners.ReverseGeocodeResultListener;
import com.cedarstudios.cedarmapssdk.listeners.StaticMapImageResultListener;
import com.cedarstudios.cedarmapssdk.model.geocoder.forward.ForwardGeocodeResponse;
import com.cedarstudios.cedarmapssdk.model.geocoder.reverse.ReverseGeocodeResponse;
import com.cedarstudios.cedarmapssdk.model.routing.GeoRoutingResponse;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CedarMaps {

    //region Constants
    private static final String defaultMapID = "cedarmaps.streets";
    private static final String defaultDirectionID = "cedarmaps.driving";
    //endregion

    //region Properties
    private String mMapID;
    private String mDirectionID;
    private static CedarMaps instance;
    //endregion

    //region Initializers
    public static CedarMaps getInstance() {
        if (instance == null) {
            instance = new CedarMaps();
        }
        return instance;
    }

    private CedarMaps() {
        mMapID = defaultMapID;
        mDirectionID = defaultDirectionID;
    }

    /**
     * Preparing Tiles for using in a mapView.
     * Make sure to call setClientID , setClientSecret and setContext before calling this method.
     * @param completionHandler The handler to notify when preparing tiles was finished with success or error.
     */
    public void prepareTiles(OnTilesConfigured completionHandler) {
        TileConfigurator.prepare(completionHandler);
    }
    //endregion


    //region Setters & Getters
    String getMapID() {
        return mMapID;
    }
    void setMapID(String mapID) {
        this.mMapID = mapID;
    }

    /**
     * Setting your clientID for using CedarMaps API.
     * This method should be called during setup before using any of the CedarMaps methods.
     * @param clientID The client ID you received for using CedarMaps SDK.
     * @return AuthenticationManager singleton object; You could use this to continue setting the other parameters such as clientSecret and context.
     */
    public AuthenticationManager setClientID(@NonNull String clientID) {
        AuthenticationManager.getInstance().setClientID(clientID);
        return AuthenticationManager.getInstance();
    }

    /**
     * Setting your clientSecret for using CedarMaps API.
     * This method should be called during setup before using any of the CedarMaps methods.
     * @param clientSecret The client secret you received for using CedarMaps SDK.
     * @return AuthenticationManager singleton object; You could use this to continue setting the other parameters such as context.
     */
    public AuthenticationManager setClientSecret(@NonNull String clientSecret) {
        AuthenticationManager.getInstance().setClientSecret(clientSecret);
        return AuthenticationManager.getInstance();
    }

    /**
     * Setting the context for using CedarMaps API.
     * This method should be called during setup before using any of the CedarMaps methods.
     * @param context You can pass your MainActivity as the context.
     *                We will use applicationContext extracted from what you pass.
     *                This needs to be set only once in the lifetime of your application.
     * @return AuthenticationManager singleton object.
     */
    public AuthenticationManager setContext(@NonNull Context context) {
        AuthenticationManager.getInstance().setContext(context);
        return AuthenticationManager.getInstance();
    }

    /**
     * Setting the baseURL for using CedarMaps API.
     * If you are given a different baseURL for using CedarMaps API, set it here.
     * This method should be called during setup before using any of the CedarMaps methods.
     * @param url If you pass null, the SDK uses the default baseURL.
     * @return AuthenticationManager singleton object.
     */
    public AuthenticationManager setAPIBaseURL(@Nullable String url) {
        AuthenticationManager.getInstance().setAPIBaseURL(url);
        return AuthenticationManager.getInstance();
    }
    //endregion


    /**
     * Forward Geocoding. You can use this method to obtain address info about the entered query.
     * This method works asynchronously and returns immediately.
     *
     * @param searchTerm Wherever you want to get info about
     * @param completionHandler The handler to notify when Forward Geocode results are ready with success or error.
     *                          The handler is called on UIThread.
     */
    public void forwardGeocode(@NonNull String searchTerm, @NonNull ForwardGeocodeResultsListener completionHandler) {
        forwardGeocode(searchTerm, null, completionHandler);
    }

    /**
     * Forward Geocoding. You can use this method to obtain address info about the entered query.
     * This method works asynchronously and returns immediately.
     *
     * @param searchTerm Wherever you want to get info about
     * @param type       Possible values are: locality, roundabout, street, freeway, expressway, boulevard (You can mix types by separating them with ",")
     * @param completionHandler The handler to notify when Forward Geocode results are ready with success or error.
     *                          The handler methods are called on UIThread.
     */
    public void forwardGeocode(@NonNull String searchTerm, String type, @NonNull ForwardGeocodeResultsListener completionHandler) {
        forwardGeocode(searchTerm, type, 30, completionHandler);
    }

    /**
     * Forward Geocoding. You can use this method to obtain address info about the entered query.
     * This method works asynchronously and returns immediately.
     *
     * @param searchTerm Wherever you want to get info about
     * @param type       Possible values are: locality, roundabout, street, freeway, expressway, boulevard (You can mix types by separating them with ",")
     * @param limit      Number of results
     * @param completionHandler The handler to notify when Forward Geocode results are ready with success or error.
     *                          The handler methods are called on UIThread.
     */
    public void forwardGeocode(String searchTerm, String type, int limit, @NonNull ForwardGeocodeResultsListener completionHandler) {
        forwardGeocode(searchTerm, null, -1, null, type, limit, completionHandler);
    }

    /**
     * Forward Geocoding. You can use this method to obtain address info about the entered query.
     * This method works asynchronously and returns immediately.
     *
     * @param searchTerm Wherever you want to get info about
     * @param location   Center point. should be accompanied with distance param
     * @param distance   GeoRouting from location. Unit is km, 0.1 means 100 meters
     * @param completionHandler The handler to notify when Forward Geocode results are ready with success or error.
     *                          The handler methods are called on UIThread.
     */
    public void forwardGeocode(String searchTerm, LatLng location, float distance, @NonNull ForwardGeocodeResultsListener completionHandler) {
        forwardGeocode(searchTerm, location, distance, null, null, 30, completionHandler);
    }

    /**
     * Forward Geocoding. You can use this method to obtain address info about the entered query.
     * This method works asynchronously and returns immediately.
     *
     * @param searchTerm Wherever you want to get info about
     * @param location   Center point. should be accompanied with distance param
     * @param distance   GeoRouting from location. Unit is km, 0.1 means 100 meters
     * @param type       Possible values are: locality, roundabout, street, freeway, expressway, boulevard (You can mix types by separating them with ",")
     * @param completionHandler The handler to notify when Forward Geocode results are ready with success or error.
     *                          The handler methods are called on UIThread.
     */
    public void forwardGeocode(String searchTerm, LatLng location, float distance, String type, @NonNull ForwardGeocodeResultsListener completionHandler) {
        forwardGeocode(searchTerm, location, distance, null, type, 30, completionHandler);
    }

    /**
     * Forward Geocoding. You can use this method to obtain address info about the entered query.
     * This method works asynchronously and returns immediately.
     *
     * @param searchTerm Wherever you want to get info about
     * @param location   Center point. should be accompanied with distance param
     * @param distance   GeoRouting from location. Unit is km, 0.1 means 100 meters
     * @param type       Possible values are: locality, roundabout, street, freeway, expressway, boulevard (You can mix types by separating them with ",")
     * @param limit      Number of results
     * @param completionHandler The handler to notify when Forward Geocode results are ready with success or error.
     *                          The handler methods are called on UIThread.
     */
    public void forwardGeocode(String searchTerm, LatLng location, float distance, String type, int limit, @NonNull ForwardGeocodeResultsListener completionHandler) {
        forwardGeocode(searchTerm, location, distance, null, type, limit, completionHandler);
    }

    /**
     * Forward Geocoding. You can use this method to obtain address info about the entered query.
     * This method works asynchronously and returns immediately.
     *
     * @param searchTerm Wherever you want to get info about
     * @param location   Center point. should be accompanied with distance param
     * @param distance   GeoRouting from location. Unit is km, 0.1 means 100 meters
     * @param bounds     Specifies the bounding box to search inside.
     * @param type       Possible values are: locality, roundabout, street, freeway, expressway, boulevard (You can mix types by separating them with ",")
     * @param limit      Number of results
     * @param completionHandler The handler to notify when Forward Geocode results are ready with success or error.
     *                          The handler methods are called on UIThread.
     */
    public void forwardGeocode(String searchTerm, LatLng location, float distance, LatLngBounds bounds, String type, int limit, @NonNull final ForwardGeocodeResultsListener completionHandler) {
        String term;

        try {
            term = URLEncoder.encode(searchTerm, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            completionHandler.onFailure(e.getMessage());
            return;
        }
        String url = String.format(Locale.ENGLISH,
                AuthenticationManager.getInstance().getAPIBaseURL() + "geocode/%s/%s.json",
                mMapID,
                term);

        url += String.format(Locale.ENGLISH, "?limit=%s", limit);

        if (location != null) {
            url += String.format(Locale.ENGLISH, "&location=%1$s,%2$s&distance=%3$s", location.getLatitude(), location.getLongitude(), distance);
        }

        if (bounds != null) {
            url += String.format(Locale.ENGLISH, "&ne=%1$s,%2$s", bounds.getNorthEast().getLatitude(), bounds.getNorthEast().getLongitude());
            url += String.format(Locale.ENGLISH, "&sw=%1$s,%2$s", bounds.getSouthWest().getLatitude(), bounds.getSouthWest().getLongitude());
        }

        if (!TextUtils.isEmpty(type)) {
            url += String.format(Locale.ENGLISH, "&type=%s", type);
        }

        getResponseBodyFromURL(url, new NetworkResponseBodyCompletionHandler() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                ForwardGeocodeResponse forwardGeocodeResponse = ForwardGeocodeResponse.parseJSON(responseBody.charStream());
                if (forwardGeocodeResponse.results == null) {
                    completionHandler.onFailure(forwardGeocodeResponse.status);
                } else {
                    completionHandler.onSuccess(forwardGeocodeResponse.results);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                completionHandler.onFailure(errorMessage);
            }
        });
    }

    /**
     * Gives an address based on a provided coordinate.
     * This method works asynchronously and returns immediately.
     *
     * @param coordinate The coordinate to get the address from.
     * @param completionHandler The handler to notify when Reverse Geocode results are ready with success or error.
     *                          The handler methods are called on UIThread.
     */
    public void reverseGeocode(LatLng coordinate, final ReverseGeocodeResultListener completionHandler) {
        String url = String.format(Locale.ENGLISH,
                AuthenticationManager.getInstance().getAPIBaseURL() + "geocode/%1$s/%2$s,%3$s.json",
                mMapID,
                coordinate.getLatitude(), coordinate.getLongitude());

        getResponseBodyFromURL(url, new NetworkResponseBodyCompletionHandler() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                ReverseGeocodeResponse reverseGeocodeResponse = ReverseGeocodeResponse.parseJSON(responseBody.charStream());
                if (reverseGeocodeResponse.result == null) {
                    completionHandler.onFailure(reverseGeocodeResponse.status);
                } else {
                    completionHandler.onSuccess(reverseGeocodeResponse.result);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                completionHandler.onFailure(errorMessage);
            }
        });
    }

    /**
     * This method calculates the distance between two points in meters.
     *
     * @param start Starting coordinate
     * @param end Ending coordinate
     * @param completionHandler The handler to notify when Geo Routing results are ready with success or error.
     *                          The handler methods are called on UIThread.
     */
    public void distance(LatLng start, LatLng end, final GeoRoutingResultListener completionHandler) {

        String url = String.format(Locale.ENGLISH,
                AuthenticationManager.getInstance().getAPIBaseURL() + "distance/%1$s/%2$s,%3$s;%4$s,%5$s",
                mDirectionID,
                start.getLatitude(), start.getLongitude(), end.getLatitude(), end.getLongitude());

        getResponseBodyFromURL(url, new NetworkResponseBodyCompletionHandler() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                GeoRoutingResponse geoRoutingResponse = GeoRoutingResponse.parseJSON(responseBody.charStream());
                if (geoRoutingResponse.result == null) {
                    completionHandler.onFailure(geoRoutingResponse.status);
                } else {
                    completionHandler.onSuccess(geoRoutingResponse.result);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                completionHandler.onFailure(errorMessage);
            }
        });
    }

    /**
     * This method calculates the distance between points in meters. It can be called with up to 15 pairs
     * This API call needs a valid access token.
     *
     * @param coordinatePairs Set up to 15 pairs of (Start, End).
     * @param completionHandler The handler to notify when Geo Routing results are ready with success or error.
     *                          The handler methods are called on UIThread.
     */
    public void distance(Pair<LatLng, LatLng>[] coordinatePairs, final GeoRoutingResultListener completionHandler) {

        String pairs = "";
        String delimiter = "";
        for (Pair<LatLng, LatLng> locationPair : coordinatePairs) {
            pairs += delimiter + String.format(Locale.ENGLISH, "%1$s,%2$s;%3$s,%4$s", locationPair.first.getLatitude(),
                    locationPair.first.getLongitude(), locationPair.second.getLatitude(), locationPair.second.getLatitude());
            delimiter = "/";
        }

        String url = String.format(Locale.ENGLISH,
                AuthenticationManager.getInstance().getAPIBaseURL() + "distance/%1$s/%2$s",
                mDirectionID, pairs);

        getResponseBodyFromURL(url, new NetworkResponseBodyCompletionHandler() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                GeoRoutingResponse geoRoutingResponse = GeoRoutingResponse.parseJSON(responseBody.charStream());
                if (geoRoutingResponse.result == null) {
                    completionHandler.onFailure(geoRoutingResponse.status);
                } else {
                    completionHandler.onSuccess(geoRoutingResponse.result);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                completionHandler.onFailure(errorMessage);
            }
        });
    }

    /**
     * This method calculates the detailed coordinates of the route between two points.
     *
     * @param start Starting coordinate
     * @param end Ending coordinate
     * @param completionHandler The handler to notify when Geo Routing results are ready with success or error.
     *                          The handler methods are called on UIThread.
     */
    public void direction(LatLng start, LatLng end, final GeoRoutingResultListener completionHandler) {

        String url = String.format(Locale.ENGLISH,
                AuthenticationManager.getInstance().getAPIBaseURL() + "direction/%1$s/%2$s,%3$s;%4$s,%5$s",
                mDirectionID,
                start.getLatitude(), start.getLongitude(), end.getLatitude(), end.getLongitude());

        getResponseBodyFromURL(url, new NetworkResponseBodyCompletionHandler() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                GeoRoutingResponse geoRoutingResponse = GeoRoutingResponse.parseJSON(responseBody.charStream());
                if (geoRoutingResponse.result == null) {
                    completionHandler.onFailure(geoRoutingResponse.status);
                } else {
                    completionHandler.onSuccess(geoRoutingResponse.result);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                completionHandler.onFailure(errorMessage);
            }
        });
    }

    /**
     * This method calculates the detailed coordinates of the route between two consecutive points. It can be called with up to 50 pairs
     *
     * @param coordinatePairs Set up to 50 pairs of (Start, End).
     * @param completionHandler The handler to notify when Geo Routing results are ready with success or error.
     *                          The handler methods are called on UIThread.
     */
    public void direction(Pair<LatLng, LatLng>[] coordinatePairs, final GeoRoutingResultListener completionHandler) {

        String pairs = "";
        String delimiter = "";
        for (Pair<LatLng, LatLng> locationPair : coordinatePairs) {
            pairs += delimiter + String.format(Locale.ENGLISH, "%1$s,%2$s;%3$s,%4$s", locationPair.first.getLatitude(),
                    locationPair.first.getLongitude(), locationPair.second.getLatitude(), locationPair.second.getLatitude());
            delimiter = "/";
        }

        String url = String.format(Locale.ENGLISH,
                AuthenticationManager.getInstance().getAPIBaseURL() + "direction/%1$s/%2$s",
                mDirectionID, pairs);

        getResponseBodyFromURL(url, new NetworkResponseBodyCompletionHandler() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                GeoRoutingResponse geoRoutingResponse = GeoRoutingResponse.parseJSON(responseBody.charStream());
                if (geoRoutingResponse.result == null) {
                    completionHandler.onFailure(geoRoutingResponse.status);
                } else {
                    completionHandler.onSuccess(geoRoutingResponse.result);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                completionHandler.onFailure(errorMessage);
            }
        });
    }


    /**
     * This method creates a static image of map for the entered location.
     *
     * @param dimension The wrapper for the width and height of the required image. The width and height should be specified in pixels. (not dp)
     * @param zoomLevel An integer for the required zoom level. Valid from 6 to 17.
     * @param centerPoint The center of the map in the image. If you pass null, make sure to fill the markers array. The boundary will be automatically set to show all the markers.
     * @param markers An array of StaticMarker objects. The markers will be drawn on the resulting image.
     * @param completionHandler The handler to notify when static image Bitmap is ready with success or error.
     *                          The handler methods are called on UIThread.
     */
    public void staticMap(@NonNull Dimension dimension, int zoomLevel, @Nullable LatLng centerPoint, @Nullable ArrayList<StaticMarker> markers, final @NonNull StaticMapImageResultListener completionHandler) {
        int validZoomLevel = Math.min(17, Math.max(zoomLevel, 6));
        String paramPosition = centerPoint != null ? String.format(Locale.ENGLISH, "%f,%f,%d", centerPoint.getLatitude(), centerPoint.getLongitude(), validZoomLevel) : "auto";

        String paramDimension = dimension.toStringUsingDp(true);

        String paramScale = "";
        if (Resources.getSystem().getDisplayMetrics().densityDpi > DisplayMetrics.DENSITY_MEDIUM) {
            paramScale = "@2x";
        }

        String paramMarkers = "";
        if (markers != null && markers.size() > 0) {
            paramMarkers = "?markers=";
            for (StaticMarker marker: markers) {
                String item = String.format(Locale.ENGLISH, "%s|%f,%f|",
                        marker.markerUri == null ? "marker-default" : marker.markerUri.toString(),
                        marker.coordinate.getLatitude(),
                        marker.coordinate.getLongitude());
                paramMarkers += item;
            }
            paramMarkers = paramMarkers.substring(0, paramMarkers.length() - 1);
        }

        String url = String.format(Locale.ENGLISH,
                AuthenticationManager.getInstance().getAPIBaseURL() + "static/light/%s/%s%s%s", paramPosition, paramDimension, paramScale, paramMarkers);

        getResponseBodyFromURL(url, new NetworkResponseBodyCompletionHandler() {
            @Override
            public void onSuccess(final ResponseBody responseBody) {
                final Handler mainHandler = new Handler(Looper.getMainLooper());

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        final Bitmap bitmap = BitmapFactory.decodeStream(responseBody.byteStream());
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (bitmap == null) {
                                    completionHandler.onFailure("Bitmap decoding failed");
                                } else {
                                    completionHandler.onSuccess(bitmap);
                                }
                            }
                        });
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                completionHandler.onFailure(errorMessage);
            }
        });
    }

    /**
     * The wrapper class whose instances are used for showing markers on the static map.
     * @see #staticMap(Dimension, int, LatLng, ArrayList, StaticMapImageResultListener)
     */
    public static class StaticMarker {
        @NonNull
        private LatLng coordinate;

        @Nullable
        private Uri markerUri;

        /**
         *
         * @param coordinate The coordinate of the marker
         * @param markerUri The remote address of the image you want to use for the marker
         */
        public StaticMarker(@NonNull LatLng coordinate, @Nullable Uri markerUri) {
            this.coordinate = coordinate;
            this.markerUri = markerUri;
        }
    }

    private interface NetworkResponseBodyCompletionHandler {
        void onSuccess(ResponseBody responseBody);
        void onFailure(String errorMessage);
    }

    private void getResponseBodyFromURL(final String url, final NetworkResponseBodyCompletionHandler completionHandler) {

        final Handler handler = new Handler(Looper.getMainLooper());

        AuthenticationManager.getInstance().getAccessToken(new AccessTokenListener() {
            @Override
            public void onSuccess(@NonNull String accessToken) {
                CedarOkHttpClient client = CedarOkHttpClient.getInstance();
                Request request = new Request.Builder()
                        .url(url)
                        .tag(url)
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (response.code() == 400) {
                                    completionHandler.onFailure(new CedarMapsException("Invalid Request. Missing Parameters.", response).getMessage());
                                } else if (response.code() == 401) {
                                    AuthenticationManager.getInstance().regenerateAccessToken();
                                    completionHandler.onFailure(new CedarMapsException("Obtaining Bearer Token Failed.", response).getMessage());
                                } else if (response.code() == 500) {
                                    completionHandler.onFailure(new CedarMapsException("Internal Server Error.", response).getMessage());
                                } else {
                                    ResponseBody body = response.body();
                                    if (body == null) {
                                        completionHandler.onFailure(new CedarMapsException("Response body can't be parsed.", response).getMessage());
                                    } else {
                                        completionHandler.onSuccess(body);
                                    }
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(final Call call, final IOException e) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                completionHandler.onFailure(new CedarMapsException(call.toString(), e).getMessage());
                            }
                        });
                    }
                });
            }

            @Override
            public void onFailure(final @NonNull String error) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        completionHandler.onFailure(error);
                    }
                });
            }
        });
    }
}
