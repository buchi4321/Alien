package com.cyanflxy.game.widget;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import com.cyanflxy.common.Utils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class GameControllerView extends FrameLayout implements View.OnTouchListener {

    public static GameControllerView addGameController(Activity activity) {
        Window window = activity.getWindow();
        ViewGroup decorView = (ViewGroup) window.getDecorView();
        View subView = decorView.getChildAt(0);

        GameControllerView gc = new GameControllerView(activity);
        decorView.removeView(subView);
        decorView.addView(gc, MATCH_PARENT, MATCH_PARENT);
        gc.addView(subView, MATCH_PARENT, MATCH_PARENT);

        return gc;
    }

    public interface MotionListener {
        void onLeft();

        void onRight();

        void onUp();

        void onDown();
    }

    private static final int EFFECTIVE_MOTION_DP = 50;

    private final float EFFECTIVE_MOTION;
    private static final float DIRECTION_PROPORTION = 2;// 方向确认比例

    private float touchX;
    private float touchY;

    private MotionListener listener;

    public GameControllerView(Context context) {
        super(context);
        EFFECTIVE_MOTION = Utils.dip2px(EFFECTIVE_MOTION_DP);

        setOnTouchListener(this);
    }

    public void setListener(MotionListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        onTouch(event);
        return true;
    }

    private void onTouch(MotionEvent ev) {
        int action = ev.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                touchX = ev.getX();
                touchY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                checkMotion(ev.getX(), ev.getY());
                break;
        }

    }

    private void checkMotion(float x, float y) {
        if (listener == null) {
            return;
        }

        float dx = x - touchX;
        float dy = y - touchY;

        float absX = Math.abs(dx);
        float absY = Math.abs(dy);

        if (absX < EFFECTIVE_MOTION && absY < EFFECTIVE_MOTION) {
            return;
        }

        float proportion = absX / absY;

        if (proportion > DIRECTION_PROPORTION) {

            if (dx > 0) {
                listener.onRight();
            } else {
                listener.onLeft();
            }

        } else if (proportion < 1 / DIRECTION_PROPORTION) {
            if (dy > 0) {
                listener.onDown();
            } else {
                listener.onUp();
            }
        }
    }

}
