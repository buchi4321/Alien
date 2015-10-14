package com.cyanflxy.game.record;

import android.os.Environment;
import android.text.TextUtils;

import com.cyanflxy.common.FileUtils;
import com.cyanflxy.game.bean.GameBean;
import com.cyanflxy.game.bean.MapBean;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.github.cyanflxy.magictower.AppApplication.baseContext;

public class GameReader {

    public static final String GAME_NAME = "SavePrincess_21";
    public static final String GAME_START_FILE = "main.file";
    public static final String GAME_MAIN_MUSIC = "music/sound_main.mp3";

    public static String DATA_PATH;
    public static final String AUTO_SAVE = GameHistory.AUTO_SAVE;

    private static Gson gson = new Gson();

    static {
        DATA_PATH = Environment.getExternalStorageDirectory() + "/CyanFlxy/Alien/" + GAME_NAME;
        new File(DATA_PATH).mkdirs();
    }

    public static String getMainMusic() {
        return GAME_NAME + File.separator + GAME_MAIN_MUSIC;
    }

    public static GameBean getGameMainData() {
        return getGameMainData(AUTO_SAVE);
    }

    public static GameBean getGameMainData(String record) {
        String recordFile = DATA_PATH + File.separator + record + File.separator + GAME_START_FILE;
        String assetsFile = getAssetsFileName(GAME_START_FILE);

        String content = getFileContent(recordFile, assetsFile);
        GameBean bean = gson.fromJson(content, GameBean.class);
        bean.setSavePath(GAME_START_FILE);
        return bean;
    }

    public static MapBean getMapData(String mapFile) {
        return getMapData(AUTO_SAVE, mapFile);
    }

    public static MapBean getMapData(String record, String mapFile) {
        String recordFile = DATA_PATH + File.separator + record + File.separator + mapFile;
        String assetsFile = getAssetsFileName(mapFile);

        String content = getFileContent(recordFile, assetsFile);
        MapBean bean = gson.fromJson(content, MapBean.class);
        bean.setSavePath(mapFile);
        return bean;
    }

    public static String getAssetsFileName(String src) {
        return GAME_NAME + File.separator + src;
    }

    public static String getFileContent(String recordFile, String assetsFile) {

        File file = new File(recordFile);
        InputStream is = null;

        try {
            if (file.exists()) {
                is = new FileInputStream(file);
            } else if (!TextUtils.isEmpty(assetsFile)) {
                is = getAssetsFileIS(assetsFile);
            } else {
                return null;
            }

            return FileUtils.getInputStreamString(is);

        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static InputStream getAssetsFileIS(String fileName) throws IOException {
        return baseContext.getAssets().open(fileName);
    }

}
