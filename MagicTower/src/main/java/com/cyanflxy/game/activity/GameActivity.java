package com.cyanflxy.game.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.cyanflxy.game.bean.HeroBean;
import com.cyanflxy.game.bean.MapBean;
import com.cyanflxy.game.bean.ShopBean;
import com.cyanflxy.game.driver.GameContext;
import com.cyanflxy.game.driver.OnGameProgressListener;
import com.cyanflxy.game.driver.OnInfoCloseListener;
import com.github.cyanflxy.magictower.MainActivity;
import com.github.cyanflxy.magictower.R;

public class GameActivity extends Activity implements OnGameProgressListener {

    public static final String GAME_FILE = "game_file";

    private GameContext gameContext;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_game);

        String gameRecord = getIntent().getStringExtra(GAME_FILE);
        gameContext = new GameContext(gameRecord, this);

        gameContext.start();
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
