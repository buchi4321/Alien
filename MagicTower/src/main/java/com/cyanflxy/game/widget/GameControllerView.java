package com.cyanflxy.game.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import com.cyanflxy.common.Utils;
import com.cyanflxy.game.bean.Direction;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

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
        void move(Direction d);
    }

    private static final int ACTION_INTERVAL = 500;

    private static final int EFFECTIVE_MOTION_DP = 50;
    private static final float DIRECTION_PROPORTION = 2;// 方向确认比例

    private final float effectiveDistance;
    private boolean isInterval;

    private float lastActionX;
    private float lastActionY;
    private Direction lastDirection;

    private float touchX;
    private float touchY;

    private MotionListener listener;
    private LocalHandler handler;

    public GameControllerView(Context context) {
        super(context);
        effectiveDistance = Utils.dip2px(EFFECTIVE_MOTION_DP);
        handler = new LocalHandler(this);

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
        touchX = ev.getX();
        touchY = ev.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastActionX = touchX;
                lastActionY = touchY;
                isInterval = false;
                lastDirection = null;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isInterval) {
                    checkMotion();
                }
                break;
            case MotionEvent.ACTION_UP:
                handler.stop();
                lastDirection = null;
                break;
        }

    }

    private void checkMotion() {
        isInterval = false;
        float dx = touchX - lastActionX;
        float dy = touchY - lastActionY;
        float distance = (float) Math.hypot(dx, dy);
        if (distance < effectiveDistance) {
            //继续上次的移动方向
            if (lastDirection != null) {
                onMoveConfirm(lastDirection, true);
            }
            return;
        }

        float proportion = Math.abs(dx) / Math.abs(dy);

        if (proportion > DIRECTION_PROPORTION) {

            if (dx > 0) {
                onMoveConfirm(Direction.right, false);
            } else {
                onMoveConfirm(Direction.left, false);
            }

        } else if (proportion < 1 / DIRECTION_PROPORTION) {

            if (dy > 0) {
                onMoveConfirm(Direction.down, false);
            } else {
                onMoveConfirm(Direction.up, false);
            }
        }
    }

    private void onMoveConfirm(Direction d, boolean isLast) {
        if (listener != null) {
            listener.move(d);
        }

        if (!isLast) {
            lastActionX = touchX;
            lastActionY = touchY;
            lastDirection = d;
        }

        isInterval = true;
        handler.sendEmptyMessageDelayed(0, ACTION_INTERVAL);
    }

    private static class LocalHandler extends Handler {

        private Reference<GameControllerView> controllerReference;

        public LocalHandler(GameControllerView controllerView) {
            controllerReference = new WeakReference<>(controllerView);
        }

        public void stop() {
            if (hasMessages(0)) {
                removeMessages(0);
            }
        }

        @Override
        public void handleMessage(Message msg) {
            GameControllerView view = controllerReference.get();
            if (view == null) {
                return;
            }

            view.checkMotion();
        }
    }

}
