
package com.cedarstudios.cedarmapssdk.model.geocoder.forward;

import java.io.Reader;
import java.io.Serializable;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * This class is used for serializing the Forward Geocode response using Gson.
 */
public class ForwardGeocodeResponse implements Serializable
{
    @SerializedName("results")
    @Expose
    public List<ForwardGeocode> results = null;

    @SerializedName("status")
    @Expose
    public String status = null;

    public static ForwardGeocodeResponse parseJSON(Reader response) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(response, ForwardGeocodeResponse.class);
    }
}
