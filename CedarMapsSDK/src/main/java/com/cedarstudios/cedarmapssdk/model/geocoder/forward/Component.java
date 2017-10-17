
package com.cedarstudios.cedarmapssdk.model.geocoder.forward;

import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Detailed components of a Forward Geocode request
 */
public class Component implements Serializable
{

    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("province")
    @Expose
    private String province;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("districts")
    @Expose
    private List<String> districts = null;
    @SerializedName("localities")
    @Expose
    private List<String> localities = null;

    /**
     *
     * @return Country name.
     */
    @Nullable
    public String getCountry() {
        return country;
    }

    /**
     *
     * @return Province name.
     */
    @Nullable
    public String getProvince() {
        return province;
    }

    /**
     *
     * @return City name.
     */
    @Nullable
    public String getCity() {
        return city;
    }

    /**
     *
     * @return List of municipality districts of the result.
     * e.g District 1, District 2, etc.
     */
    @Nullable
    public List<String> getDistricts() {
        return districts;
    }

    /**
     *
     * @return List of municipality locality names of the result.
     * May be empty or null.
     */
    @Nullable
    public List<String> getLocalities() {
        return localities;
    }
}
