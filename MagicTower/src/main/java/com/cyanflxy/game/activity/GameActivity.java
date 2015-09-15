package com.cyanflxy.game.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.cyanflxy.game.bean.HeroBean;
import com.cyanflxy.game.bean.MapBean;
import com.cyanflxy.game.bean.ShopBean;
import com.cyanflxy.game.driver.GameContext;
import com.cyanflxy.game.driver.OnGameProgressListener;
import com.cyanflxy.game.driver.OnInfoCloseListener;
import com.cyanflxy.game.fragment.IntroduceFragment;
import com.github.cyanflxy.magictower.MainActivity;
import com.github.cyanflxy.magictower.R;

public class GameActivity extends FragmentActivity implements OnGameProgressListener {

    public static final String GAME_FILE = "game_file";

    private GameContext gameContext;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_game);

        String gameRecord = getIntent().getStringExtra(GAME_FILE);
        gameContext = new GameContext(gameRecord, this);

        if (!TextUtils.isEmpty(gameContext.getIntroduce())) {
            String btnString = getString(R.string.continue_game);
            showIntroduceFragment(gameContext.getIntroduce(),btnString);
        }

    }

    private void showIntroduceFragment(String info, String btnString) {
        String introduceTag = IntroduceFragment.class.getSimpleName();

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(introduceTag);

        if (fragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            fragment = IntroduceFragment.newInstance(info,btnString);
            ft.add(R.id.full_fragment_content, fragment, introduceTag);
            ft.addToBackStack(null);
            ft.commit();
        } else {
            Bundle bundle = new Bundle();
            bundle.putString(IntroduceFragment.ARG_INFO_STRING, info);
            bundle.putString(IntroduceFragment.ARG_BTN_STRING, btnString);
            fragment.setArguments(bundle);
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

    @Override
    public void onShowInfo(String info, OnInfoCloseListener listener) {

    }

    @Override
    public void onHeroStateChange(HeroBean hero) {

    }

    @Override
    public void onMapChane(MapBean map) {

    }

    @Override
    public void onShowShop(ShopBean shop) {

    }

    @Override
    public void onShowDialogue(String dialogue) {

    }

    @Override
    public void onGameEnd() {
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }
}
