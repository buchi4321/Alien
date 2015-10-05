package com.cyanflxy.game.fragment;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.cyanflxy.common.Utils;
import com.cyanflxy.game.data.GameSharedPref;
import com.cyanflxy.game.widget.SettingCheckBox;
import com.github.cyanflxy.magictower.BuildConfig;
import com.github.cyanflxy.magictower.R;

public class SettingFragment extends BaseFragment {

    public static final String TAG = "SettingFragment";

    private int orientation;

    // 屏幕亮度
    private SeekBar lightSeekBar;
    // 游戏音量
    private SeekBar volumeSeekBar;

    // 背景音乐
    private SettingCheckBox bgMusic;
    // 游戏音效
    private SettingCheckBox gameSound;
    // 自动寻路
    private SettingCheckBox autoWay;
    // 商店快捷
    private SettingCheckBox shopShortcut;
    // 开启所有功能
    private SettingCheckBox openAllFunction;
    // 无视地图
    private SettingCheckBox mapInvisible;
    // 显示打斗界面
    private SettingCheckBox showFightView;

    private RadioGroup orientationGroup;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ignore
            }
        });

        // 屏幕亮度
        lightSeekBar = (SeekBar) view.findViewById(R.id.screen_light_seek);
        lightSeekBar.setOnSeekBarChangeListener(lightSeekListener);

        // 游戏音量
        volumeSeekBar = (SeekBar) view.findViewById(R.id.game_volume_seek);
        volumeSeekBar.setOnSeekBarChangeListener(volumeSeekListener);

        // 背景音乐
        bgMusic = (SettingCheckBox) view.findViewById(R.id.background_music);
        bgMusic.setOnCheckedChangeListener(onCheckedChangeListener);

        // 游戏音效
        gameSound = (SettingCheckBox) view.findViewById(R.id.game_sound);
        gameSound.setOnCheckedChangeListener(onCheckedChangeListener);

        // 自动寻路
        autoWay = (SettingCheckBox) view.findViewById(R.id.auto_find_way);
        autoWay.setOnCheckedChangeListener(onCheckedChangeListener);

        // 屏幕方向
        orientationGroup = (RadioGroup) view.findViewById(R.id.orientation_group);
        orientationGroup.setOnCheckedChangeListener(orientationChangeListener);

        // 开发模式功能
        View devView = view.findViewById(R.id.dev_function);
        if (BuildConfig.DEBUG) {
            devView.setVisibility(View.VISIBLE);

            // 商店快捷
            shopShortcut = (SettingCheckBox) view.findViewById(R.id.shop_shortcut);
            shopShortcut.setOnCheckedChangeListener(onCheckedChangeListener);

            // 开启所有功能
            openAllFunction = (SettingCheckBox) view.findViewById(R.id.open_all_function);
            openAllFunction.setOnCheckedChangeListener(onCheckedChangeListener);

            // 无视地图
            mapInvisible = (SettingCheckBox) view.findViewById(R.id.map_invisible);
            mapInvisible.setOnCheckedChangeListener(onCheckedChangeListener);

            // 显示打斗界面
            showFightView = (SettingCheckBox) view.findViewById(R.id.show_fight_dialog);
            showFightView.setOnCheckedChangeListener(onCheckedChangeListener);

        } else {
            devView.setVisibility(View.GONE);
        }

        view.findViewById(R.id.back).setOnClickListener(onCloseListener);
    }

    @Override
    public void onResume() {
        super.onResume();

        // 必须在这里刷新状态，否则横竖屏切换的时候，checkbox状态会丢失，不知为何。
        lightSeekBar.setProgress(GameSharedPref.getScreenLight());
        volumeSeekBar.setProgress(GameSharedPref.getGameVolume());

        bgMusic.setChecked(GameSharedPref.isPlayBackgroundMusic());
        gameSound.setChecked(GameSharedPref.isPlayGameSound());
        autoWay.setChecked(GameSharedPref.isAutoFindWay());
        shopShortcut.setChecked(GameSharedPref.isOpenShopShortcut());
        openAllFunction.setChecked(GameSharedPref.isOpenAllFunction());
        mapInvisible.setChecked(GameSharedPref.isMapInvisible());
        showFightView.setChecked(GameSharedPref.isShowFightView());

        orientation = GameSharedPref.getScreenOrientation();
        switch (orientation) {
            case ActivityInfo.SCREEN_ORIENTATION_SENSOR:
                ((RadioButton) orientationGroup.findViewById(R.id.orientation_auto)).setChecked(true);
                break;
            case ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT:
                ((RadioButton) orientationGroup.findViewById(R.id.orientation_port)).setChecked(true);
                break;
            case ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE:
                ((RadioButton) orientationGroup.findViewById(R.id.orientation_land)).setChecked(true);
                break;
        }
    }

    private SeekBar.OnSeekBarChangeListener lightSeekListener
            = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Utils.setBrightness(getActivity(), progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            GameSharedPref.setScreenLight(seekBar.getProgress());
        }
    };

    private SeekBar.OnSeekBarChangeListener volumeSeekListener
            = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            GameSharedPref.setGameVolume(seekBar.getProgress());
        }
    };

    private RadioGroup.OnCheckedChangeListener orientationChangeListener
            = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.orientation_auto:
                    GameSharedPref.setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    checkOrientation();
                    break;
                case R.id.orientation_port:
                    GameSharedPref.setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                    checkOrientation();
                    break;
                case R.id.orientation_land:
                    GameSharedPref.setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                    checkOrientation();
                    break;
            }
        }
    };

    private void checkOrientation() {
        int newOrientation = GameSharedPref.getScreenOrientation();
        if (orientation != newOrientation) {
            //noinspection ResourceType
            getActivity().setRequestedOrientation(newOrientation);
        }
    }

    private SettingCheckBox.OnCheckedChangeListener onCheckedChangeListener
            = new SettingCheckBox.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(SettingCheckBox buttonView, boolean isChecked) {

            switch (buttonView.getId()) {
                case R.id.background_music:
                    GameSharedPref.setPlayBackgroundMusic(isChecked);
                    break;
                case R.id.game_sound:
                    GameSharedPref.setPlayGameSound(isChecked);
                    break;
                case R.id.auto_find_way:
                    GameSharedPref.setAutoFindWay(isChecked);
                    break;
                case R.id.shop_shortcut:
                    GameSharedPref.setOpenShopShortcut(isChecked);
                    break;
                case R.id.open_all_function:
                    GameSharedPref.setOpenAllFunction(isChecked);
                    break;
                case R.id.map_invisible:
                    GameSharedPref.setMapInvisible(isChecked);
                    break;
                case R.id.show_fight_dialog:
                    GameSharedPref.setShowFightView(isChecked);
                    break;
            }
        }
    };
}
