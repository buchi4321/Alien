package com.cyanflxy.game.widget;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.github.cyanflxy.magictower.R;

import java.lang.reflect.Field;

import static com.github.cyanflxy.magictower.AppApplication.baseContext;

public class FightResultToast extends Toast {

    private static Toast instance;

    public static void initToast() {
        instance = new FightResultToast();
    }

    public static void show(int money, int exp) {
        instance.setText(baseContext.getString(R.string.fight_get,money,exp));
        instance.show();
    }


    private TextView toastText;

    private FightResultToast() {
        super(baseContext);

        int id = R.layout.__leak_canary_display_leak;

        LayoutInflater inflate = LayoutInflater.from(baseContext);
        View v = inflate.inflate(R.layout.toast_fight_result, null);
        toastText = (TextView) v.findViewById(R.id.message);

        setView(v);
        setDuration(LENGTH_SHORT);
        setGravity(Gravity.CENTER_VERTICAL, 0, 0);

        try {
            Field tnField = getClass().getSuperclass().getDeclaredField("mTN");
            tnField.setAccessible(true);
            Object tn = tnField.get(this);

            Field paramsField = tn.getClass().getDeclaredField("mParams");
            paramsField.setAccessible(true);

            WindowManager.LayoutParams params = (WindowManager.LayoutParams) paramsField.get(tn);
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.windowAnimations = 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setText(CharSequence str) {
        toastText.setText(str);
    }
}
