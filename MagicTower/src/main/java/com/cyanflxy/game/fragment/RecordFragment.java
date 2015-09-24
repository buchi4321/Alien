package com.cyanflxy.game.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cyanflxy.game.data.GameSharedPref;
import com.cyanflxy.game.dialog.CommInputDialog;
import com.cyanflxy.game.dialog.ProgressFragmentDialog;
import com.cyanflxy.game.dialog.RecordItemMenuDialog;
import com.cyanflxy.game.driver.GameContext;
import com.cyanflxy.game.driver.ImageResourceManager;
import com.cyanflxy.game.record.GameHistory;
import com.cyanflxy.game.record.GameRecord;
import com.cyanflxy.game.record.RecordLoadTask;
import com.github.cyanflxy.magictower.R;

import java.util.ArrayList;

public class RecordFragment extends BaseFragment
        implements View.OnClickListener,
        AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {

    public static final String TAG = "RecordFragment";

    private static final String ARG_START_MODE = "start_mode";
    public static final int MODE_READ = 0;
    public static final int MODE_SAVE = 1;

    private static final String SAVE_MENU_POSITION = "menu_position";
    private static final String SAVE_RECORD_LIST = "record_list";

    public static RecordFragment newInstance(int mode) {
        RecordFragment fragment = new RecordFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(ARG_START_MODE, mode);
        fragment.setArguments(bundle);

        return fragment;
    }

    public interface OnRecordItemSelected {
        void onSelected(int mode, String record);
    }

    private int mode;
    private OnRecordItemSelected listener;
    private ArrayList<GameRecord> gameRecords;
    private RecordLoadTask recordLoadTask;
    private BaseAdapter listAdapter;
    private ImageResourceManager imageManager;
    private int onItemMenuPosition;

    private ListView listView;
    private View emptyView;

    public void setRecordItemSelected(OnRecordItemSelected l) {
        listener = l;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listAdapter = new RecordAdapter();

        mode = getArguments().getInt(ARG_START_MODE);
        imageManager = GameContext.getInstance().getImageResourceManager();

        resetFragmentCallback();

        if (savedInstanceState != null) {
            onItemMenuPosition = savedInstanceState.getInt(SAVE_MENU_POSITION);
            //noinspection unchecked
            gameRecords = (ArrayList<GameRecord>) savedInstanceState.getSerializable(SAVE_RECORD_LIST);
        }

        if (gameRecords == null) {
            loadGameRecord();
        }

    }

    private void loadGameRecord() {

        FragmentManager fm = getFragmentManager();
        ProgressFragmentDialog dialog = (ProgressFragmentDialog) fm.findFragmentByTag(ProgressFragmentDialog.TAG);

        if (dialog != null) {
            recordLoadTask = (RecordLoadTask) dialog.getTaskObject();
            fm.beginTransaction().remove(dialog).commit();
        }

        if (recordLoadTask == null) {
            recordLoadTask = new RecordLoadTask();
            recordLoadTask.execute(mode);
        }

        recordLoadTask.setOnTaskCompleteListener(onTaskCompleteListener);

        dialog = new ProgressFragmentDialog();
        dialog.setTaskObject(recordLoadTask);
        dialog.show(fm, ProgressFragmentDialog.TAG);

    }

    private RecordLoadTask.OnTaskCompleteListener onTaskCompleteListener
            = new RecordLoadTask.OnTaskCompleteListener() {
        @Override
        public void onTaskComplete(ArrayList<GameRecord> recordList) {
            recordLoadTask = null;
            gameRecords = recordList;
            listAdapter.notifyDataSetChanged();

            FragmentManager fm = getFragmentManager();
            ProgressFragmentDialog dialog = (ProgressFragmentDialog) fm.findFragmentByTag(ProgressFragmentDialog.TAG);
            if (dialog != null) {
                dialog.dismiss();
            }

            if (recordList.size() == 0) {
                if (listView != null && emptyView != null) {
                    listView.setEmptyView(emptyView);
                }
            }

        }
    };


    private void resetFragmentCallback() {
        FragmentManager fm = getFragmentManager();

        RecordItemMenuDialog itemMenuDialog = (RecordItemMenuDialog) fm.findFragmentByTag(RecordItemMenuDialog.TAG);
        if (itemMenuDialog != null) {
            itemMenuDialog.setOnMenuClickListener(onMenuClickListener);
        }

        CommInputDialog inputDialog = (CommInputDialog) fm.findFragmentByTag(CommInputDialog.TAG);
        if (inputDialog != null) {
            inputDialog.setOnInputFinishListener(onInputFinishListener);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = (ListView) view.findViewById(R.id.record_list);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        listView.setAdapter(listAdapter);

        emptyView = view.findViewById(R.id.empty_view);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do nothing
            }
        });

        view.findViewById(R.id.back).setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroyView();

        if (recordLoadTask != null) {
            recordLoadTask.setOnTaskCompleteListener(null);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(SAVE_MENU_POSITION, onItemMenuPosition);
        outState.putSerializable(SAVE_RECORD_LIST, gameRecords);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                ((OnFragmentCloseListener) getActivity()).closeFragment(this);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (listener != null) {
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            GameRecord record = viewHolder.gameRecord;

            if (record.hero == null) {
                GameSharedPref.addNewRecordId();
            }

            listener.onSelected(mode, record.recordName);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        GameRecord record = viewHolder.gameRecord;
        if (record.hero == null || record.id == 0) {
            return true;
        }

        onItemMenuPosition = position;

        RecordItemMenuDialog dialog = new RecordItemMenuDialog();
        dialog.setOnMenuClickListener(onMenuClickListener);
        dialog.show(getFragmentManager(), RecordItemMenuDialog.TAG);

        return true;
    }

    private GameRecord getCurrentMenuRecord() {
        return gameRecords.get(onItemMenuPosition);
    }

    private RecordItemMenuDialog.OnMenuClickListener onMenuClickListener
            = new RecordItemMenuDialog.OnMenuClickListener() {
        @Override
        public void onDelete() {
            GameRecord record = getCurrentMenuRecord();
            GameHistory.deleteRecord(record.recordName);

            gameRecords.remove(record);
            listAdapter.notifyDataSetChanged();
        }

        @Override
        public void onRename() {
            GameRecord record = getCurrentMenuRecord();

            CommInputDialog dialog = CommInputDialog.newInstance(getString(R.string.input_name), record.displayName);
            dialog.setOnInputFinishListener(onInputFinishListener);
            dialog.show(getFragmentManager(), CommInputDialog.TAG);
        }
    };

    private CommInputDialog.OnInputFinishListener onInputFinishListener
            = new CommInputDialog.OnInputFinishListener() {
        @Override
        public void onInputFinish(DialogFragment dialog, String result) {
            dialog.dismiss();

            GameRecord record = getCurrentMenuRecord();
            GameHistory.rename(record.recordName, result);
            record.displayName = result;

            listAdapter.notifyDataSetChanged();
        }
    };

    private class RecordAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return gameRecords == null ? 0 : gameRecords.size();
        }

        @Override
        public Object getItem(int position) {
            return gameRecords == null ? null : gameRecords.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GameRecord record = gameRecords.get(position);

            View view = convertView;
            ViewHolder viewHolder;
            if (view == null) {
                view = LayoutInflater.from(getActivity()).inflate(R.layout.view_record_item, null);
                viewHolder = new ViewHolder();
                view.setTag(viewHolder);

                viewHolder.avatarImage = (ImageView) view.findViewById(R.id.avatar);
                viewHolder.hpText = (TextView) view.findViewById(R.id.hero_hp);
                viewHolder.damageText = (TextView) view.findViewById(R.id.hero_damage);
                viewHolder.defenceText = (TextView) view.findViewById(R.id.hero_defense);
                viewHolder.floorText = (TextView) view.findViewById(R.id.floor);
                viewHolder.timeText = (TextView) view.findViewById(R.id.record_time);
                viewHolder.nameText = (TextView) view.findViewById(R.id.record_name);

                viewHolder.attributeArea = view.findViewById(R.id.attribute_area);
                viewHolder.newRecordView = view.findViewById(R.id.new_record);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.gameRecord = record;

            if (record.hero == null) {
                viewHolder.attributeArea.setVisibility(View.INVISIBLE);
                viewHolder.avatarImage.setVisibility(View.INVISIBLE);
                viewHolder.newRecordView.setVisibility(View.VISIBLE);
            } else {
                viewHolder.attributeArea.setVisibility(View.VISIBLE);
                viewHolder.avatarImage.setVisibility(View.VISIBLE);
                viewHolder.newRecordView.setVisibility(View.INVISIBLE);

                viewHolder.avatarImage.setImageBitmap(imageManager.getBitmap(record.hero.avatar));
                viewHolder.hpText.setText(String.valueOf(record.hero.hp));
                viewHolder.damageText.setText(String.valueOf(record.hero.damage));
                viewHolder.defenceText.setText(String.valueOf(record.hero.defense));
                viewHolder.floorText.setText(getString(R.string.floor, record.hero.floor));
                viewHolder.timeText.setText(record.recordTime);
                viewHolder.nameText.setText(record.displayName);

            }

            return view;
        }
    }

    private class ViewHolder {
        public ImageView avatarImage;
        public TextView hpText;
        public TextView damageText;
        public TextView defenceText;
        public TextView floorText;
        public TextView timeText;
        public TextView nameText;

        public View newRecordView;
        public View attributeArea;

        public GameRecord gameRecord;
    }


}
