package com.cyanflxy.game.record;

import java.io.File;

import static com.github.cyanflxy.magictower.AppApplication.baseContext;

public class GameHistory {
    private static String DATA_PATH;
    private static final String AUTO_SAVE = "auto";

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
}
