package com.cedarstudios.cedarmapssdk.model.routing;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class Instruction implements Serializable {

    public static final class Sign {
        public static final int TURN_SHARP_LEFT = -3;
        public static final int TURN_LEFT = -2;
        public static final int TURN_SLIGHT_LEFT = -1;
        public static final int CONTINUE = 0;
        public static final int TURN_SLIGHT_RIGHT = 1;
        public static final int TURN_RIGHT = 2;
        public static final int TURN_SHARP_RIGHT = 3;
        public static final int FINISH = 4;
        public static final int REACHABLE_VIA = 5;
        public static final int USE_ROUNDABOUT = 6;
        public static final int KEEP_RIGHT = 7;
    }

    @SerializedName("distance")
    @Expose
    private Double distance;

    @SerializedName("sign")
    @Expose
    private int sign;

    @SerializedName("interval")
    @Expose
    private List<Integer> interval;

    @SerializedName("text")
    @Expose
    private String text;

    @SerializedName("time")
    @Expose
    private Integer time;

    @SerializedName("street_name")
    @Expose
    private String streetName;


    /**
     *
     * @return Distance of route section in the instruction in meters
     */
    @Nullable
    public Double getDistance() {
        return distance;
    }

    /**
     *
     * @return Traffic sign of route section in the instruction. Use Sign class for constants.
     */
    public int getSign() {
        return sign;
    }

    /**
     *
     * @return An array of indices; these indices can be looked up in Route `geometry` property. It shows locations included in this section.
     */
    public List<Integer> getInterval() {
        return interval;
    }

    /**
     *
     * @return Textual instruction of route section.
     */
    @Nullable
    public String getText() {
        return text;
    }

    /**
     *
     * @return Time duration of route section in the instruction in seconds
     */
    @Nullable
    public Integer getTime() {
        return time;
    }

    /**
     *
     * @return Main street name in the route section.
     */
    @Nullable
    public String getStreetName() {
        return streetName;
    }
}
