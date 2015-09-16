package com.cyanflxy.game.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.cyanflxy.game.driver.GameContext;
import com.cyanflxy.game.fragment.IntroduceFragment;
import com.github.cyanflxy.magictower.R;

public class GameActivity extends FragmentActivity {

    public static final String GAME_FILE = "game_file";

    private GameContext gameContext;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_game);

        String gameRecord = getIntent().getStringExtra(GAME_FILE);
        gameContext = GameContext.getInstance(gameRecord);

        if (!TextUtils.isEmpty(gameContext.getIntroduce())) {
            String btnString = getString(R.string.continue_game);
            showIntroduceFragment(gameContext.getIntroduce(), btnString);
        }

    }

    private void showIntroduceFragment(String info, String btnString) {
        String introduceTag = IntroduceFragment.class.getSimpleName();

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(introduceTag);

        if (fragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            fragment = IntroduceFragment.newInstance(info, btnString);
            ft.add(R.id.full_fragment_content, fragment, introduceTag);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

}
