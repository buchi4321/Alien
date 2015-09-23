package com.cyanflxy.game.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.cyanflxy.game.driver.GameContext;
import com.cyanflxy.game.driver.OnGameProcessListener;
import com.cyanflxy.game.fragment.BaseFragment;
import com.cyanflxy.game.fragment.DialogueFragment;
import com.cyanflxy.game.fragment.IntroduceFragment;
import com.cyanflxy.game.fragment.OnFragmentCloseListener;
import com.cyanflxy.game.widget.GameControllerView;
import com.cyanflxy.game.widget.HeroInfoView;
import com.cyanflxy.game.widget.MapView;
import com.github.cyanflxy.magictower.MainActivity;
import com.github.cyanflxy.magictower.R;

import java.util.List;

public class GameActivity extends FragmentActivity
        implements FragmentManager.OnBackStackChangedListener,
        OnFragmentCloseListener, GameControllerView.MotionListener,
        OnGameProcessListener {

    private GameContext gameContext;
    private MapView mapView;
    private HeroInfoView heroInfoView;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        gameContext = GameContext.getInstance();
        gameContext.setGameListener(this);

        setContentView(R.layout.activity_game);

        mapView = (MapView) findViewById(R.id.map_view);
        mapView.setGameContext(gameContext);

        heroInfoView = (HeroInfoView) findViewById(R.id.hero_info_view);
        heroInfoView.setGameContext(gameContext);

        GameControllerView gc = GameControllerView.addGameController(this);
        gc.setListener(this);

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        if (!TextUtils.isEmpty(gameContext.getIntroduce())) {
            String btnString = getString(R.string.continue_game);
            showIntroduceFragment(gameContext.getIntroduce(), btnString);
            gameContext.setIntroduceShown();
        }
    }

    @Override
    protected void onDestroy() {
        getSupportFragmentManager().removeOnBackStackChangedListener(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        BaseFragment f = getCurrentTopFragment();
        if (f != null) {
            if (!f.onBackPress()) {
                closeFragment(f);
            }
            return;
        }

        // TODO 显示菜单

        endGame();
    }

    @Override
    public void closeFragment(Fragment f) {
        getSupportFragmentManager().popBackStackImmediate();
        heroInfoView.refreshInfo();
    }

    @Override
    public void onBackStackChanged() {
        if (getCurrentTopFragment() == null) {
            if (gameContext.isFinish()) {// 游戏结束，退出游戏
                endGame();
            }
        }
    }

    private BaseFragment getCurrentTopFragment() {

        FragmentManager fm = getSupportFragmentManager();
        List<Fragment> fragments = fm.getFragments();

        if (fragments != null && fragments.size() > 0) {
            for (int i = fragments.size() - 1; i >= 0; i--) {
                Fragment f = fragments.get(i);
                if (f != null && f.isVisible()) {
                    return (BaseFragment) f;
                }
            }
        }
        return null;
    }

    private void showFinishFragment() {
        if (!TextUtils.isEmpty(gameContext.getFinishString())) {
            String btnString = getString(R.string.finish_game);
            showIntroduceFragment(gameContext.getFinishString(), btnString);
            gameContext.setFinishShown();
        }
    }

    private void showIntroduceFragment(String info, String btnString) {
        String tag = BaseFragment.getFragmentTag(IntroduceFragment.class);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(tag);

        if (fragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            fragment = IntroduceFragment.newInstance(info, btnString);
            ft.add(R.id.full_fragment_content, fragment, tag);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    private void endGame() {
        // 只有真正退出游戏的时候才需要干掉游戏实例
        mapView.onDestroy();
        GameContext.destroyInstance();

        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onLeft() {
        if (getCurrentTopFragment() != null) {
            return;
        }

        gameContext.moveLeft();
        onMoveAction();
    }

    @Override
    public void onRight() {
        if (getCurrentTopFragment() != null) {
            return;
        }

        gameContext.moveRight();
        onMoveAction();
    }

    @Override
    public void onUp() {
        if (getCurrentTopFragment() != null) {
            return;
        }

        gameContext.moveUP();
        onMoveAction();
    }

    @Override
    public void onDown() {
        if (getCurrentTopFragment() != null) {
            return;
        }

        gameContext.moveDown();
        onMoveAction();
    }

    private void onMoveAction() {
        mapView.checkMove();
        heroInfoView.refreshInfo();
    }

    @Override
    public void showDialogue() {
        String tag = BaseFragment.getFragmentTag(DialogueFragment.class);

        FragmentManager fm = getSupportFragmentManager();
        DialogueFragment fragment = (DialogueFragment) fm.findFragmentByTag(tag);

        if (fragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            fragment = new DialogueFragment();
            ft.add(R.id.bottom_half_content, fragment, tag);
            ft.addToBackStack(null);
            ft.commit();
        }
    }
}
