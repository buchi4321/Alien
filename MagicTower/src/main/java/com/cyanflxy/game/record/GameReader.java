package com.cyanflxy.game.record;

import android.os.Environment;

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

    public static String DATA_PATH;
    public static final String AUTO_SAVE = GameHistory.AUTO_SAVE;

    private static Gson gson = new Gson();

    static {
        DATA_PATH = Environment.getExternalStorageDirectory() + "/CyanFlxy/Alien/" + GAME_NAME;
        new File(DATA_PATH).mkdirs();
    }

    public static GameBean getGameMainData() {
        return getGameMainData(AUTO_SAVE);
    }

    public static GameBean getGameMainData(String record) {
        String recordFile = DATA_PATH + "/" + record + "/" + GAME_START_FILE;
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
        String recordFile = DATA_PATH + "/" + record + "/" + mapFile;
        String assetsFile = getAssetsFileName(mapFile);

        String content = getFileContent(recordFile, assetsFile);
        MapBean bean = gson.fromJson(content, MapBean.class);
        bean.setSavePath(mapFile);
        return bean;
    }

    public static boolean haveReachMap(String mapName) {
        String recordFile = DATA_PATH + "/" + AUTO_SAVE + "/" + mapName;
        return new File(recordFile).exists();
    }

    public static String getAssetsFileName(String src) {
        return GAME_NAME + "/" + src;
    }

    private static String getFileContent(String recordFile, String assetsFile) {

        File file = new File(recordFile);
        InputStream is = null;

        try {
            if (!file.exists()) {
                is = getAssetsFileIS(assetsFile);
            } else {
                is = new FileInputStream(file);
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

    public static InputStream getAssetsFileIS(String fileName) throws IOException {
        return baseContext.getAssets().open(fileName);
    }

}
