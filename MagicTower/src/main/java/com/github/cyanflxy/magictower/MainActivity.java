package com.github.cyanflxy.magictower;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.cyanflxy.common.CommDialog;
import com.cyanflxy.game.activity.GameActivity;
import com.cyanflxy.game.record.GameHistory;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final long BACK_TIME = 1000;
    private static final String ARG_NEW_GAME_DIALOG_SHOWING = "new_game_dialog";

    private long lastBackPressed;
    private CommDialog newGameDialog;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);

        findViewById(R.id.new_game).setOnClickListener(this);
        findViewById(R.id.read_memory).setOnClickListener(this);
        findViewById(R.id.setting).setOnClickListener(this);
        findViewById(R.id.help).setOnClickListener(this);
        findViewById(R.id.exit).setOnClickListener(this);

        if (bundle != null) {
            if (bundle.getBoolean(ARG_NEW_GAME_DIALOG_SHOWING)) {
                newGame();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - lastBackPressed < BACK_TIME) {
            super.onBackPressed();
        } else {
            lastBackPressed = System.currentTimeMillis();
            Toast.makeText(this, R.string.press_again_exit, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (newGameDialog != null && newGameDialog.isShowing()) {
            newGameDialog.dismiss();
            outState.putBoolean(ARG_NEW_GAME_DIALOG_SHOWING, true);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_game:
                newGame();
                break;
            case R.id.read_memory:
                startGame();
                break;
            case R.id.setting:
                break;
            case R.id.help:
                break;
            case R.id.exit:
                finish();
                break;
        }
    }

    private void newGame() {
        if (GameHistory.haveAutoSave()) {

            if (newGameDialog == null) {
                newGameDialog = new CommDialog(this);
                newGameDialog.setText(R.string.new_game_tip);
                newGameDialog.setOnOkClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (GameHistory.deleteAutoSave()) {
                            startGame();
                            newGameDialog.dismiss();
                        } else {
                            Toast.makeText(MainActivity.this, R.string.delete_fail, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            newGameDialog.show();
        } else {
            startGame();
        }
    }

    private void startGame() {
        startActivity(new Intent(this, GameActivity.class));
        finish();
    }
}
