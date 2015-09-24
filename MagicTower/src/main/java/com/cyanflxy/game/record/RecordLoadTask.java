package com.cyanflxy.game.record;

import android.os.AsyncTask;

import com.cyanflxy.game.data.GameSharedPref;
import com.cyanflxy.game.fragment.RecordFragment;

import java.util.ArrayList;
import java.util.Collections;

public class RecordLoadTask extends AsyncTask<Integer, Integer, ArrayList<GameRecord>> {

    public interface OnTaskCompleteListener {
        void onTaskComplete(ArrayList<GameRecord> recordList);
    }

    private boolean isComplete;
    private ArrayList<GameRecord> recordList;
    private OnTaskCompleteListener onTaskCompleteListener;

    public RecordLoadTask() {
        isComplete = false;
    }

    public void setOnTaskCompleteListener(OnTaskCompleteListener onTaskCompleteListener) {
        this.onTaskCompleteListener = onTaskCompleteListener;
        sendResult();
    }

    @Override
    protected ArrayList<GameRecord> doInBackground(Integer... params) {

        ArrayList<GameRecord> records = new ArrayList<>();

        records.add(GameHistory.getAutoSaveRecord());
        records.addAll(GameHistory.getRecords());

        if (params != null && params.length == 1) {
            if (params[0] == RecordFragment.MODE_SAVE) {
                GameRecord record = new GameRecord();
                record.id = GameSharedPref.getNewRecordId();
                record.recordName = GameHistory.SAVE_RECORD + GameSharedPref.getNewRecordId();
                records.add(record);
            }
        }

        Collections.sort(records);

        return records;
    }

    @Override
    protected void onPostExecute(ArrayList<GameRecord> records) {
        super.onPostExecute(records);
        recordList = records;
        isComplete = true;
        sendResult();
    }

    private void sendResult() {
        if (isComplete && onTaskCompleteListener != null) {
            onTaskCompleteListener.onTaskComplete(recordList);
        }
    }
}
