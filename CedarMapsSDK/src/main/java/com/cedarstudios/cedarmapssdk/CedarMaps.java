package com.cedarstudios.cedarmapssdk;


import android.util.Pair;

import com.cedarstudios.cedarmapssdk.auth.OAuth2Support;

import org.json.JSONObject;
import org.osmdroid.api.IGeoPoint;

public interface CedarMaps extends OAuth2Support, CedarMapsBase {

    /**
     * Forward Geocoding. This API call needs a valid access token.
     *
     * @param searchTerm Wherever you want to get info about
     * @return Results as JSONObject
     * @throws CedarMapsException
     */
    JSONObject geocode(String searchTerm) throws CedarMapsException;

    /**
     * Forward Geocoding. This API call needs a valid access token.
     *
     * @param searchTerm Wherever you want to get info about
     * @param type       Possible values are: locality, roundabout, street, freeway, expressway, boulevard (You can mix types by separating them with ",")
     * @return Results as JSONObject
     * @throws CedarMapsException
     */
    JSONObject geocode(String searchTerm, String type) throws CedarMapsException;

    /**
     * Forward Geocoding. This API call needs a valid access token.
     *
     * @param searchTerm Wherever you want to get info about
     * @param type       Possible values are: locality, roundabout, street, freeway, expressway, boulevard (You can mix types by separating them with ",")
     * @param limit      Number of results
     * @return Results as JSONObject
     * @throws CedarMapsException
     */
    JSONObject geocode(String searchTerm, String type, int limit) throws CedarMapsException;

    /**
     * Forward Geocoding. This API call needs a valid access token.
     *
     * @param searchTerm Wherever you want to get info about
     * @param location   Center point. should be accompanied with distance param
     * @param distance   Distance from location. Unit is km, 0.1 means 100 meters
     * @return Results as JSONObject
     * @throws CedarMapsException
     */
    JSONObject geocode(String searchTerm, IGeoPoint location, float distance) throws CedarMapsException;

    /**
     * Forward Geocoding. This API call needs a valid access token.
     *
     * @param searchTerm Wherever you want to get info about
     * @param location   Center point. should be accompanied with distance param
     * @param distance   Distance from location. Unit is km, 0.1 means 100 meters
     * @param type       Possible values are: locality, roundabout, street, freeway, expressway, boulevard (You can mix types by separating them with ",")
     * @return Results as JSONObject
     * @throws CedarMapsException
     */
    JSONObject geocode(String searchTerm, IGeoPoint location, float distance, String type) throws CedarMapsException;

    /**
     * Forward Geocoding. This API call needs a valid access token.
     *
     * @param searchTerm Wherever you want to get info about
     * @param location   Center point. should be accompanied with distance param
     * @param distance   Distance from location. Unit is km, 0.1 means 100 meters
     * @param type       Possible values are: locality, roundabout, street, freeway, expressway, boulevard (You can mix types by separating them with ",")
     * @param limit      Number of results
     * @return Results as JSONObject
     * @throws CedarMapsException
     */
    JSONObject geocode(String searchTerm, IGeoPoint location, float distance, String type, int limit) throws CedarMapsException;


    /**
     * Forward Geocoding. This API call needs a valid access token.
     *
     * @param searchTerm Wherever you want to get info about
     * @param location   Center point. should be accompanied with distance param
     * @param distance   Distance from location. Unit is km, 0.1 means 100 meters
     * @param ne         Specifies north east of the bounding box - should be accompanied with sw param
     * @param sw         Specifies south west of the bounding box - should be accompanied with ne param
     * @param type       Possible values are: locality, roundabout, street, freeway, expressway, boulevard (You can mix types by separating them with ",")
     * @param limit      Number of results
     * @return Results as JSONObject
     * @throws CedarMapsException
     */
    JSONObject geocode(String searchTerm, IGeoPoint location, float distance, IGeoPoint ne, IGeoPoint sw, String type, int limit) throws CedarMapsException;

    /**
     * Gives an address based on a provided IGeoPoint pair. This API call needs a valid access token.
     *
     * @param lat
     * @param lng
     * @return Results as JSONObject
     * @throws CedarMapsException
     */
    JSONObject geocode(double lat, double lng) throws CedarMapsException;

    /**
     * This method calculates the distance between points in meters. It can be called with up to 50 different points in a single request.
     * This API call needs a valid access token.
     *
     * @param location1 Starting point
     * @param location2 Ending point
     * @return Results as JSONObject
     * @throws CedarMapsException
     */
    JSONObject distance(IGeoPoint location1, IGeoPoint location2) throws CedarMapsException;

    /**
     * This method calculates the distance between points in meters. It can be called with up to 15 pairs
     * This API call needs a valid access token.
     *
     * @param locationPairs Set up to 15 pairs
     * @return Results as JSONObject
     * @throws CedarMapsException
     */
    JSONObject distance(Pair<IGeoPoint, IGeoPoint>[] locationPairs) throws CedarMapsException;

    /**
     * Gives all localities in a city wih geometry in GeoJSON format.
     * @param city City name in English or Persian
     * @return Results as JSONObject
     * @throws CedarMapsException
     */
    JSONObject locality(String city) throws CedarMapsException;


}
