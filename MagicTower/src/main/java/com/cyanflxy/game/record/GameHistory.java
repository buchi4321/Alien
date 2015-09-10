package com.cyanflxy.game.record;

import com.cyanflxy.game.bean.GameBean;

import java.io.File;

import static com.github.cyanflxy.magictower.AppApplication.baseContext;

public class GameHistory {
    private static String DATA_PATH;
    public static final String AUTO_SAVE = "auto";
    public static final String SAVE_RECORD = "record_";//带编号

    static {
        DATA_PATH = baseContext.getFilesDir().getAbsolutePath();
    }

    public static boolean haveAutoSave() {
        File auto = new File(DATA_PATH, AUTO_SAVE);
        return auto.exists();
    }

    public static void deleteAutoSave() {
        // TODO STUB
    }

    public static GameBean getGame(String record) {
        return new GameBean();
    }
}
