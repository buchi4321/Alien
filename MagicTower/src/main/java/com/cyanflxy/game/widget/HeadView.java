package com.cyanflxy.game.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.cyanflxy.game.bean.ImageInfoBean;
import com.cyanflxy.game.driver.ImageResourceManager;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;

public class HeadView extends ImageView {

    private static final int ANIMATION_DURATION = MapView.ANIMATION_DURATION;

    private ImageInfoBean info;
    private ImageResourceManager imageManager;

    private int mapAnimatePhase;
    private Handler handler;

    public HeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        handler = new AnimateHandler(this);
    }

    public void setImageManager(ImageResourceManager imageManager) {
        this.imageManager = imageManager;
    }

    public void setImageInfo(ImageInfoBean info) {
        this.info = info;
        mapAnimatePhase = 0;

        setImage();

        if (info.getIdLength() > 1) {
            handler.sendEmptyMessageDelayed(0, ANIMATION_DURATION);
        } else {
            if (handler.hasMessages(0)) {
                handler.removeMessages(0);
            }
        }
    }

    private void setImage() {
        int len = info.getIdLength();
        int index = mapAnimatePhase % len;
        Bitmap bitmap = imageManager.getBitmap(info.getId(index));
        setImageBitmap(bitmap);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (handler.hasMessages(0)) {
            handler.removeMessages(0);
        }

        super.onDetachedFromWindow();
    }

    private static class AnimateHandler extends Handler {

        private Reference<HeadView> mapViewRef;

        public AnimateHandler(HeadView mapView) {
            mapViewRef = new SoftReference<>(mapView);
        }

        @Override
        public void handleMessage(Message msg) {
            HeadView mapView = mapViewRef.get();
            if (mapView == null) {
                return;
            }

            mapView.mapAnimatePhase++;
            mapView.setImage();
            sendEmptyMessageDelayed(0, ANIMATION_DURATION);
        }
    }
}
