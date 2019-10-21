package com.cedarstudios.cedarmapssdk.model.geocoder.reverse;

import androidx.annotation.NonNull;

public enum FormattedAddressPrefixLength {
    SHORT {
        @NonNull
        @Override
        public String toString() {
            return "short";
        }
    },

    LONG {
        @NonNull
        @Override
        public String toString() {
            return "long";
        }
    },

    NONE {
        @NonNull
        @Override
        public String toString() {
            return "none";
        }
    };

    public abstract @NonNull String toString();

}
