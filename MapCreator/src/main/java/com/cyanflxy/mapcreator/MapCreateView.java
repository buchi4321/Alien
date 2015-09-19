package com.cyanflxy.mapcreator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.cyanflxy.mapcreator.bean.ImageInfoBean;
import com.cyanflxy.mapcreator.bean.MapBean;

import java.util.Arrays;

public class MapCreateView extends View {

    private static final int WIDTH_PIECE = 11;
    private static final int HEIGHT_PIECE = 11;

    private float pieceSize;

    private Paint linePaint;
    private Paint dotPaint;

    private Path outBorder;
    private Path dotPath;

    private ImageInfoBean[] mapData;
    private RectF imageRect;

    private ImageManager imageManager;

    private float touchX;
    private float touchY;
    private ImageInfoBean currentImage;

    public MapCreateView(Context context, AttributeSet attrs) {
        super(context, attrs);

        linePaint = new Paint();
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(1);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);

        dotPaint = new Paint();
        dotPaint.setColor(Color.BLACK);
        dotPaint.setStrokeWidth(1);
        dotPaint.setAntiAlias(true);
        dotPaint.setStyle(Paint.Style.STROKE);
        dotPaint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));

        outBorder = new Path();
        dotPath = new Path();

        imageRect = new RectF();
        touchX = -1;
        touchY = -1;

        mapData = new ImageInfoBean[WIDTH_PIECE * HEIGHT_PIECE];
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        float size = Math.min(width / (float) WIDTH_PIECE, height / (float) HEIGHT_PIECE);
        float fWidth = size * WIDTH_PIECE;
        float fHeight = size * HEIGHT_PIECE;
        widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) fWidth, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) fHeight, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (Float.compare(size, pieceSize) == 0 || Float.compare(size, 0) <= 0) {
            return;
        }

        pieceSize = size;

        // 外环
        outBorder.reset();
        outBorder.moveTo(0, 0);
        outBorder.lineTo(0, fHeight);
        outBorder.lineTo(fWidth, fHeight);
        outBorder.lineTo(fWidth, 0);
        outBorder.close();

        //中间虚线
        dotPath.reset();
        for (int i = 1; i < WIDTH_PIECE; i++) {
            float w = i * pieceSize;
            dotPath.moveTo(w, 0);
            dotPath.lineTo(w, fHeight);
        }

        for (int i = 1; i < HEIGHT_PIECE; i++) {
            float h = i * pieceSize;
            dotPath.moveTo(0, h);
            dotPath.lineTo(fWidth, h);
        }
    }

    public void setImageManager(ImageManager imageManager) {
        this.imageManager = imageManager;
    }

    public void setCurrentImage(ImageInfoBean imageInfo) {
        currentImage = imageInfo;
    }

    public ImageInfoBean[] getMapData() {
        return mapData;
    }

    public void loadMapData(MapBean mapBean) {
        currentImage = null;
        Arrays.fill(mapData, null);

        for (int i = 0; i < mapBean.mapData.length; i++) {
            String name = mapBean.mapData[i].element;
            if (!TextUtils.isEmpty(name)) {
                mapData[i] = imageManager.getImage(name);
            }
        }

        invalidate();
    }

    public void clearMap(){
        currentImage = null;
        Arrays.fill(mapData, null);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (imageManager == null) {
            return;
        }

        int floorId = imageManager.getFloorImageInfo().getFirstId();
        Bitmap floorBitmap = imageManager.getBitmap(floorId);

        int imageIndex = 0;
        for (int row = 0; row < HEIGHT_PIECE; row++) {
            for (int col = 0; col < WIDTH_PIECE; col++) {
                ImageInfoBean info = mapData[imageIndex];
                if (info == null) {
                    info = imageManager.getFloorImageInfo();
                }
                imageIndex++;

                Bitmap bitmap = imageManager.getBitmap(info.getFirstId());

                float left = col * pieceSize;
                float top = row * pieceSize;

                imageRect.set(left, top, left + pieceSize, top + pieceSize);
                canvas.drawBitmap(floorBitmap, null, imageRect, null);
                canvas.drawBitmap(bitmap, null, imageRect, null);
            }
        }

        if (touchX > 0 && touchY > 0 && currentImage != null) {
            float half = pieceSize / 2;
            imageRect.set(touchX - half, touchY - half, touchX + half, touchY + half);
            Bitmap bitmap = imageManager.getBitmap(currentImage.getFirstId());
            canvas.drawBitmap(bitmap, null, imageRect, null);
        }

        canvas.drawPath(outBorder, linePaint);
        canvas.drawPath(dotPath, dotPaint);

    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {

        touchX = event.getX();
        touchY = event.getY();

        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if (currentImage != null) {
                    int focusId = calculateTouchId(touchX, touchY);
                    if (focusId >= 0 && focusId < mapData.length) {
                        mapData[focusId] = currentImage;
                    }
                }
                touchX = -1;
                touchY = -1;
                break;
        }
        invalidate();
        return true;
    }

    private int calculateTouchId(float x, float y) {
        int row = (int) (y / pieceSize);
        int col = (int) (x / pieceSize);
        return row * WIDTH_PIECE + col;
    }

}
