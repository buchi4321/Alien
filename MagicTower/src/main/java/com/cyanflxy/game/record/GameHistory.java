package com.cyanflxy.game.record;

import com.cyanflxy.game.bean.BeanParent;
import com.cyanflxy.game.bean.GameBean;
import com.cyanflxy.game.bean.MapBean;
import com.github.cyanflxy.magictower.AppApplication;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import static com.github.cyanflxy.magictower.AppApplication.baseContext;

public class GameHistory {
    private static final String GAME_NAME = "SavePrincess_21";
    private static final String GAME_START_FILE = "main.file";

    private static String DATA_PATH;
    public static final String AUTO_SAVE = "auto";
    public static final String SAVE_RECORD = "record_";//带编号

    private static Gson gson = new Gson();

    static {
        DATA_PATH = baseContext.getFilesDir().getAbsolutePath() + GAME_NAME;
    }

    public static boolean haveAutoSave() {
        File auto = new File(DATA_PATH, AUTO_SAVE);
        File mainFile = new File(auto,GAME_START_FILE);
        return mainFile.exists();
    }

    public static void deleteAutoSave() {
        // TODO STUB
    }

    public static GameBean getGame(String record) {
        String recordFile = DATA_PATH + "/" + record + "/" + GAME_START_FILE;
        String assetsFile = GAME_NAME + "/" + GAME_START_FILE;

        String content = getFileContent(recordFile, assetsFile);
        GameBean bean = gson.fromJson(content, GameBean.class);
        bean.setSavePath(GAME_START_FILE);
        return bean;
    }

    public static MapBean getMap(String record, String mapFile) {
        String recordFile = DATA_PATH + "/" + record + "/" + mapFile;
        String assetsFile = GAME_NAME + "/" + mapFile;

        String content = getFileContent(recordFile, assetsFile);
        MapBean bean = gson.fromJson(content, MapBean.class);
        bean.setSavePath(mapFile);
        return bean;
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

            return getInputStreamString(is);

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
        return AppApplication.baseContext.getAssets().open(fileName);
    }

    private static String getInputStreamString(InputStream is) throws IOException {
        byte[] buffer = new byte[is.available()];
        int len = is.read(buffer);
        return new String(buffer, 0, len, "utf-8");
    }

    public static boolean saveBean(String record, BeanParent bean) {
        String fileName = DATA_PATH + "/" + record + "/" + bean.getSavePath();
        File file = new File(fileName);

        File folder = file.getParentFile();
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                return false;
            }
        }

        String str = gson.toJson(bean);
        return saveFile(str, file);
    }

    public static boolean saveFile(String str, File path) {
        BufferedWriter bw = null;

        try {
            bw = new BufferedWriter(new FileWriter(path));
            bw.write(str);
            bw.flush();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;

        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    public static boolean copyRecord(String source, String dest) {
        return true;
    }
}
