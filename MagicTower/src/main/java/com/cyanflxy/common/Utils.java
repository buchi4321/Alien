package com.cyanflxy.common;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;

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

    /**
     * InputMethodManager持有的mCurRootView会持有Activity，导致泄漏
     *
     * @param context
     */
    public static void fixInputMethodManagerLeak(Context context) {
        if (context == null) {
            return;
        }

        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm == null) {
                return;
            }

            Field f_mCurRootView = imm.getClass().getDeclaredField("mCurRootView");
            Field f_mServedView = imm.getClass().getDeclaredField("mServedView");
            Field f_mNextServedView = imm.getClass().getDeclaredField("mNextServedView");

            f_mCurRootView.setAccessible(true);
            f_mCurRootView.set(imm, null);

            f_mServedView.setAccessible(true);
            f_mServedView.set(imm, null);

            f_mNextServedView.setAccessible(true);
            f_mNextServedView.set(imm, null);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
