package com.github.cyanflxy.magictower;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.cyanflxy.common.Utils;
import com.cyanflxy.game.activity.FragmentStartManager;
import com.cyanflxy.game.activity.GameActivity;
import com.cyanflxy.game.data.GameSharedPref;
import com.cyanflxy.game.dialog.NewGameDialog;
import com.cyanflxy.game.fragment.BaseFragment;
import com.cyanflxy.game.fragment.RecordFragment;
import com.cyanflxy.game.fragment.SettingFragment;
import com.cyanflxy.game.record.GameHistory;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.game.UMGameAgent;

public class MainActivity extends FragmentActivity implements View.OnClickListener,
        BaseFragment.OnFragmentCloseListener {

    private static final long BACK_TIME = 1000;

    private long lastBackPressed;

    private FragmentStartManager fragmentStartManager;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Utils.setBrightness(this, GameSharedPref.getScreenLight());
        //noinspection ResourceType
        setRequestedOrientation(GameSharedPref.getScreenOrientation());

        setContentView(R.layout.activity_main);

        findViewById(R.id.new_game).setOnClickListener(this);
        findViewById(R.id.read_record).setOnClickListener(this);
        findViewById(R.id.setting).setOnClickListener(this);
        findViewById(R.id.exit).setOnClickListener(this);

        fragmentStartManager = new FragmentStartManager(getSupportFragmentManager());
        fragmentStartManager.registerFragment(RecordFragment.class, R.id.full_fragment_content, onRecordItemSelected);
        fragmentStartManager.registerFragment(SettingFragment.class, R.id.full_fragment_content, null);
        fragmentStartManager.registerDialogFragment(NewGameDialog.class, onNewGameListener);

        fragmentStartManager.resetListener();

        UMGameAgent.setDebugMode(BuildConfig.DEBUG);
        //noinspection deprecation
        MobclickAgent.updateOnlineConfig(this);
        UMGameAgent.init(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UMGameAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UMGameAgent.onPause(this);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().popBackStackImmediate()) {
            return;
        }

        if (System.currentTimeMillis() - lastBackPressed < BACK_TIME) {
            super.onBackPressed();
        } else {
            lastBackPressed = System.currentTimeMillis();
            Toast.makeText(this, R.string.press_again_exit, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        Utils.fixInputMethodManagerLeak(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_game:
                newGame();
                break;
            case R.id.read_record:
                fragmentStartManager.startFragment(RecordFragment.class,
                        RecordFragment.ARG_START_MODE, RecordFragment.MODE_READ);
                break;
            case R.id.setting:
                fragmentStartManager.startFragment(SettingFragment.class);
                break;
            case R.id.exit:
                finish();
                break;
            default:
                break;
        }
    }

    private void newGame() {
        if (GameHistory.haveAutoSave()) {
            fragmentStartManager.startFragment(NewGameDialog.class,
                    NewGameDialog.ARG_CONTENT_STRING, getString(R.string.new_game_tip));
        } else {
            startGame();
        }
    }

    private void startGame() {
        startActivity(new Intent(this, GameActivity.class));
        finish();
    }

    @Override
    public void popFragment() {
        getSupportFragmentManager().popBackStackImmediate();
    }

    private NewGameDialog.OnOkClickListener onNewGameListener
            = new NewGameDialog.OnOkClickListener() {
        @Override
        public void onClick() {
            getSupportFragmentManager().popBackStackImmediate();

            if (GameHistory.deleteAutoSave()) {
                startGame();
            } else {
                Toast.makeText(MainActivity.this, R.string.delete_fail, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private RecordFragment.OnRecordItemSelected onRecordItemSelected
            = new RecordFragment.OnRecordItemSelected() {
        @Override
        public void onSelected(int mode, String record) {
            popFragment();

            if (!TextUtils.equals(record, GameHistory.AUTO_SAVE)) {
                GameHistory.deleteRecord(GameHistory.AUTO_SAVE);
                GameHistory.copyRecord(record, GameHistory.AUTO_SAVE);
            }

            startGame();
        }
    };

}
