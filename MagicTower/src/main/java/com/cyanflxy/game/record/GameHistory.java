package com.cyanflxy.game.record;

import android.os.Environment;

import com.cyanflxy.game.bean.BeanParent;
import com.cyanflxy.game.bean.GameBean;
import com.cyanflxy.game.bean.MapBean;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.github.cyanflxy.magictower.AppApplication.baseContext;

public class GameHistory {
    private static final String GAME_NAME = "SavePrincess_21";
    private static final String GAME_START_FILE = "main.file";

    private static String DATA_PATH;
    public static final String AUTO_SAVE = "auto";
    public static final String SAVE_RECORD = "record_";//带编号

    private static Gson gson = new Gson();

    static {
        DATA_PATH = Environment.getExternalStorageDirectory()+"/CyanFlxy/Alien/"+GAME_NAME;
        new File(DATA_PATH).mkdirs();
    }

    public static String getAssetsFileName(String src) {
        return GAME_NAME + "/" + src;
    }

    public static boolean haveAutoSave() {
        File auto = new File(DATA_PATH, AUTO_SAVE);
        File mainFile = new File(auto, GAME_START_FILE);
        return mainFile.exists();
    }

    public static boolean deleteAutoSave() {
        return deleteRecord(AUTO_SAVE);
    }

    public static boolean deleteRecord(String recordName) {
        return deleteFolder(new File(DATA_PATH, recordName));
    }

    private static boolean deleteFolder(File folder) {
        if (!folder.exists()) {
            return true;
        }

        File[] subFiles = folder.listFiles();
        if (subFiles != null) {
            for (File f : subFiles) {
                if (f.isFile()) {
                    if (!f.delete()) {
                        return false;
                    }
                } else {
                    if (!deleteFolder(f)) {
                        return false;
                    }
                }
            }
        }

        return folder.delete();

    }

    public static GameBean getGame() {
        String recordFile = DATA_PATH + "/" + AUTO_SAVE + "/" + GAME_START_FILE;
        String assetsFile = getAssetsFileName(GAME_START_FILE);

        String content = getFileContent(recordFile, assetsFile);
        GameBean bean = gson.fromJson(content, GameBean.class);
        bean.setSavePath(GAME_START_FILE);
        return bean;
    }

    public static MapBean getMap(String mapFile) {
        String recordFile = DATA_PATH + "/" + AUTO_SAVE + "/" + mapFile;
        String assetsFile = getAssetsFileName(mapFile);

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

    public static InputStream getAssetsFileIS(String fileName) throws IOException {
        return baseContext.getAssets().open(fileName);
    }

    public static String getInputStreamString(InputStream is) throws IOException {
        byte[] buffer = new byte[is.available()];
        int len = is.read(buffer);
        return new String(buffer, 0, len, "utf-8");
    }

    public static boolean autoSave( BeanParent bean) {
        String fileName = DATA_PATH + "/" + AUTO_SAVE + "/" + bean.getSavePath();
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
        File sourceFolder = new File(DATA_PATH, source);
        File destFolder = new File(DATA_PATH, dest);

        return copyFolder(sourceFolder, destFolder);
    }

    private static boolean copyFolder(File source, File dest) {
        if (!dest.exists()) {
            if (!dest.mkdir()) {
                return false;
            }
        }

        String[] sourceFiles = source.list();
        for (String file : sourceFiles) {
            File src = new File(source, file);
            File dst = new File(dest, file);
            if (src.isFile()) {
                if (!copyFile(src, dst)) {
                    return false;
                }
            } else {
                if (!copyFolder(src, dst)) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean copyFile(File src, File dst) {
        InputStream is = null;
        OutputStream os = null;
        byte[] buffer = new byte[1024];

        try {
            is = new FileInputStream(src);
            os = new FileOutputStream(dst);

            while (true) {
                int len = is.read(buffer);
                if (len <= 0) {
                    break;
                }

                os.write(buffer, 0, len);
            }

            os.flush();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

    }
}
