package com.github.cyanflxy.magictower;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.cyanflxy.common.Utils;
import com.cyanflxy.game.activity.GameActivity;
import com.cyanflxy.game.dialog.NewGameDialog;
import com.cyanflxy.game.fragment.OnFragmentCloseListener;
import com.cyanflxy.game.fragment.RecordFragment;
import com.cyanflxy.game.record.GameHistory;

public class MainActivity extends FragmentActivity implements View.OnClickListener,
        OnFragmentCloseListener {

    private static final long BACK_TIME = 1000;

    private long lastBackPressed;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);

        findViewById(R.id.new_game).setOnClickListener(this);
        findViewById(R.id.read_record).setOnClickListener(this);
        findViewById(R.id.setting).setOnClickListener(this);
        findViewById(R.id.help).setOnClickListener(this);
        findViewById(R.id.exit).setOnClickListener(this);

        resetFragmentCallback();
    }

    private void resetFragmentCallback() {
        FragmentManager fm = getSupportFragmentManager();

        NewGameDialog dialog = (NewGameDialog) fm.findFragmentByTag(NewGameDialog.TAG);
        if (dialog != null) {
            dialog.setOnOkClickListener(onNewGameListener);
        }

        RecordFragment recordFragment = (RecordFragment) fm.findFragmentByTag(RecordFragment.TAG);
        if (recordFragment != null) {
            recordFragment.setRecordItemSelected(onRecordItemSelected);
        }

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
                showRecordFragment();
                break;
            case R.id.setting:
                break;
            case R.id.help:
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

            final NewGameDialog dialog = NewGameDialog.newInstance(getString(R.string.new_game_tip));
            dialog.setOnOkClickListener(onNewGameListener);

            dialog.show(getSupportFragmentManager(), NewGameDialog.TAG);

        } else {
            startGame();
        }
    }

    private void startGame() {
        startActivity(new Intent(this, GameActivity.class));
        finish();
    }

    private void showRecordFragment() {
        String tag = RecordFragment.TAG;

        FragmentManager fm = getSupportFragmentManager();
        RecordFragment fragment = (RecordFragment) fm.findFragmentByTag(tag);

        if (fragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            fragment = RecordFragment.newInstance(RecordFragment.MODE_READ);
            fragment.setRecordItemSelected(onRecordItemSelected);

            ft.add(R.id.full_fragment_content, fragment, tag);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    @Override
    public void closeFragment(Fragment f) {
        getSupportFragmentManager().popBackStackImmediate();
    }

    private View.OnClickListener onNewGameListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
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
            closeFragment(null);

            if (!TextUtils.equals(record, GameHistory.AUTO_SAVE)) {
                GameHistory.deleteRecord(GameHistory.AUTO_SAVE);
                GameHistory.copyRecord(record, GameHistory.AUTO_SAVE);
            }

            startGame();
        }
    };
}
