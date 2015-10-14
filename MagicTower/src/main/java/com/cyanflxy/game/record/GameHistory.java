package com.cyanflxy.game.record;

import android.text.TextUtils;

import com.cyanflxy.common.FileUtils;
import com.cyanflxy.game.bean.BeanParent;
import com.cyanflxy.game.bean.GameBean;
import com.cyanflxy.game.bean.ShopBean;
import com.cyanflxy.game.data.GameSharedPref;
import com.github.cyanflxy.magictower.R;
import com.google.gson.Gson;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.github.cyanflxy.magictower.AppApplication.baseContext;

public class GameHistory {

    public static final int AUTO_SAVE_ID = Integer.MAX_VALUE;

    public static final String AUTO_SAVE = "auto";
    public static final String SAVE_RECORD = "record_";//带编号

    public static final String RECORD_NAME = "name";
    public static final String RECORD_TIME = "time";
    private static final String SHOP_SHORTCUT = "shop_shortcut.file";

    private static Gson gson = new Gson();

    public static boolean haveAutoSave() {
        File auto = new File(GameReader.DATA_PATH, AUTO_SAVE);
        File mainFile = new File(auto, GameReader.GAME_START_FILE);
        return mainFile.exists();
    }

    public static boolean deleteAutoSave() {
        return deleteRecord(AUTO_SAVE);
    }

    public static boolean deleteRecord(String recordName) {
        File folder = new File(GameReader.DATA_PATH, recordName);
        File renameFolder = new File(GameReader.DATA_PATH, "" + System.currentTimeMillis());
        if (!folder.renameTo(renameFolder)) {
            return false;
        }

        return FileUtils.deleteFolder(renameFolder);
    }

    public static boolean autoSave(BeanParent bean) {
        return save(AUTO_SAVE, bean);
    }

    public static boolean save(String record, BeanParent bean) {
        String fileName = GameReader.DATA_PATH + File.separator + record + File.separator + bean.getSavePath();
        File file = new File(fileName);

        File folder = file.getParentFile();
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                return false;
            }
        }

        String timeFile = GameReader.DATA_PATH + File.separator + record + File.separator + RECORD_TIME;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String time = format.format(new Date());
        FileUtils.saveFile(time, new File(timeFile));

        String str = gson.toJson(bean);
        return FileUtils.saveFile(str, file);
    }

    public static boolean copyRecord(String source, String dest) {
        File sourceFolder = new File(GameReader.DATA_PATH, source);
        File destFolder = new File(GameReader.DATA_PATH, dest);

        return FileUtils.copyFolder(sourceFolder, destFolder);
    }

    public static GameRecord getAutoSaveRecord() {
        if (haveAutoSave()) {
            GameRecord record = getGameRecord(AUTO_SAVE);
            record.id = AUTO_SAVE_ID;
            record.displayName = baseContext.getString(R.string.auto_save);
            return record;
        } else {
            return null;
        }
    }

    private static GameRecord getGameRecord(String recordName) {
        GameRecord record = new GameRecord();
        record.id = getRecordId(recordName);
        record.displayName = getRecordName(recordName);
        record.recordName = recordName;
        record.recordTime = getRecordTime(recordName);

        GameBean game = GameReader.getGameMainData(recordName);
        record.hero = game.hero;

        return record;
    }

    public static List<GameRecord> getRecords() {
        List<GameRecord> list = new ArrayList<>();

        File recordFolder = new File(GameReader.DATA_PATH);
        String[] recordNames = recordFolder.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.startsWith(SAVE_RECORD);
            }
        });

        int maxId = 0;
        if (recordNames != null && recordNames.length > 0) {
            for (String record : recordNames) {
                list.add(getGameRecord(record));

                int id = getRecordId(record);
                if (maxId < id) {
                    maxId = id;
                }
            }
        }

        GameSharedPref.setMaxRecordId(maxId + 1);

        return list;
    }

    public static String getRecordName(String record) {
        if (TextUtils.equals(record, AUTO_SAVE)) {
            return baseContext.getString(R.string.auto_save);
        }

        String name;
        File nameFile = new File(GameReader.DATA_PATH + File.separator + record + File.separator + RECORD_NAME);
        if (nameFile.exists()) {
            name = FileUtils.getFileContent(nameFile);
        } else {
            name = baseContext.getString(R.string.record_name, getRecordId(record));
        }

        return name;
    }

    private static int getRecordId(String recordName) {
        if (!recordName.startsWith(SAVE_RECORD)) {
            return 0;
        }
        String index = recordName.substring(SAVE_RECORD.length());
        try {
            return Integer.valueOf(index);
        } catch (Exception e) {
            return 0;
        }
    }

    private static String getRecordTime(String record) {
        File nameFile = new File(GameReader.DATA_PATH + File.separator + record + File.separator + RECORD_TIME);
        if (!nameFile.exists()) {
            return "";
        }

        return FileUtils.getFileContent(nameFile);
    }

    public static void rename(String recordName, String newName) {
        File nameFile = new File(GameReader.DATA_PATH + File.separator + recordName + File.separator + RECORD_NAME);
        FileUtils.saveFile(newName, nameFile);
    }

    public static boolean haveShop() {
        String shopFile = GameReader.DATA_PATH + File.separator + AUTO_SAVE + File.separator + SHOP_SHORTCUT;
        return new File(shopFile).exists();
    }

    public static ShopBean[] getShops() {
        String shopFile = GameReader.DATA_PATH + File.separator + AUTO_SAVE + File.separator + SHOP_SHORTCUT;
        String content = GameReader.getFileContent(shopFile, null);
        if (!TextUtils.isEmpty(content)) {
            return gson.fromJson(content, ShopBean[].class);
        } else {
            return null;
        }
    }

    public static void saveShop(ShopBean shop) {
        String shopFile = GameReader.DATA_PATH + File.separator + AUTO_SAVE + File.separator + SHOP_SHORTCUT;

        ShopBean[] shops = getShops();
        if (shops != null && shops.length > 0) {
            Set<ShopBean> shopSet = new HashSet<>(Arrays.asList(shops));
            shopSet.add(shop);

            FileUtils.saveFile(gson.toJson(shopSet), new File(shopFile));
        } else {
            shops = new ShopBean[]{shop};
            FileUtils.saveFile(gson.toJson(shops), new File(shopFile));
        }

    }

}
