package com.cyanflxy.game.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.cyanflxy.game.bean.EnemyProperty;
import com.cyanflxy.game.bean.HeroBean;
import com.cyanflxy.game.driver.GameContext;
import com.cyanflxy.game.parser.SentenceParser;
import com.cyanflxy.game.sound.SoundUtil;
import com.cyanflxy.game.widget.BattleView;
import com.github.cyanflxy.magictower.R;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public class BattleDialog extends BaseDialogFragment {

    public static final String ARG_ENEMY = "enemy";

    public interface OnBattleEndListener extends OnDialogFragmentFunctionListener {
        void onBattleEnd();
    }

    private HeroBean hero;
    private EnemyProperty enemy;
    private BattleView battleView;
    private OnBattleEndListener listener;

    @Override
    public void setOnDialogFragmentFunctionListener(OnDialogFragmentFunctionListener l) {
        listener = (OnBattleEndListener) l;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GameContext gameContext = GameContext.getInstance();

        battleView = new BattleView(getActivity());
        battleView.setImageManager(gameContext.getImageResourceManager());

        hero = gameContext.getHero();
        enemy = (EnemyProperty) getArguments().getSerializable(ARG_ENEMY);
        battleView.setInfo(hero, enemy);

        Handler handler = new BattleHandler(this);
        handler.sendEmptyMessage(MSG_LIFE_DRAIN);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.common_dialog_style);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.setContentView(battleView);

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });

        return dialog;
    }

    private void onBattleEnd() {
        try {
            dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (listener != null) {
            listener.onBattleEnd();
        }
    }

    private static final int MSG_LIFE_DRAIN = 1;
    private static final int MSG_HIT_ENEMY = 2;
    private static final int MSG_HIT_HERO = 3;

    private static final int BATTLE_INTERVAL = 200;

    private static class BattleHandler extends Handler {

        private Reference<BattleDialog> dialogReference;
        private HeroBean hero;
        private EnemyProperty enemy;

        public BattleHandler(BattleDialog dialog) {
            dialogReference = new WeakReference<>(dialog);

            hero = dialog.hero;
            enemy = dialog.enemy;

        }

        @Override
        public void handleMessage(Message msg) {

            BattleDialog dialog = dialogReference.get();
            if (dialog == null) {
                return;
            }

            switch (msg.what) {
                case MSG_LIFE_DRAIN:
                    if (!TextUtils.isEmpty(enemy.lifeDrain)) {
                        int drain = SentenceParser.parseLifeDrain(hero.hp, enemy.lifeDrain);
                        hero.hp -= drain;
                    }
                    sendEmptyMessageDelayed(MSG_HIT_ENEMY, BATTLE_INTERVAL);
                    break;
                case MSG_HIT_ENEMY: {
                    int damage = hero.damage - enemy.defense;
                    enemy.hp -= damage;
                    if (enemy.hp <= 0) {
                        enemy.hp = 0;
                    } else {
                        SoundUtil.fight();
                    }

                    dialog.battleView.invalidate();
                    if (enemy.hp > 0) {
                        sendEmptyMessageDelayed(MSG_HIT_HERO, BATTLE_INTERVAL);
                    } else {
                        dialog.onBattleEnd();
                    }
                }
                break;
                case MSG_HIT_HERO: {
                    int damage = enemy.damage - hero.defense;
                    if (damage > 0) {
                        hero.hp -= damage;
                        dialog.battleView.invalidate();
                    }
                    sendEmptyMessageDelayed(MSG_HIT_ENEMY, BATTLE_INTERVAL);
                }
                break;
            }
        }
    }
}
