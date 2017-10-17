package com.cedarstudios.cedarmapssdk.model.geocoder.reverse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Traffic zone info for a Reverse Geocode response
 */
public class TrafficZone implements Serializable {

    @SerializedName("in_central")
    @Expose
    private Boolean isInCentral;

    @SerializedName("in_evenodd")
    @Expose
    private Boolean isInEvenOdd;

    @SerializedName("name")
    @Expose
    private String name;

    /**
     *
     * @return Shows if the location is in Central Traffic Zone.
     */
    public Boolean getInCentral() {
        return isInCentral;
    }

    /**
     *
     * @return Shows if the location is in Even/Odd Traffic Zone.
     */
    public Boolean getInEvenOdd() {
        return isInEvenOdd;
    }

    /**
     *
     * @return Name of Traffic Zone.
     */
    public String getName() {
        return name;
    }
}
