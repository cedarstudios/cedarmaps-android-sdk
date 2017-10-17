
package com.cedarstudios.cedarmapssdk.model.geocoder.forward;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * The result of Forward Geocode request
 */
public class ForwardGeocode implements Serializable
{

    private static final Map<String,String> streetTypes = new HashMap<String,String>() {{
        put("freeway", "آزادراه");
        put("expressway", "بزرگراه");
        put("road", "جاده");
        put("boulevard", "بلوار");
        put("roundabout", "میدان");
        put("intersection", "تقاطع");
        put("street", "خیابان");
        put("city", "شهر");
        put("locality", "محله");
        put("poi", "مکان");
        put("residential", "کوچه");
        put("footway", "پیاده‌رو");
        put("path", "پیاده‌رو");
        put("primary", "خیابان اصلی");
        put("secondary", "خیابان فرعی");
        put("trunk", "بزرگراه");
        put("trunk_link", "رمپ");
        put("service", "کنارگذر");
    }};

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("alternate_name")
    @Expose
    private String alternateName;

    @SerializedName("full_name")
    @Expose
    private String fullName;

    @SerializedName("name_en")
    @Expose
    private String nameEn;

    @SerializedName("type")
    @Expose
    private String type;

    /**
     *
     * @return Persian equivalent of field Type.
     */
    @Nullable
    public String getPersianType() {
        if (type == null || type.isEmpty()) {
            return null;
        }
        return streetTypes.get(type);
    }

    @SerializedName("location")
    @Expose
    private Location location;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("components")
    @Expose
    private Component components;

    /**
     *
     * @return Identifier for the result.
     */
    @NonNull
    public Integer getId() {
        return id;
    }

    /**
     *
     * @return Name of the result.
     */
    @NonNull
    public String getName() {
        return name;
    }

    /**
     *
     * @return Alternate name of the result. e.g. Niayesh, Hashemi Rafsanjani
     */
    @Nullable
    public String getAlternateName() {
        return alternateName;
    }

    /**
     *
     * @return Full name of the result. e.g. Hakim, Ayatollah Hakim
     */
    @Nullable
    public String getFullName() {
        return fullName;
    }

    /**
     *
     * @return English name of the result
     */
    @Nullable
    public String getEnglishName() {
        return nameEn;
    }

    /**
     *
     * @return Type of the result. e.g. street, locality, place, etc.
     */
    @NonNull
    public String getType() {
        return type;
    }

    /**
     *
     * @return Geometric location of the result.
     */
    @NonNull
    public Location getLocation() {
        return location;
    }

    /**
     *
     * @return A simple generated address from components field.
     * @see #getComponents()
     */
    @Nullable
    public String getAddress() {
        return address;
    }

    /**
     *
     * @return Detailed address components for the result.
     */
    @NonNull
    public Component getComponents() {
        return components;
    }
}
