package com.cedarstudios.cedarmapssdk.model;

import android.support.annotation.NonNull;

public enum DirectionID {
    DRIVING {
        @NonNull
        @Override
        public String toString() {
            return "cedarmaps.driving";
        }
    };

    @NonNull
    public abstract String toString();
}
