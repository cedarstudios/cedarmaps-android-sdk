package com.cedarstudios.cedarmapssdk;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.cedarstudios.cedarmapssdk.listeners.ReverseGeocodeResultListener;
import com.cedarstudios.cedarmapssdk.listeners.StaticMapImageResultListener;
import com.cedarstudios.cedarmapssdk.model.DirectionID;
import com.cedarstudios.cedarmapssdk.model.MapID;
import com.cedarstudios.cedarmapssdk.model.StaticMarker;
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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

@SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
public final class CedarMaps {

    //region Properties
    private MapID mapID;
    private DirectionID directionID;
    private static CedarMaps instance;
    private AuthenticationManager authManager = AuthenticationManager.getInstance();
    //endregion

    //region Constructor

    @NonNull
    public static CedarMaps getInstance() {
        if (instance == null) {
            instance = new CedarMaps();
        }
        return instance;
    }

    private CedarMaps() {
        mapID = MapID.STREETS;
        directionID = DirectionID.DRIVING;
    }
    //endregion


    //region Setters & Getters

    /**
     * @return MapID of current configuration. Possible values are "MapID.STREETS" and "MapID.MIX".
     */
    @NonNull
    public MapID getMapID() {
        return mapID;
    }

    /**
     * This method specifies the result types when using Geocoding APIs. Possible values are "MapID.STREETS" and "MapID.MIX"
     *
     * @param mapID The map ID
     * @return CedarMaps singleton object
     */
    @NonNull
    public CedarMaps setMapID(@NonNull MapID mapID) {
        this.mapID = mapID;
        return CedarMaps.getInstance();
    }

    /**
     * Setting your clientID for using CedarMaps API.
     * This method should be called during setup before using any of the CedarMaps methods.
     *
     * @param clientID The client ID you received for using CedarMaps SDK.
     * @return CedarMaps singleton object; You could use this to continue setting the other parameters such as clientSecret and context.
     */
    @NonNull
    public CedarMaps setClientID(@NonNull String clientID) {
        authManager.setClientID(clientID);
        return CedarMaps.getInstance();
    }

    /**
     * Setting your clientSecret for using CedarMaps API.
     * This method should be called during setup before using any of the CedarMaps methods.
     *
     * @param clientSecret The client secret you received for using CedarMaps SDK.
     * @return CedarMaps singleton object; You could use this to continue setting the other parameters such as context.
     */
    @NonNull
    public CedarMaps setClientSecret(@NonNull String clientSecret) {
        authManager.setClientSecret(clientSecret);
        return CedarMaps.getInstance();
    }

    /**
     * Setting the context for using CedarMaps API.
     * This method should be called during setup before using any of the CedarMaps methods.
     *
     * @param context You can pass your MainActivity as the context.
     *                We will use applicationContext extracted from what you pass.
     *                This needs to be set only once in the lifetime of your application.
     * @return CedarMaps singleton object.
     */
    @NonNull
    public CedarMaps setContext(@NonNull Context context) {
        authManager.setContext(context);
        return CedarMaps.getInstance();
    }

    /**
     * Setting the baseURL for using CedarMaps API.
     * If you are given a different baseURL for using CedarMaps API, set it here.
     * This method should be called during setup before using any of the CedarMaps methods.
     *
     * @param url If you pass null, the SDK uses the default baseURL.
     * @return AuthenticationManager singleton object.
     */
    @NonNull
    public CedarMaps setAPIBaseURL(@Nullable String url) {
        authManager.setAPIBaseURL(url);
        return CedarMaps.getInstance();
    }
    //endregion

    @Nullable
    public String getSavedAccessToken() throws Exception {
        return authManager.getSavedAccessToken();
    }

    /**
     * Forward Geocoding. You can use this method to obtain address info about the entered query.
     * This method works asynchronously and returns immediately.
     *
     * @param searchTerm        Wherever you want to get info about
     * @param completionHandler The handler to notify when Forward Geocode results are ready with success or error.
     */
    public void forwardGeocode(@NonNull String searchTerm, @NonNull final ForwardGeocodeResultsListener completionHandler) {
        forwardGeocode(searchTerm, null, null, null, null, 30, completionHandler);
    }

    /**
     * Forward Geocoding. You can use this method to obtain address info about the entered query.
     * This method works asynchronously and returns immediately.
     *
     * @param searchTerm        Wherever you want to get info about
     * @param limit             Number of results
     * @param completionHandler The handler to notify when Forward Geocode results are ready with success or error.
     */
    public void forwardGeocode(@NonNull String searchTerm, int limit, @NonNull final ForwardGeocodeResultsListener completionHandler) {
        forwardGeocode(searchTerm, null, null, null, null, limit, completionHandler);
    }


    /**
     * Forward Geocoding. You can use this method to obtain address info about the entered query.
     * This method works asynchronously and returns immediately.
     *
     * @param searchTerm        Wherever you want to get info about
     * @param type              Possible values are: locality, roundabout, street, freeway, expressway, boulevard (You can mix types by separating them with ",")
     * @param completionHandler The handler to notify when Forward Geocode results are ready with success or error.
     */
    public void forwardGeocode(@NonNull String searchTerm, @NonNull String type, @NonNull final ForwardGeocodeResultsListener completionHandler) {
        forwardGeocode(searchTerm, null, null, null, type, 30, completionHandler);
    }

    /**
     * Forward Geocoding. You can use this method to obtain address info about the entered query.
     * This method works asynchronously and returns immediately.
     *
     * @param searchTerm        Wherever you want to get info about
     * @param type              Possible values are: locality, roundabout, street, freeway, expressway, boulevard (You can mix types by separating them with ",")
     * @param limit             Number of results
     * @param completionHandler The handler to notify when Forward Geocode results are ready with success or error.
     */
    public void forwardGeocode(@NonNull String searchTerm, @NonNull String type, int limit, @NonNull final ForwardGeocodeResultsListener completionHandler) {
        forwardGeocode(searchTerm, null, null, null, type, limit, completionHandler);
    }

    /**
     * Forward Geocoding. You can use this method to obtain address info about the entered query.
     * This method works asynchronously and returns immediately.
     *
     * @param searchTerm        Wherever you want to get info about
     * @param centerAndRadius   Center point and accompanying Radius distance. Radius unit is km, 0.1 means 100 meters. This is a necessity for the system.
     *                          If this is set to non null, proximity will be ignored.
     * @param completionHandler The handler to notify when Forward Geocode results are ready with success or error.
     */
    public void forwardGeocode(@NonNull String searchTerm, @NonNull Pair<LatLng, Float> centerAndRadius, @NonNull final ForwardGeocodeResultsListener completionHandler) {
        forwardGeocode(searchTerm, centerAndRadius, null, null, null, 30, completionHandler);
    }

    /**
     * Forward Geocoding. You can use this method to obtain address info about the entered query.
     * This method works asynchronously and returns immediately.
     *
     * @param searchTerm        Wherever you want to get info about
     * @param centerAndRadius   Center point and accompanying Radius distance. Radius unit is km, 0.1 means 100 meters. This is a necessity for the system.
     * @param type              Possible values are: locality, roundabout, street, freeway, expressway, boulevard (You can mix types by separating them with ",")
     * @param completionHandler The handler to notify when Forward Geocode results are ready with success or error.
     */
    public void forwardGeocode(@NonNull String searchTerm, @NonNull Pair<LatLng, Float> centerAndRadius, @NonNull String type, @NonNull final ForwardGeocodeResultsListener completionHandler) {
        forwardGeocode(searchTerm, centerAndRadius, null, null, type, 30, completionHandler);
    }

    /**
     * Forward Geocoding. You can use this method to obtain address info about the entered query.
     * This method works asynchronously and returns immediately.
     *
     * @param searchTerm        Wherever you want to get info about
     * @param centerAndRadius   Center point and accompanying Radius distance. Radius unit is km, 0.1 means 100 meters. This is a necessity for the system.
     *                          If this is set to non null, proximity will be ignored.
     * @param limit             Number of results
     * @param completionHandler The handler to notify when Forward Geocode results are ready with success or error.
     */
    public void forwardGeocode(@NonNull String searchTerm, @NonNull Pair<LatLng, Float> centerAndRadius, int limit, @NonNull final ForwardGeocodeResultsListener completionHandler) {
        forwardGeocode(searchTerm, centerAndRadius, null, null, null, limit, completionHandler);
    }

    /**
     * Forward Geocoding. You can use this method to obtain address info about the entered query.
     * This method works asynchronously and returns immediately.
     *
     * @param searchTerm        Wherever you want to get info about
     * @param centerAndRadius   Center point and accompanying Radius distance. Radius unit is km, 0.1 means 100 meters. This is a necessity for the system.
     *                          If this is set to non null, proximity will be ignored.
     * @param type              Possible values are: locality, roundabout, street, freeway, expressway, boulevard (You can mix types by separating them with ",")
     * @param limit             Number of results
     * @param completionHandler The handler to notify when Forward Geocode results are ready with success or error.
     */
    public void forwardGeocode(@NonNull String searchTerm, @NonNull Pair<LatLng, Float> centerAndRadius, @NonNull String type, int limit, @NonNull final ForwardGeocodeResultsListener completionHandler) {
        forwardGeocode(searchTerm, centerAndRadius, null, null, type, limit, completionHandler);
    }

    /**
     * Forward Geocoding. You can use this method to obtain address info about the entered query.
     * This method works asynchronously and returns immediately.
     *
     * @param searchTerm        Wherever you want to get info about
     * @param proximity         Center point around which search is performed. This is a hint for the system.
     * @param completionHandler The handler to notify when Forward Geocode results are ready with success or error.
     */
    public void forwardGeocode(@NonNull String searchTerm, @NonNull LatLng proximity, @NonNull final ForwardGeocodeResultsListener completionHandler) {
        forwardGeocode(searchTerm, null, null, proximity, null, 30, completionHandler);
    }

    /**
     * Forward Geocoding. You can use this method to obtain address info about the entered query.
     * This method works asynchronously and returns immediately.
     *
     * @param searchTerm        Wherever you want to get info about
     * @param proximity         Center point around which search is performed. This is a hint for the system.
     * @param limit             Number of results
     * @param completionHandler The handler to notify when Forward Geocode results are ready with success or error.
     */
    public void forwardGeocode(@NonNull String searchTerm, @NonNull LatLng proximity, int limit, @NonNull final ForwardGeocodeResultsListener completionHandler) {
        forwardGeocode(searchTerm, null, null, proximity, null, limit, completionHandler);
    }

    /**
     * Forward Geocoding. You can use this method to obtain address info about the entered query.
     * This method works asynchronously and returns immediately.
     *
     * @param searchTerm        Wherever you want to get info about
     * @param proximity         Center point around which search is performed. This is a hint for the system.
     * @param type              Possible values are: locality, roundabout, street, freeway, expressway, boulevard (You can mix types by separating them with ",")
     * @param completionHandler The handler to notify when Forward Geocode results are ready with success or error.
     */
    public void forwardGeocode(@NonNull String searchTerm, @NonNull LatLng proximity, @NonNull String type, @NonNull final ForwardGeocodeResultsListener completionHandler) {
        forwardGeocode(searchTerm, null, null, proximity, type, 30, completionHandler);
    }

    /**
     * Forward Geocoding. You can use this method to obtain address info about the entered query.
     * This method works asynchronously and returns immediately.
     *
     * @param searchTerm        Wherever you want to get info about
     * @param proximity         Center point around which search is performed. This is a hint for the system.
     * @param type              Possible values are: locality, roundabout, street, freeway, expressway, boulevard (You can mix types by separating them with ",")
     * @param limit             Number of results
     * @param completionHandler The handler to notify when Forward Geocode results are ready with success or error.
     */
    public void forwardGeocode(@NonNull String searchTerm, @NonNull LatLng proximity, @NonNull String type, int limit, @NonNull final ForwardGeocodeResultsListener completionHandler) {
        forwardGeocode(searchTerm, null, null, proximity, type, limit, completionHandler);
    }

    /**
     * Forward Geocoding. You can use this method to obtain address info about the entered query.
     * This method works asynchronously and returns immediately.
     *
     * @param searchTerm        Wherever you want to get info about
     * @param bounds            Specifies the bounding box to search inside.
     *                          If this is set to non null, proximity will be ignored.
     * @param completionHandler The handler to notify when Forward Geocode results are ready with success or error.
     */
    public void forwardGeocode(@NonNull String searchTerm, @NonNull LatLngBounds bounds, @NonNull final ForwardGeocodeResultsListener completionHandler) {
        forwardGeocode(searchTerm, null, bounds, null, null, 30, completionHandler);
    }

    /**
     * Forward Geocoding. You can use this method to obtain address info about the entered query.
     * This method works asynchronously and returns immediately.
     *
     * @param searchTerm        Wherever you want to get info about
     * @param bounds            Specifies the bounding box to search inside.
     *                          If this is set to non null, proximity will be ignored.
     * @param type              Possible values are: locality, roundabout, street, freeway, expressway, boulevard (You can mix types by separating them with ",")
     * @param completionHandler The handler to notify when Forward Geocode results are ready with success or error.
     */
    public void forwardGeocode(@NonNull String searchTerm, @NonNull LatLngBounds bounds, @NonNull String type, @NonNull final ForwardGeocodeResultsListener completionHandler) {
        forwardGeocode(searchTerm, null, bounds, null, type, 30, completionHandler);
    }

    /**
     * Forward Geocoding. You can use this method to obtain address info about the entered query.
     * This method works asynchronously and returns immediately.
     *
     * @param searchTerm        Wherever you want to get info about
     * @param bounds            Specifies the bounding box to search inside.
     *                          If this is set to non null, proximity will be ignored.
     * @param limit             Number of results
     * @param completionHandler The handler to notify when Forward Geocode results are ready with success or error.
     */
    public void forwardGeocode(@NonNull String searchTerm, @NonNull LatLngBounds bounds, int limit, @NonNull final ForwardGeocodeResultsListener completionHandler) {
        forwardGeocode(searchTerm, null, bounds, null, null, limit, completionHandler);
    }

    /**
     * Forward Geocoding. You can use this method to obtain address info about the entered query.
     * This method works asynchronously and returns immediately.
     *
     * @param searchTerm        Wherever you want to get info about
     * @param bounds            Specifies the bounding box to search inside.
     *                          If this is set to non null, proximity will be ignored.
     * @param type              Possible values are: locality, roundabout, street, freeway, expressway, boulevard (You can mix types by separating them with ",")
     * @param limit             Number of results
     * @param completionHandler The handler to notify when Forward Geocode results are ready with success or error.
     */
    public void forwardGeocode(@NonNull String searchTerm, @NonNull LatLngBounds bounds, @NonNull String type, int limit, @NonNull final ForwardGeocodeResultsListener completionHandler) {
        forwardGeocode(searchTerm, null, bounds, null, type, limit, completionHandler);
    }

    /**
     * Forward Geocoding. You can use this method to obtain address info about the entered query.
     * This method works asynchronously and returns immediately.
     *
     * @param searchTerm        Wherever you want to get info about
     * @param centerAndRadius   Center point and accompanying Radius distance. Radius unit is km, 0.1 means 100 meters. This is a necessity for the system.
     *                          If this is set to non null, proximity will be ignored.
     * @param bounds            Specifies the bounding box to search inside.
     *                          If this is set to non null, proximity will be ignored.
     * @param proximity         Center point around which search is performed. This is a hint for the system.
     * @param type              Possible values are: locality, roundabout, street, freeway, expressway, boulevard (You can mix types by separating them with ",")
     * @param limit             Number of results
     * @param completionHandler The handler to notify when Forward Geocode results are ready with success or error.
     *                          The handler methods are called on UIThread.
     */
    public void forwardGeocode(@NonNull String searchTerm, @Nullable Pair<LatLng, Float> centerAndRadius, @Nullable LatLngBounds bounds, @Nullable LatLng proximity, @Nullable String type, int limit, @NonNull final ForwardGeocodeResultsListener completionHandler) {
        String term;

        try {
            term = URLEncoder.encode(searchTerm, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            completionHandler.onFailure(e.getMessage());
            return;
        }
        String url = String.format(Locale.ENGLISH,
                authManager.getAPIBaseURL() + "geocode/%s/%s.json",
                mapID.toString(),
                term);

        int normalizedLimit = limit > 0 ? limit : 30;
        url += String.format(Locale.ENGLISH, "?limit=%s", normalizedLimit);

        if (centerAndRadius != null && centerAndRadius.first != null && centerAndRadius.second != null) {
            url += String.format(Locale.ENGLISH, "&location=%1$s,%2$s&distance=%3$s",
                    centerAndRadius.first.getLatitude(),
                    centerAndRadius.first.getLongitude(),
                    centerAndRadius.second);
        }

        if (bounds != null) {
            url += String.format(Locale.ENGLISH, "&ne=%1$s,%2$s", bounds.getNorthEast().getLatitude(), bounds.getNorthEast().getLongitude());
            url += String.format(Locale.ENGLISH, "&sw=%1$s,%2$s", bounds.getSouthWest().getLatitude(), bounds.getSouthWest().getLongitude());
        }

        if (proximity != null && bounds == null && centerAndRadius == null) {
            url += String.format(Locale.ENGLISH, "&proximity=%1$s,%2$s", proximity.getLatitude(), proximity.getLongitude());
        }

        if (!TextUtils.isEmpty(type)) {
            url += String.format(Locale.ENGLISH, "&type=%s", type);
        }

        getResponseBodyFromURL(url, new NetworkResponseBodyCompletionHandler() {
            @Override
            public void onSuccess(final ResponseBody responseBody) {
                runOnBackgroundThread(new Runnable() {
                    @Override
                    public void run() {
                        final ForwardGeocodeResponse forwardGeocodeResponse = ForwardGeocodeResponse.parseJSON(responseBody.charStream());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (forwardGeocodeResponse.results == null) {
                                    completionHandler.onFailure(forwardGeocodeResponse.status);
                                } else {
                                    completionHandler.onSuccess(forwardGeocodeResponse.results);
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
     * Gives an address based on a provided coordinate.
     * This method works asynchronously and returns immediately.
     *
     * @param coordinate        The coordinate to get the address from.
     * @param completionHandler The handler to notify when Reverse Geocode results are ready with success or error.
     *                          The handler methods are called on UIThread.
     */
    public void reverseGeocode(LatLng coordinate, final ReverseGeocodeResultListener completionHandler) {
        String url = String.format(Locale.ENGLISH,
                authManager.getAPIBaseURL() + "geocode/%1$s/%2$s,%3$s.json",
                mapID.toString(),
                coordinate.getLatitude(), coordinate.getLongitude());

        getResponseBodyFromURL(url, new NetworkResponseBodyCompletionHandler() {
            @Override
            public void onSuccess(final ResponseBody responseBody) {
                runOnBackgroundThread(new Runnable() {
                    @Override
                    public void run() {
                        final ReverseGeocodeResponse reverseGeocodeResponse = ReverseGeocodeResponse.parseJSON(responseBody.charStream());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (reverseGeocodeResponse.result == null) {
                                    completionHandler.onFailure(reverseGeocodeResponse.status);
                                } else {
                                    completionHandler.onSuccess(reverseGeocodeResponse.result);
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
     * This method calculates the distance between two points in meters.
     *
     * @param start             Starting coordinate
     * @param end               Ending coordinate
     * @param completionHandler The handler to notify when Geo Routing results are ready with success or error.
     *                          The handler methods are called on UIThread.
     */
    public void distance(LatLng start, LatLng end, final GeoRoutingResultListener completionHandler) {

        String url = String.format(Locale.ENGLISH,
                authManager.getAPIBaseURL() + "distance/%1$s/%2$s,%3$s;%4$s,%5$s",
                directionID.toString(),
                start.getLatitude(), start.getLongitude(), end.getLatitude(), end.getLongitude());

        getResponseBodyFromURL(url, new NetworkResponseBodyCompletionHandler() {
            @Override
            public void onSuccess(final ResponseBody responseBody) {
                runOnBackgroundThread(new Runnable() {
                    @Override
                    public void run() {
                        final GeoRoutingResponse geoRoutingResponse = GeoRoutingResponse.parseJSON(responseBody.charStream());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (geoRoutingResponse.result == null) {
                                    completionHandler.onFailure(geoRoutingResponse.status);
                                } else {
                                    completionHandler.onSuccess(geoRoutingResponse.result);
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
     * This method calculates the distance between points in meters. It can be called with up to 15 pairs
     * This API call needs a valid access token.
     *
     * @param coordinatePairs   Set up to 15 pairs of (Start, End).
     * @param completionHandler The handler to notify when Geo Routing results are ready with success or error.
     *                          The handler methods are called on UIThread.
     */
    public void distance(Pair<LatLng, LatLng>[] coordinatePairs, final GeoRoutingResultListener completionHandler) {

        StringBuilder pairs = new StringBuilder();
        String delimiter = "";
        for (Pair<LatLng, LatLng> locationPair : coordinatePairs) {
            if (locationPair.first == null || locationPair.second == null) {
                continue;
            }
            pairs.append(delimiter).append(String.format(Locale.ENGLISH, "%1$s,%2$s;%3$s,%4$s", locationPair.first.getLatitude(),
                    locationPair.first.getLongitude(), locationPair.second.getLatitude(), locationPair.second.getLongitude()));
            delimiter = "/";
        }

        String url = String.format(Locale.ENGLISH,
                authManager.getAPIBaseURL() + "distance/%1$s/%2$s",
                directionID.toString(), pairs.toString());

        getResponseBodyFromURL(url, new NetworkResponseBodyCompletionHandler() {
            @Override
            public void onSuccess(final ResponseBody responseBody) {
                runOnBackgroundThread(new Runnable() {
                    @Override
                    public void run() {
                        final GeoRoutingResponse geoRoutingResponse = GeoRoutingResponse.parseJSON(responseBody.charStream());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (geoRoutingResponse.result == null) {
                                    completionHandler.onFailure(geoRoutingResponse.status);
                                } else {
                                    completionHandler.onSuccess(geoRoutingResponse.result);
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
     * This method calculates the detailed coordinates of the route between two points.
     *
     * @param start             Starting coordinate
     * @param end               Ending coordinate
     * @param completionHandler The handler to notify when Geo Routing results are ready with success or error.
     *                          The handler methods are called on UIThread.
     */
    public void direction(LatLng start, LatLng end, final GeoRoutingResultListener completionHandler) {

        ArrayList<Pair<LatLng, LatLng>> points = new ArrayList<>();
        points.add(new Pair<>(start, end));

        direction(points, false, new Locale("fa"), completionHandler);
    }

    /**
     * This method calculates the detailed coordinates of the route between two points with textual instructions.
     *
     * @param start             Starting coordinate
     * @param end               Ending coordinate
     * @param locale            Identifier for language. Currently Locale("fa") and Locale("en") are supported.
     * @param completionHandler The handler to notify when Geo Routing results are ready with success or error.
     *                          The handler methods are called on UIThread.
     */
    public void directionWithInstructions(LatLng start, LatLng end, Locale locale, final GeoRoutingResultListener completionHandler) {

        ArrayList<Pair<LatLng, LatLng>> points = new ArrayList<>();
        points.add(new Pair<>(start, end));

        direction(points, true, locale, completionHandler);
    }

    /**
     * This method calculates the detailed coordinates of the route between two consecutive points. It can be called with up to 50 pairs
     *
     * @param coordinatePairs   Set up to 50 pairs of (Start, End).
     * @param completionHandler The handler to notify when Geo Routing results are ready with success or error.
     *                          The handler methods are called on UIThread.
     */
    public void direction(ArrayList<Pair<LatLng, LatLng>> coordinatePairs, final GeoRoutingResultListener completionHandler) {
        direction(coordinatePairs, false, new Locale("fa"), completionHandler);
    }


    /**
     * This method calculates the detailed coordinates of the route between two consecutive points with textual instructions. It can be called with up to 50 pairs
     *
     * @param coordinatePairs   Set up to 50 pairs of (Start, End).
     * @param locale            Identifier for language. Currently Locale("fa") and Locale("en") are supported.
     * @param completionHandler The handler to notify when Geo Routing results are ready with success or error.
     *                          The handler methods are called on UIThread.
     */
    public void directionWithInstructions(ArrayList<Pair<LatLng, LatLng>> coordinatePairs, Locale locale, final GeoRoutingResultListener completionHandler) {
        direction(coordinatePairs, true, locale, completionHandler);
    }

    private void direction(ArrayList<Pair<LatLng, LatLng>> coordinatePairs, Boolean shouldShowInstructions, Locale locale, final GeoRoutingResultListener completionHandler) {

        StringBuilder pairs = new StringBuilder();
        String delimiter = "";
        for (Pair<LatLng, LatLng> locationPair : coordinatePairs) {
            if (locationPair.first == null || locationPair.second == null) {
                continue;
            }
            pairs.append(delimiter).append(String.format(Locale.ENGLISH, "%1$s,%2$s;%3$s,%4$s", locationPair.first.getLatitude(),
                    locationPair.first.getLongitude(), locationPair.second.getLatitude(), locationPair.second.getLongitude()));
            delimiter = "/";
        }

        String url = String.format(Locale.ENGLISH,
                authManager.getAPIBaseURL() + "direction/%1$s/%2$s?instructions=%3$s&locale=%4$s",
                directionID.toString(),
                pairs.toString(),
                shouldShowInstructions ? "true" : "false",
                locale.getLanguage().contains("fa") ? "fa" : "en"
        );

        getResponseBodyFromURL(url, new NetworkResponseBodyCompletionHandler() {
            @Override
            public void onSuccess(final ResponseBody responseBody) {
                runOnBackgroundThread(new Runnable() {
                    @Override
                    public void run() {
                        final GeoRoutingResponse geoRoutingResponse = GeoRoutingResponse.parseJSON(responseBody.charStream());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (geoRoutingResponse.result == null) {
                                    completionHandler.onFailure(geoRoutingResponse.status);
                                } else {
                                    completionHandler.onSuccess(geoRoutingResponse.result);
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
     * This method creates a static image of map for the entered location.
     *
     * @param width             Width of the required image in pixels. (not dp)
     * @param height            Height of the required image in pixels. (not dp)
     * @param zoomLevel         An integer for the required zoom level. Valid from 6 to 17.
     * @param centerPoint       The center of the map in the image. If you pass null, make sure to fill the markers array. The boundary will be automatically set to show all the markers.
     * @param markers           An array of StaticMarker objects. The markers will be drawn on the resulting image.
     * @param completionHandler The handler to notify when static image Bitmap is ready with success or error.
     *                          The handler methods are called on UIThread.
     */
    public void staticMap(int width, int height, int zoomLevel, @Nullable LatLng centerPoint, @Nullable ArrayList<StaticMarker> markers, final @NonNull StaticMapImageResultListener completionHandler) {
        int validZoomLevel = Math.min(17, Math.max(zoomLevel, 6));
        String paramPosition = centerPoint != null ? String.format(Locale.ENGLISH, "%f,%f,%d", centerPoint.getLatitude(), centerPoint.getLongitude(), validZoomLevel) : "auto";

        String paramDimension = SizeHelper.stringValueUsingDp(width, height);

        String paramScale = "";
        if (Resources.getSystem().getDisplayMetrics().densityDpi > DisplayMetrics.DENSITY_MEDIUM) {
            paramScale = "@2x";
        }

        StringBuilder paramMarkers = new StringBuilder();
        if (markers != null && markers.size() > 0) {
            paramMarkers = new StringBuilder("?markers=");
            for (StaticMarker marker : markers) {
                String item = String.format(Locale.ENGLISH, "%s|%f,%f|",
                        marker.getMarkerUri() == null ? "marker-default" : marker.getMarkerUri().toString(),
                        marker.getCoordinate().getLatitude(),
                        marker.getCoordinate().getLongitude());
                paramMarkers.append(item);
            }
            paramMarkers = new StringBuilder(paramMarkers.substring(0, paramMarkers.length() - 1));
        }

        String url = String.format(Locale.ENGLISH,
                authManager.getAPIBaseURL() + "static/light/%s/%s%s%s", paramPosition, paramDimension, paramScale, paramMarkers.toString());

        getResponseBodyFromURL(url, new NetworkResponseBodyCompletionHandler() {
            @Override
            public void onSuccess(final ResponseBody responseBody) {
                runOnBackgroundThread(new Runnable() {
                    @Override
                    public void run() {
                        final Bitmap bitmap = BitmapFactory.decodeStream(responseBody.byteStream());
                        runOnUiThread(new Runnable() {
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

    private interface NetworkResponseBodyCompletionHandler {
        void onSuccess(ResponseBody responseBody);

        void onFailure(String errorMessage);
    }

    private void getResponseBodyFromURL(final String url, final NetworkResponseBodyCompletionHandler completionHandler) {

        authManager.getAccessToken(new AccessTokenListener() {
            @Override
            public void onSuccess(@NonNull String accessToken) {
                OkHttpClient client = CedarOkHttpClient.getSharedInstance(authManager.getContext());
                Request request = new Request.Builder()
                        .url(url)
                        .tag(url)
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, final Response response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (response.code()) {
                                    case 400:
                                        completionHandler.onFailure(new Exception("Invalid Request. Missing Parameters.").getMessage());
                                        break;
                                    case 401:
                                        try {
                                            authManager.generateAccessToken();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        completionHandler.onFailure(new Exception("Obtaining Bearer Token Failed.").getMessage());
                                        break;
                                    case 500:
                                        completionHandler.onFailure(new Exception("Internal Server Error.").getMessage());
                                        break;
                                    default:
                                        ResponseBody body = response.body();
                                        if (body == null) {
                                            completionHandler.onFailure(new Exception("Response body can't be parsed.").getMessage());
                                        } else {
                                            completionHandler.onSuccess(body);
                                        }
                                        break;
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(final Call call, final IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                completionHandler.onFailure(new Exception(call.toString(), e).getMessage());
                            }
                        });
                    }
                });
            }

            @Override
            public void onFailure(final @NonNull String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        completionHandler.onFailure(error);
                    }
                });
            }
        });
    }

    private void runOnUiThread(Runnable task) {
        new Handler(Looper.getMainLooper()).post(task);
    }

    private void runOnBackgroundThread(Runnable task) {
        AsyncTask.execute(task);
    }
}
