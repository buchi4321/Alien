package com.github.cyanflxy.magictower;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.cyanflxy.common.CommFragmentDialog;
import com.cyanflxy.game.activity.GameActivity;
import com.cyanflxy.game.record.GameHistory;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private static final long BACK_TIME = 1000;

    private long lastBackPressed;

    private View contentView;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        contentView = findViewById(R.id.content_view);

        findViewById(R.id.new_game).setOnClickListener(this);
        findViewById(R.id.read_record).setOnClickListener(this);
        findViewById(R.id.setting).setOnClickListener(this);
        findViewById(R.id.help).setOnClickListener(this);
        findViewById(R.id.exit).setOnClickListener(this);

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_game:
                newGame();
                break;
            case R.id.read_record:
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

            final CommFragmentDialog dialog = CommFragmentDialog.newInstance(getString(R.string.new_game_tip));
            dialog.setOnOkClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (GameHistory.deleteAutoSave()) {
                        startGame();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(MainActivity.this, R.string.delete_fail, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            dialog.show(getSupportFragmentManager(), "NewGameDialog");

        } else {
            startGame();
        }
    }

    private void startGame() {
        startActivity(new Intent(this, GameActivity.class));
        finish();
    }

}
