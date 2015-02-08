package com.cedarstudios.cedarmapssdk;

import com.cedarstudios.cedarmapssdk.auth.OAuth2Support;

import org.json.JSONObject;

public interface CedarMaps extends OAuth2Support, CedarMapsBase {

    JSONObject geocode(String searchTerm) throws CedarMapsException;

    JSONObject geocode(String searchTerm, String city) throws CedarMapsException;

    JSONObject geocode(String searchTerm, String city, double lat, double lng)
            throws CedarMapsException;

    JSONObject geocode(String searchTerm, String city, double lat, double lng, long distance)
            throws CedarMapsException;

    JSONObject geocode(String searchTerm, String city, double lat, double lng, long distance,
            int limit) throws CedarMapsException;

    JSONObject geocode(double lat, double lng) throws CedarMapsException;

}
