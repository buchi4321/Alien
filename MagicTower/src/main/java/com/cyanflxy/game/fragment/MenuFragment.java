package com.cyanflxy.game.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.cyanflxy.magictower.R;

public class MenuFragment extends BaseFragment implements View.OnClickListener {

    public static final String TAG = "MenuFragment";

    public interface OnMenuClickListener {
        void onMainMenu();

        void onReadRecord();

        void onSaveRecord();

        void onSetting();

    }

    private OnMenuClickListener listener;

    public void setOnMenuClickListener(OnMenuClickListener l) {
        listener = l;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.main_menu).setOnClickListener(this);
        view.findViewById(R.id.read_record).setOnClickListener(this);
        view.findViewById(R.id.save_record).setOnClickListener(this);
        view.findViewById(R.id.setting).setOnClickListener(this);
        view.findViewById(R.id.back_game).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_menu:
                if (listener != null) {
                    listener.onMainMenu();
                }
                break;
            case R.id.read_record:
                if (listener != null) {
                    listener.onReadRecord();
                }
                break;
            case R.id.save_record:
                if (listener != null) {
                    listener.onSaveRecord();
                }
                break;
            case R.id.setting:
                if (listener != null) {
                    listener.onSetting();
                }
                break;
            case R.id.back_game:
                closeFragment();
                break;
        }
    }
}
