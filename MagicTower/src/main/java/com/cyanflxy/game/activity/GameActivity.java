package com.cyanflxy.game.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.cyanflxy.common.Utils;
import com.cyanflxy.game.bean.EnemyProperty;
import com.cyanflxy.game.bean.ImageInfoBean;
import com.cyanflxy.game.bean.ShopBean;
import com.cyanflxy.game.data.GameSharedPref;
import com.cyanflxy.game.dialog.BattleDialog;
import com.cyanflxy.game.driver.GameContext;
import com.cyanflxy.game.driver.OnGameProcessListener;
import com.cyanflxy.game.fragment.BaseFragment;
import com.cyanflxy.game.fragment.DialogueFragment;
import com.cyanflxy.game.fragment.EnemyPropertyFragment;
import com.cyanflxy.game.fragment.FlyFragment;
import com.cyanflxy.game.fragment.IntroduceFragment;
import com.cyanflxy.game.fragment.MenuFragment;
import com.cyanflxy.game.fragment.RecordFragment;
import com.cyanflxy.game.fragment.SettingFragment;
import com.cyanflxy.game.fragment.ShopFragment;
import com.cyanflxy.game.fragment.ShopShortcutFragment;
import com.cyanflxy.game.record.GameHistory;
import com.cyanflxy.game.widget.GameControllerView;
import com.cyanflxy.game.widget.HeroInfoView;
import com.cyanflxy.game.widget.MapView;
import com.cyanflxy.game.widget.MessageToast;
import com.github.cyanflxy.magictower.BuildConfig;
import com.github.cyanflxy.magictower.MainActivity;
import com.github.cyanflxy.magictower.R;
import com.umeng.analytics.game.UMGameAgent;

import java.util.List;

public class GameActivity extends FragmentActivity
        implements BaseFragment.OnFragmentCloseListener {

    private GameContext gameContext;
    private MapView mapView;
    private HeroInfoView heroInfoView;

    private FragmentStartManager fragmentStartManager;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        gameContext = GameContext.getInstance();
        gameContext.setGameListener(gameProcessListener);

        Utils.setBrightness(this, GameSharedPref.getScreenLight());
        //noinspection ResourceType
        setRequestedOrientation(GameSharedPref.getScreenOrientation());

        setContentView(R.layout.activity_game);

        mapView = (MapView) findViewById(R.id.map_view);
        heroInfoView = (HeroInfoView) findViewById(R.id.hero_info_view);

        final View invincible = findViewById(R.id.invincible);
        if (BuildConfig.DEBUG) {
            invincible.setVisibility(View.VISIBLE);
            invincible.setSelected(GameSharedPref.isMapInvisible());
            invincible.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean newValue = !GameSharedPref.isMapInvisible();
                    GameSharedPref.setMapInvisible(newValue);
                    invincible.setSelected(newValue);
                }
            });
        }

        GameControllerView gc = GameControllerView.addGameController(this);
        gc.setListener(directionMotionListener);

        fragmentStartManager = new FragmentStartManager(getSupportFragmentManager());

        fragmentStartManager.registerFragment(IntroduceFragment.class, R.id.full_fragment_content, null);
        fragmentStartManager.registerFragment(MenuFragment.class, R.id.full_fragment_content, onMenuClickListener);
        fragmentStartManager.registerFragment(RecordFragment.class, R.id.full_fragment_content, onRecordItemSelected);
        fragmentStartManager.registerFragment(SettingFragment.class, R.id.full_fragment_content, null);
        fragmentStartManager.registerFragment(DialogueFragment.class, R.id.bottom_half_content, onDialogueEndListener);
        fragmentStartManager.registerFragment(EnemyPropertyFragment.class, R.id.full_fragment_content, null);
        fragmentStartManager.registerFragment(FlyFragment.class, R.id.full_fragment_content, onMapSelectListener);
        fragmentStartManager.registerFragment(ShopFragment.class, R.id.shop_content, onAttributeChangeListener);
        fragmentStartManager.registerFragment(ShopShortcutFragment.class, R.id.shop_content, onAttributeChangeListener);
        fragmentStartManager.registerDialogFragment(BattleDialog.class, onBattleEndListener);

        fragmentStartManager.resetListener();

        loadGameContext();
        UMGameAgent.init(this);
    }

    private void loadGameContext() {
        mapView.setGameContext(gameContext);

        heroInfoView.setGameContext(gameContext);
        heroInfoView.setOnFunctionClickListener(onFunctionClickListener);

        if (!TextUtils.isEmpty(gameContext.getIntroduce())) {
            String btnString = getString(R.string.continue_game);

            fragmentStartManager.startFragment(IntroduceFragment.class,
                    IntroduceFragment.ARG_INFO_STRING, gameContext.getIntroduce(),
                    IntroduceFragment.ARG_BTN_STRING, btnString);

            gameContext.setIntroduceShown();
            gameContext.autoSave();
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
    public void onBackPressed() {

        BaseFragment f = getCurrentTopFragment();
        if (f != null) {
            if (!f.onBackPress()) {
                popFragment();
            }
            return;
        }

        fragmentStartManager.startFragment(MenuFragment.class);
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

    @Override
    public void popFragment() {
        getSupportFragmentManager().popBackStackImmediate();
        heroInfoView.refreshInfo();

        if (gameContext.isFinish()) {// 游戏结束，退出游戏
            if (TextUtils.isEmpty(gameContext.getFinishString())) {
                endGame();
            } else {
                showFinishFragment();
            }
        }
    }

    private void closeAllFragment() {
        //noinspection StatementWithEmptyBody
        while (getSupportFragmentManager().popBackStackImmediate()) {
            // non
        }
    }

    private void endGame() {
        // 只有真正退出游戏的时候才需要干掉游戏实例
        mapView.onDestroy();
        GameContext.destroyInstance();

        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    private void showFinishFragment() {
        if (!TextUtils.isEmpty(gameContext.getFinishString())) {
            String btnString = getString(R.string.finish_game);

            fragmentStartManager.startFragment(IntroduceFragment.class,
                    IntroduceFragment.ARG_INFO_STRING, gameContext.getFinishString(),
                    IntroduceFragment.ARG_BTN_STRING, btnString);

            gameContext.setFinishShown();
        }
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
            fragmentStartManager.startFragment(DialogueFragment.class);
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
            EnemyProperty property = new EnemyProperty(enemy);
            fragmentStartManager.startFragment(BattleDialog.class, BattleDialog.ARG_ENEMY, property);
        }

        @Override
        public void showShop(ShopBean shopBean) {
            fragmentStartManager.startFragment(ShopFragment.class, ShopFragment.ARG_SHOP_BEAN, shopBean);
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
            fragmentStartManager.startFragment(RecordFragment.class,
                    RecordFragment.ARG_START_MODE, RecordFragment.MODE_READ);
        }

        @Override
        public void onSaveRecord() {
            fragmentStartManager.startFragment(RecordFragment.class,
                    RecordFragment.ARG_START_MODE, RecordFragment.MODE_SAVE);
        }

        @Override
        public void onSetting() {
            fragmentStartManager.startFragment(SettingFragment.class);
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
            fragmentStartManager.startFragment(EnemyPropertyFragment.class);
        }

        @Override
        public void onJumpFloor() {
            if (gameContext.getCurrentMap().cannotFly) {
                MessageToast.showText(R.string.cannot_fly);
            } else {
                fragmentStartManager.startFragment(FlyFragment.class);
            }
        }

        @Override
        public void onShopShortcut() {
            if (GameHistory.haveShop()) {
                fragmentStartManager.startFragment(ShopShortcutFragment.class);
            } else {
                MessageToast.showText(R.string.no_shop_shortcut);
            }
        }
    };

    private FlyFragment.OnMapSelectListener onMapSelectListener
            = new FlyFragment.OnMapSelectListener() {
        @Override
        public void onMapSelect(int mapFloor) {
            if (gameContext.jumpFloor(mapFloor)) {
                popFragment();
            }
        }
    };

    private ShopFragment.OnAttributeChangeListener onAttributeChangeListener
            = new ShopFragment.OnAttributeChangeListener() {
        @Override
        public void onAttributeChange() {
            heroInfoView.refreshInfo();
        }
    };

    private DialogueFragment.OnDialogueEndListener onDialogueEndListener
            = new DialogueFragment.OnDialogueEndListener() {
        @Override
        public void onDialogueEnd() {
            gameContext.onDialogueEnd();
        }
    };
}
