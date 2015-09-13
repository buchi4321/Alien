package com.cyanflxy.mapcreator.bean;

import android.content.Context;
import android.content.SharedPreferences;

import static com.cyanflxy.mapcreator.AppApplication.baseContext;

public class SharePref {
    private static final String DATA_SP = "data";
    private static final SharedPreferences dataSP = baseContext.getSharedPreferences(DATA_SP, Context.MODE_PRIVATE);

    private static final String CURRENT_FLOOR = "current_floor";

    public static void setCurrentFloor(int floor) {
        SharedPreferences.Editor editor = dataSP.edit();
        editor.putInt(CURRENT_FLOOR, floor);
        editor.apply();
    }

    public static int getCurrentFloor() {
        return dataSP.getInt(CURRENT_FLOOR, 0);
    }
}
