package com.cedarstudios.cedarmapssdk.model.routing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Reader;
import java.io.Serializable;

/**
 * This class is used for serializing the Routing response using Gson.
 */
public class GeoRoutingResponse implements Serializable {
    @SerializedName("result")
    @Expose
    public GeoRouting result = null;

    @SerializedName("status")
    @Expose
    public String status = null;

    public static GeoRoutingResponse parseJSON(Reader response) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(response, GeoRoutingResponse.class);
    }
}
