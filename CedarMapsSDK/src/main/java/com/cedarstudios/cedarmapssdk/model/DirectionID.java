package com.cedarstudios.cedarmapssdk.model;

import androidx.annotation.NonNull;

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
