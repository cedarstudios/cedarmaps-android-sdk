package com.cedarstudios.cedarmapssdk.model.geocoder.reverse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Reader;
import java.io.Serializable;

/**
 * This class is used for serializing the Reverse Geocode response using Gson.
 */
public class ReverseGeocodeResponse implements Serializable {

    @SerializedName("result")
    @Expose
    public ReverseGeocode result = null;

    @SerializedName("status")
    @Expose
    public String status = null;

    public static ReverseGeocodeResponse parseJSON(Reader response) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(response, ReverseGeocodeResponse.class);
    }

}
