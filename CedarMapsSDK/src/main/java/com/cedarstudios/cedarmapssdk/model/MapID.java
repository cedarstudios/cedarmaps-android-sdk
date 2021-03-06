package com.cedarstudios.cedarmapssdk.model;

import androidx.annotation.NonNull;

public enum MapID {
    STREETS {
        @NonNull
        @Override
        public String toString() {
            return "cedarmaps.streets";
        }
    },

    PLACES {
        @NonNull
        @Override
        public String toString() {
            return "cedarmaps.places";
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
