package com.cedarstudios.cedarmapssdk.utils;

import android.text.TextUtils;

public class CedarMapsUtils {

    private static String accessToken = null;

    public static String getAccessToken() {
        if (TextUtils.isEmpty(accessToken)) {
            return null;
        }
        return accessToken;
    }

    public static void setAccessToken(String accessToken) {
        CedarMapsUtils.accessToken = accessToken;
    }

}