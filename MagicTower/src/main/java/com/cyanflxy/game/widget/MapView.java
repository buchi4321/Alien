package com.cyanflxy.game.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.cyanflxy.game.bean.HeroPositionBean;
import com.cyanflxy.game.bean.ImageInfoBean;
import com.cyanflxy.game.bean.MapBean;
import com.cyanflxy.game.bean.MapElementBean;
import com.cyanflxy.game.driver.GameContext;
import com.cyanflxy.game.driver.ImageResourceManager;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class MapView extends View {

    private static final int ANIMATION_DURATION = 500;
    private static final int OPEN_DOOR_DURATION = 70;
    public static final int HERO_MOVE_DURATION = 100;

    private int widthPiece = 11;
    private int heightPiece = 11;
    private float pieceSize;

    private RectF imageRect;
    private GameContext gameContext;
    private ImageResourceManager imageResourceManager;
    private Bitmap floorBitmap;

    private int mapAnimatePhase;
    private Handler animateHandler;

    // 主角移动处理,数据定义
    private float heroMoveDx;
    private float heroMoveDy;
    private int heroAnimatePhase;
    private HeroPositionBean currentPosition;
    private BlockingQueue<HeroPositionBean> heroSteps;
    private ExecutorService threadPool;
    private Lock heroPositionLock;
    private Lock heroMoveStepLock;
    private Condition heroMoveSignal;

    // 开门动画数据
    private int doorX;
    private int doorY;
    private ImageInfoBean doorInfo;
    private int doorPhase;

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.BLACK);

        imageRect = new RectF();

        animateHandler = new AnimateHandler(this);
        animateHandler.sendEmptyMessage(MSG_MAP_ANIMATION);

        heroPositionLock = new ReentrantLock();
        heroMoveStepLock = new ReentrantLock();
        heroMoveSignal = heroMoveStepLock.newCondition();

        threadPool = Executors.newCachedThreadPool();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        pieceSize = Math.min(width / (float) widthPiece, height / (float) heightPiece);
        float fWidth = pieceSize * widthPiece;
        float fHeight = pieceSize * heightPiece;
        widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) fWidth, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) fHeight, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onDetachedFromWindow() {
        threadPool.shutdown();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (gameContext == null) {
            return;
        }

        onMapChange();

        MapBean map = gameContext.getCurrentMap();

        int imageIndex = 0;
        for (int row = 0; row < heightPiece; row++) {
            for (int col = 0; col < widthPiece; col++) {

                String imageName = map.mapData[imageIndex].element;
                canvas.drawBitmap(getBitmap(imageName), null, getImageRect(row, col), null);

                imageIndex++;

            }
        }

        // 绘制开门动画
        if (doorInfo != null && doorPhase < doorInfo.getIdLength()) {
            int id = doorInfo.getId(doorPhase);
            Bitmap bitmap = imageResourceManager.getBitmap(id);
            imageRect = getImageRectXY(doorX, doorY);
            canvas.drawBitmap(bitmap, null, imageRect, null);
        }

        // 绘制主角或移动动画
        HeroPositionBean p;
        int phase;
        float dx;
        float dy;

        heroPositionLock.lock();
        try {
            p = currentPosition;
            phase = heroAnimatePhase;
            dx = heroMoveDx;
            dy = heroMoveDy;
        } finally {
            heroPositionLock.unlock();
        }

        if (phase < 0) {
            phase = 0;
        }

        imageRect = getImageRectXY(p.x, p.y);
        imageRect.offset(dx * phase, dy * phase);

        Bitmap bitmap = getHeroBitmap(p.direction, phase);
        canvas.drawBitmap(bitmap, null, imageRect, null);
    }

    public void setGameContext(GameContext gameContext) {
        this.gameContext = gameContext;

        imageResourceManager = gameContext.getImageResourceManager();

        MapBean map = gameContext.getCurrentMap();
        int id = imageResourceManager.getImage(map.floorImage).id;
        floorBitmap = imageResourceManager.getBitmap(id);

        HeroPositionBean p = gameContext.getHeroPosition();
        currentPosition = p.copy();

        Runnable heroMove = new HeroMoveThread(p);
        threadPool.execute(heroMove);
    }

    public void openDoor(int x, int y, MapElementBean doorElement) {
        doorX = x;
        doorY = y;
        doorInfo = imageResourceManager.getImage(doorElement.element);
        doorPhase = 0;

        invalidate();
        animateHandler.sendEmptyMessageDelayed(MSG_OPEN_DOOR, OPEN_DOOR_DURATION);
    }

    private void onMapChange() {
        MapBean map = gameContext.getCurrentMap();

        int wp = map.mapWidth;
        int hp = map.mapHeight;

        if (wp != widthPiece || hp != heightPiece) {
            requestLayout();
        }
    }

    private RectF getImageRectXY(int x, int y) {
        return getImageRect(y, x);
    }

    private RectF getImageRect(int row, int col) {
        float left = col * pieceSize;
        float top = row * pieceSize;
        //右下延伸1个像素，避免图片中出现间隙的情况
        imageRect.set(left, top, left + pieceSize + 1, top + pieceSize + 1);
        return imageRect;
    }

    private Bitmap getBitmap(String imageName) {
        if (TextUtils.isEmpty(imageName)) {
            return floorBitmap;
        } else {
            ImageInfoBean info = imageResourceManager.getImage(imageName);

            if ("door".equals(info.type)) {
                return imageResourceManager.getBitmap(info.ids[0]);
            } else if (info.ids != null && info.ids.length > 0) {
                int index = mapAnimatePhase % info.ids.length;
                return imageResourceManager.getBitmap(info.ids[index]);
            } else {
                return imageResourceManager.getBitmap(info.id);
            }
        }
    }

    private Bitmap getHeroBitmap(HeroPositionBean.Direction d, int phase) {
        String name = d.name();
        ImageInfoBean info = imageResourceManager.getImage(name);
        if (info.ids != null && info.ids.length > 0) {
            int index = phase % info.ids.length;
            return imageResourceManager.getBitmap(info.ids[index]);
        } else {
            return imageResourceManager.getBitmap(info.id);
        }
    }

    // 主角移动处理代码
    public void checkMove() {
        HeroPositionBean p = gameContext.getHeroPosition();

        Runnable heroPosition = new HeroMovePutThread(p);
        threadPool.execute(heroPosition);

    }

    private class HeroMoveThread implements Runnable {

        private HeroPositionBean lastPosition;

        public HeroMoveThread(HeroPositionBean lastPosition) {
            heroSteps = new ArrayBlockingQueue<>(5);
            this.lastPosition = lastPosition.copy();
        }

        @Override
        public void run() {
            HeroPositionBean p;

            while (true) {
                try {
                    p = heroSteps.take();
                } catch (InterruptedException e) {
                    return;
                }

                if (p.equals(lastPosition)) {
                    continue;
                }

                heroPositionLock.lock();
                try {
                    heroAnimatePhase = 0;
                    currentPosition = lastPosition;
                    currentPosition.direction = p.direction;

                    float stepLen = pieceSize / imageResourceManager.getHeroMoveStep();

                    switch (p.direction) {
                        case left:
                            heroMoveDx = -stepLen;
                            heroMoveDy = 0;
                            break;
                        case right:
                            heroMoveDx = stepLen;
                            heroMoveDy = 0;
                            break;
                        case up:
                            heroMoveDx = 0;
                            heroMoveDy = -stepLen;
                            break;
                        case down:
                            heroMoveDx = 0;
                            heroMoveDy = stepLen;
                            break;
                    }

                } finally {
                    heroPositionLock.unlock();
                }

                postInvalidate();

                Message msg = animateHandler.obtainMessage(MSG_HERO_ANIMATION, p.x, p.y);
                animateHandler.sendMessageDelayed(msg, HERO_MOVE_DURATION);

                lastPosition = p.copy();

                heroMoveStepLock.lock();
                try {
                    while (heroAnimatePhase != -1) {
                        try {
                            heroMoveSignal.await();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                } finally {
                    heroMoveStepLock.unlock();
                }
            }
        }
    }

    private class HeroMovePutThread implements Runnable {

        private HeroPositionBean position;

        public HeroMovePutThread(HeroPositionBean p) {
            position = p.copy();
        }

        @Override
        public void run() {
            try {
                heroSteps.put(position);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    private static final int MSG_MAP_ANIMATION = 1;
    private static final int MSG_HERO_ANIMATION = 2;
    private static final int MSG_OPEN_DOOR = 3;

    private static class AnimateHandler extends Handler {

        private Reference<MapView> mapViewRef;

        public AnimateHandler(MapView mapView) {
            mapViewRef = new WeakReference<>(mapView);
        }

        @Override
        public void handleMessage(Message msg) {
            MapView mapView = mapViewRef.get();
            if (mapView == null) {
                return;
            }

            switch (msg.what) {
                case MSG_MAP_ANIMATION:
                    mapView.mapAnimatePhase++;
                    sendEmptyMessageDelayed(MSG_MAP_ANIMATION, ANIMATION_DURATION);
                    break;
                case MSG_HERO_ANIMATION:
                    mapView.heroAnimatePhase++;
                    if (mapView.heroAnimatePhase < mapView.imageResourceManager.getHeroMoveStep()) {
                        Message msg2 = obtainMessage(MSG_HERO_ANIMATION, msg.arg1, msg.arg2);
                        sendMessageDelayed(msg2, HERO_MOVE_DURATION);
                    } else {
                        mapView.heroAnimatePhase = -1;
                        mapView.currentPosition.x = msg.arg1;
                        mapView.currentPosition.y = msg.arg2;

                        mapView.heroMoveStepLock.lock();
                        try {
                            mapView.heroMoveSignal.signalAll();
                        } finally {
                            mapView.heroMoveStepLock.unlock();
                        }
                    }
                    break;
                case MSG_OPEN_DOOR:
                    mapView.doorPhase++;
                    if (mapView.doorPhase < mapView.doorInfo.getIdLength()) {
                        sendEmptyMessageDelayed(MSG_OPEN_DOOR, OPEN_DOOR_DURATION);
                    }
                    break;
                default:
                    break;
            }

            mapView.invalidate();

        }
    }

}
