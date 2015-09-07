package com.github.cyanflxy.magictower;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.cyanflxy.common.CommDialog;
import com.cyanflxy.game.record.GameHistory;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.new_game).setOnClickListener(this);
        findViewById(R.id.read_memory).setOnClickListener(this);
        findViewById(R.id.setting).setOnClickListener(this);
        findViewById(R.id.help).setOnClickListener(this);
        findViewById(R.id.exit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_game:
                newGame();
                break;
            case R.id.read_memory:
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
            CommDialog dialog = new CommDialog(this);
            dialog.setText(R.string.new_game_tip);
            dialog.setOnOkClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GameHistory.deleteAutoSave();
                    startGame();
                }
            });
            dialog.show();
        } else {
            startGame();
        }
    }

    private void startGame() {

    }
}
