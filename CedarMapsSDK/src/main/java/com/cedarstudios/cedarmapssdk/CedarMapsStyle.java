package com.cedarstudios.cedarmapssdk;

import androidx.annotation.NonNull;

public enum CedarMapsStyle {
    VECTOR_LIGHT {
        @NonNull
        @Override
        public String urlString() {
            return baseUrl + "styles/cedarmaps.light.json";
        }
    },

    VECTOR_DARK {
        @NonNull
        @Override
        public String urlString() {
            return baseUrl + "styles/cedarmaps.dark.json";
        }
    },

    RASTER_LIGHT {
        @NonNull
        @Override
        public String urlString() {
            return baseUrl + "tiles/light.json";
        }
    };

    public abstract String urlString();
    private static final String baseUrl = AuthenticationManager.getInstance().getAPIBaseURL();
}
