package com.cedarstudios.cedarmapssdk.model;

import android.support.annotation.NonNull;

public enum MapID {
    STREETS {
        @NonNull
        @Override
        public String toString() {
            return "cedarmaps.streets";
        }
    },

    MIX {
        @NonNull
        @Override
        public String toString() {
            return "cedarmaps.mix";
        }
    };

    @NonNull
    public abstract String toString();
}
