package com.zpj.widget.toolbar;

import android.graphics.Color;

public class ColorUtils {

    private static final float CONTRAST_LIGHT_ITEM_THRESHOLD = 1.5f;

    private ColorUtils() {

    }

    @Deprecated
    public static boolean isDarkColor(int color) {
        float a = getContrastForColor(color);
        return a >= CONTRAST_LIGHT_ITEM_THRESHOLD;
    }

    public static boolean isDarkenColor(int color) {
        return android.support.v4.graphics.ColorUtils.calculateLuminance(color) <= 0.5;
    }

    private static float getContrastForColor(int color) {
        float bgR = Color.red(color) / 255f;
        float bgG = Color.green(color) / 255f;
        float bgB = Color.blue(color) / 255f;
        bgR = (bgR < 0.03928f) ? bgR / 12.92f : (float) Math.pow((bgR + 0.055f) / 1.055f, 2.4f);
        bgG = (bgG < 0.03928f) ? bgG / 12.92f : (float) Math.pow((bgG + 0.055f) / 1.055f, 2.4f);
        bgB = (bgB < 0.03928f) ? bgB / 12.92f : (float) Math.pow((bgB + 0.055f) / 1.055f, 2.4f);
        float bgL = 0.2126f * bgR + 0.7152f * bgG + 0.0722f * bgB;
        return Math.abs((1.05f) / (bgL + 0.05f));
    }

}
