package com.cyanflxy.game.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;

import static com.github.cyanflxy.magictower.AppApplication.baseContext;

public class GameSharedPref {

    private static final SharedPreferences sp = baseContext.getSharedPreferences("data", Context.MODE_PRIVATE);

    // 记录id
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

    // 屏幕亮度
    private static final String SCREEN_LIGHT = "screen_light";

    public static int getScreenLight() {
        return sp.getInt(SCREEN_LIGHT, 90);
    }

    public static void setScreenLight(int light) {
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt(SCREEN_LIGHT, light);
        ed.apply();
    }

    // 游戏音量
    private static final String GAME_VOLUME = "game_volume";

    public static int getGameVolume() {
        return sp.getInt(GAME_VOLUME, 70);
    }

    public static void setGameVolume(int volume) {
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt(GAME_VOLUME, volume);
        ed.apply();
    }

    // 背景音乐
    private static final String BACKGROUND_MUSIC = "background_music";

    public static boolean isPlayBackgroundMusic() {
        return sp.getBoolean(BACKGROUND_MUSIC, true);
    }

    public static void setPlayBackgroundMusic(boolean play) {
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(BACKGROUND_MUSIC, play);
        ed.apply();
    }

    // 游戏音效
    private static final String GAME_SOUND = "game_sound";

    public static boolean isPlayGameSound() {
        return sp.getBoolean(GAME_SOUND, true);
    }

    public static void setPlayGameSound(boolean play) {
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(GAME_SOUND, play);
        ed.apply();
    }

    // 自动寻路
    private static final String AUTO_FIND_WAY = "auto_find_way";

    public static boolean isAutoFindWay() {
        return sp.getBoolean(AUTO_FIND_WAY, false);
    }

    public static void setAutoFindWay(boolean auto) {
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(AUTO_FIND_WAY, auto);
        ed.apply();
    }

    // 无视地图
    private static final String MAP_INVISIBLE = "map_invisible";

    public static boolean isMapInvisible() {
        return sp.getBoolean(MAP_INVISIBLE, false);
    }

    public static void setMapInvisible(boolean mapInvisible) {
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(MAP_INVISIBLE, mapInvisible);
        ed.apply();
    }

    // 商店快捷
    private static final String SHOP_SHORTCUT = "shop_shortcut";

    public static boolean isOpenShopShortcut() {
        return sp.getBoolean(SHOP_SHORTCUT, false);
    }

    public static void setOpenShopShortcut(boolean open) {
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(SHOP_SHORTCUT, open);
        ed.apply();
    }

    // 功能全开
    private static final String OPEN_ALL_FUNCTION = "open_all_function";

    public static boolean isOpenAllFunction() {
        return sp.getBoolean(OPEN_ALL_FUNCTION, false);
    }

    public static void setOpenAllFunction(boolean open) {
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(OPEN_ALL_FUNCTION, open);
        ed.apply();
    }

    // 显示打斗场面
    private static final String SHOW_FIGHT_VIEW = "show_fight_view";

    public static boolean isShowFightView() {
        return sp.getBoolean(SHOW_FIGHT_VIEW, true);
    }

    public static void setShowFightView(boolean show) {
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean(SHOW_FIGHT_VIEW, show);
        ed.apply();
    }

    private static final String SCREEN_ORIENTATION = "screen_orientation";

    public static int getScreenOrientation() {
        return sp.getInt(SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    public static void setScreenOrientation(int orientation) {
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt(SCREEN_ORIENTATION, orientation);
        ed.apply();
    }

}
