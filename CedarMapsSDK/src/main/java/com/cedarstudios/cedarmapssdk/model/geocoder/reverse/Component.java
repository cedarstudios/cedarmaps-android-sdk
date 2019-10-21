package com.cedarstudios.cedarmapssdk.model.geocoder.reverse;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Detailed components of a Reverse Geocode request
 */
public class Component implements Serializable
{
    @SerializedName("long_name")
    @Expose
    private String longName;
    @SerializedName("short_name")
    @Expose
    private String shortName;
    @SerializedName("type")
    @Expose
    private String type;

    /**
     *
     * @return Long name of a component
     */
    @Nullable
    public String getLongName() {
        return longName;
    }

    /**
     *
     * @return Short name of a component
     */
    @Nullable
    public String getShortName() {
        return shortName;
    }

    /**
     *
     * @return Type of a component
     */
    @Nullable
    public String getType() {
        return type;
    }
}
