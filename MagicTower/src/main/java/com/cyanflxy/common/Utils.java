package com.cyanflxy.common;

import static com.github.cyanflxy.magictower.AppApplication.baseContext;

public class Utils {

    public static int dip2px(float dpValue) {
        float scale = baseContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(float pxValue) {
        final float scale = baseContext.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int px2sp(float pxValue) {
        final float fontScale = baseContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(float spValue) {
        final float fontScale = baseContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static boolean isArrayEmpty(Object[] array) {
        if (array == null || array.length == 0) {
            return true;
        }

        for (Object o : array) {
            if (o != null) {
                return false;
            }
        }

        return true;
    }
}
