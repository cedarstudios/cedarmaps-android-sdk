package com.cedarstudios.cedarmapssdk.model.geocoder.reverse;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * The result of Reverse Geocode request
 */
public class ReverseGeocode implements Serializable {

    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("components")
    @Expose
    private List<Component> components = null;
    @SerializedName("locality")
    @Expose
    private String locality;
    @SerializedName("district")
    @Expose
    private String district;
    @SerializedName("place")
    @Expose
    private String place;
    @SerializedName("province")
    @Expose
    private String province;
    @SerializedName("traffic_zone")
    @Expose
    private TrafficZone trafficZone;

    /**
     *
     * @return Address of result.
     */
    @Nullable
    public String getAddress() {
        return address;
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
     * @return Address components.
     */
    @Nullable
    public List<Component> getComponents() {
        return components;
    }

    /**
     *
     * @return Locality name
     */
    @Nullable
    public String getLocality() {
        return locality;
    }

    /**
     *
     * @return District name
     */
    @Nullable
    public String getDistrict() {
        return district;
    }

    /**
     * Place name
     * @return
     */
    @Nullable
    public String getPlace() {
        return place;
    }

    /**
     *
     * @return Province name
     */
    @Nullable
    public String getProvince() {
        return province;
    }

    /**
     *
     * @return Traffic zone info.
     */
    @Nullable
    public TrafficZone getTrafficZone() {
        return trafficZone;
    }
}
