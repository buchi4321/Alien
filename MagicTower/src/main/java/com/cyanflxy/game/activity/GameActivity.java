package com.cyanflxy.game.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.Toast;

import com.cyanflxy.common.Utils;
import com.cyanflxy.game.bean.ImageInfoBean;
import com.cyanflxy.game.data.GameSharedPref;
import com.cyanflxy.game.dialog.BattleDialog;
import com.cyanflxy.game.driver.GameContext;
import com.cyanflxy.game.driver.OnGameProcessListener;
import com.cyanflxy.game.fragment.BaseFragment;
import com.cyanflxy.game.fragment.DialogueFragment;
import com.cyanflxy.game.fragment.EnemyPropertyFragment;
import com.cyanflxy.game.fragment.IntroduceFragment;
import com.cyanflxy.game.fragment.MenuFragment;
import com.cyanflxy.game.fragment.OnFragmentCloseListener;
import com.cyanflxy.game.fragment.RecordFragment;
import com.cyanflxy.game.fragment.SettingFragment;
import com.cyanflxy.game.widget.GameControllerView;
import com.cyanflxy.game.widget.HeroInfoView;
import com.cyanflxy.game.widget.MapView;
import com.github.cyanflxy.magictower.MainActivity;
import com.github.cyanflxy.magictower.R;
import com.umeng.analytics.game.UMGameAgent;

import java.util.List;

public class GameActivity extends FragmentActivity
        implements FragmentManager.OnBackStackChangedListener,
        OnFragmentCloseListener {

    private GameContext gameContext;
    private MapView mapView;
    private HeroInfoView heroInfoView;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        gameContext = GameContext.getInstance();

        Utils.setBrightness(this, GameSharedPref.getScreenLight());
        //noinspection ResourceType
        setRequestedOrientation(GameSharedPref.getScreenOrientation());

        setContentView(R.layout.activity_game);

        mapView = (MapView) findViewById(R.id.map_view);
        heroInfoView = (HeroInfoView) findViewById(R.id.hero_info_view);

        GameControllerView gc = GameControllerView.addGameController(this);
        gc.setListener(directionMotionListener);

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        loadGameContext();
        resetFragmentCallback();

        UMGameAgent.init(this);
    }

    private void loadGameContext() {
        gameContext.setGameListener(gameProcessListener);

        mapView.setGameContext(gameContext);

        heroInfoView.setGameContext(gameContext);
        heroInfoView.setOnFunctionClickListener(onFunctionClickListener);

        if (!TextUtils.isEmpty(gameContext.getIntroduce())) {
            String btnString = getString(R.string.continue_game);
            showIntroduceFragment(gameContext.getIntroduce(), btnString);
            gameContext.setIntroduceShown();
            gameContext.autoSave();
        }
    }

    private void resetFragmentCallback() {
        FragmentManager fm = getSupportFragmentManager();

        MenuFragment menuFragment = (MenuFragment) fm.findFragmentByTag(MenuFragment.TAG);
        if (menuFragment != null) {
            menuFragment.setOnMenuClickListener(onMenuClickListener);
        }

        RecordFragment recordFragment = (RecordFragment) fm.findFragmentByTag(RecordFragment.TAG);
        if (recordFragment != null) {
            recordFragment.setRecordItemSelected(onRecordItemSelected);
        }

        BattleDialog battleDialog = (BattleDialog) fm.findFragmentByTag(BattleDialog.TAG);
        if (battleDialog != null) {
            battleDialog.setOnBattleEndListener(onBattleEndListener);
        }
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

        showMenuFragment();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU
                && event.getAction() == KeyEvent.ACTION_DOWN) {

            Fragment f = getCurrentTopFragment();
            if (f == null) {
                showMenuFragment();
            } else if (f instanceof MenuFragment) {
                closeFragment(f);
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void closeFragment(Fragment f) {
        getSupportFragmentManager().popBackStackImmediate();
        heroInfoView.refreshInfo();
    }

    private void closeAllFragment() {
        //noinspection StatementWithEmptyBody
        while (getSupportFragmentManager().popBackStackImmediate()) {
            // non
        }
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
        String tag = IntroduceFragment.TAG;

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

    private void showMenuFragment() {
        String tag = MenuFragment.TAG;

        FragmentManager fm = getSupportFragmentManager();
        MenuFragment fragment = (MenuFragment) fm.findFragmentByTag(tag);

        if (fragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            fragment = new MenuFragment();
            fragment.setOnMenuClickListener(onMenuClickListener);

            ft.add(R.id.full_fragment_content, fragment, tag);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    private void showRecordFragment(int mode) {

        String tag = RecordFragment.TAG;

        FragmentManager fm = getSupportFragmentManager();
        RecordFragment fragment = (RecordFragment) fm.findFragmentByTag(tag);

        if (fragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            fragment = RecordFragment.newInstance(mode);
            fragment.setRecordItemSelected(onRecordItemSelected);

            ft.replace(R.id.full_fragment_content, fragment, tag);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    private void showSettingFragment() {
        String tag = SettingFragment.TAG;

        FragmentManager fm = getSupportFragmentManager();
        SettingFragment fragment = (SettingFragment) fm.findFragmentByTag(tag);

        if (fragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            fragment = new SettingFragment();

            ft.add(R.id.full_fragment_content, fragment, tag);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    private void showDialogueFragment() {
        String tag = DialogueFragment.TAG;

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

    private void showBattleFragment(ImageInfoBean enemy) {
        if (GameSharedPref.isShowFightView()) {
            String tag = BattleDialog.TAG;

            FragmentManager fm = getSupportFragmentManager();
            BattleDialog dialog = (BattleDialog) fm.findFragmentByTag(tag);

            if (dialog == null) {
                dialog = BattleDialog.newInstance(enemy);
                dialog.setOnBattleEndListener(onBattleEndListener);
                dialog.show(fm, tag);
            }
        } else {
            onBattleEndListener.onBattleEnd();
        }

    }

    private void showEnemyPropertyFragment() {
        String tag = EnemyPropertyFragment.TAG;

        FragmentManager fm = getSupportFragmentManager();
        EnemyPropertyFragment fragment = (EnemyPropertyFragment) fm.findFragmentByTag(tag);

        if (fragment == null) {
            fragment = new EnemyPropertyFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.full_fragment_content, fragment, tag);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    private void showFlyFragment() {

    }

    // 方向控制器回调
    private GameControllerView.MotionListener directionMotionListener
            = new GameControllerView.MotionListener() {
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

    };

    // 游戏进程回调
    private OnGameProcessListener gameProcessListener
            = new OnGameProcessListener() {

        @Override
        public void showDialogue() {
            showDialogueFragment();
        }

        @Override
        public void openDoor(int x, int y, String doorName) {
            mapView.openDoor(x, y, doorName);
        }

        @Override
        public void changeFloor(int floor) {
            mapView.changeFloor();
        }

        @Override
        public void showBattle(ImageInfoBean enemy) {
            showBattleFragment(enemy);
        }
    };

    // 菜单Fragment回调
    private MenuFragment.OnMenuClickListener onMenuClickListener
            = new MenuFragment.OnMenuClickListener() {
        @Override
        public void onMainMenu() {
            endGame();
        }

        @Override
        public void onReadRecord() {
            showRecordFragment(RecordFragment.MODE_READ);
        }

        @Override
        public void onSaveRecord() {
            showRecordFragment(RecordFragment.MODE_SAVE);
        }

        @Override
        public void onSetting() {
            showSettingFragment();
        }

    };

    private RecordFragment.OnRecordItemSelected onRecordItemSelected
            = new RecordFragment.OnRecordItemSelected() {
        @Override
        public void onSelected(int mode, String record) {
            closeAllFragment();

            if (mode == RecordFragment.MODE_READ) {
                gameContext.readRecord(record);
                mapView.newMap();
                heroInfoView.refreshInfo();
            } else if (mode == RecordFragment.MODE_SAVE) {
                if (gameContext.save(record)) {
                    Toast.makeText(GameActivity.this, R.string.save_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GameActivity.this, R.string.save_fail, Toast.LENGTH_SHORT).show();
                }
            }

        }
    };

    private BattleDialog.OnBattleEndListener onBattleEndListener
            = new BattleDialog.OnBattleEndListener() {
        @Override
        public void onBattleEnd() {
            gameContext.onBattleEnd();
            heroInfoView.refreshInfo();
        }
    };

    private HeroInfoView.OnFunctionClickListener onFunctionClickListener
            = new HeroInfoView.OnFunctionClickListener() {
        @Override
        public void onEnemyProperty() {
            showEnemyPropertyFragment();
        }

        @Override
        public void onJumpFloor() {
            showFlyFragment();
        }
    };
}
