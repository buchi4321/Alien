package com.cyanflxy.game.data;

import android.content.Context;
import android.content.SharedPreferences;

import static com.github.cyanflxy.magictower.AppApplication.baseContext;

public class GameSharedPref {

    private static final SharedPreferences sp = baseContext.getSharedPreferences("data", Context.MODE_PRIVATE);

    private static final String NEW_RECORD_ID = "new_record_id";

    public static int getNewRecordId() {
        return sp.getInt(NEW_RECORD_ID, 1);
    }

    public static void addNewRecordId() {
        int id = getNewRecordId();
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt(NEW_RECORD_ID, id + 1);
        ed.apply();
    }

    public static void setMaxRecordId(int maxId) {
        int saveId = getNewRecordId();
        if (saveId > maxId) {
            return;
        }

        SharedPreferences.Editor ed = sp.edit();
        ed.putInt(NEW_RECORD_ID, maxId);
        ed.apply();
    }

}
