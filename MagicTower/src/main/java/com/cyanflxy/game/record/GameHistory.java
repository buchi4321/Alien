package com.cyanflxy.game.record;

import android.text.TextUtils;

import com.cyanflxy.common.FileUtils;
import com.cyanflxy.game.bean.BeanParent;
import com.cyanflxy.game.bean.GameBean;
import com.cyanflxy.game.data.GameSharedPref;
import com.github.cyanflxy.magictower.R;
import com.google.gson.Gson;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.github.cyanflxy.magictower.AppApplication.baseContext;

public class GameHistory {
    public static final String AUTO_SAVE = "auto";
    public static final String SAVE_RECORD = "record_";//带编号

    public static final String RECORD_NAME = "name";
    public static final String RECORD_TIME = "time";

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
        return FileUtils.deleteFolder(new File(GameReader.DATA_PATH, recordName));
    }

    public static boolean autoSave(BeanParent bean) {
        return save(AUTO_SAVE, bean);
    }

    public static boolean save(String record, BeanParent bean) {
        String fileName = GameReader.DATA_PATH + "/" + record + "/" + bean.getSavePath();
        File file = new File(fileName);

        File folder = file.getParentFile();
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                return false;
            }
        }

        String timeFile = GameReader.DATA_PATH + "/" + record + "/" + RECORD_TIME;
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
            record.id = 0;
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
        File nameFile = new File(GameReader.DATA_PATH + "/" + record + "/" + RECORD_NAME);
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
        File nameFile = new File(GameReader.DATA_PATH + "/" + record + "/" + RECORD_TIME);
        if (!nameFile.exists()) {
            return "";
        }

        return FileUtils.getFileContent(nameFile);
    }

    public static void rename(String recordName, String newName) {
        File nameFile = new File(GameReader.DATA_PATH + "/" + recordName + "/" + RECORD_NAME);
        FileUtils.saveFile(newName, nameFile);
    }
}
