package com.cyanflxy.game.activity;

import android.app.Activity;
import android.os.Bundle;

import com.github.cyanflxy.magictower.R;

public class GameActivity extends Activity {


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_game);
    }
}
