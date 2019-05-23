package com.cedarstudios.cedarmapssdk;

import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

final class SizeHelper {
    static String stringValueUsingDp(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width or Height should me positive");
        }
        return String.format(Locale.ENGLISH, "%dx%d", pixelToDp(width), pixelToDp(height));
    }

    private static int pixelToDp(int pixel) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        return Math.round(pixel / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
