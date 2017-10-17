package com.cedarstudios.cedarmapssdk;

import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 * A wrapper class for the width and height of the required image.
 */
public class Dimension {

    private int width;
    private int height;

    public Dimension(int width, int height) {
        this.width = width;
        this.height = height;
    }

    int getWidthUsingDp(boolean usingDp) {
        if (usingDp) {
            return pixelToDp(width);
        } else {
            return width;
        }
    }

    int getHeightUsingDp(boolean usingDp) {
        if (usingDp) {
            return pixelToDp(height);
        } else {
            return height;
        }
    }

    public String toString() {
        return toStringUsingDp(false);
    }

    String toStringUsingDp(boolean usingDp) {
        return String.format(Locale.ENGLISH, "%dx%d", getWidthUsingDp(usingDp), getHeightUsingDp(usingDp));
    }

    int pixelToDp(int pixel) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        return Math.round(pixel / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    int dpToPixel(int dp) {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
