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
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.cyanflxy.mapcreator.bean.ImageInfoBean;

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

//    public void setPiece(int w, int h) {
//
//        // 如果地图宽高有变化，应该在这里改动数据
//
//        requestLayout();
//    }

    public void setImageManager(ImageManager imageManager) {
        this.imageManager = imageManager;
        currentImage = imageManager.getFloorImageInfo();
        mapData = new ImageInfoBean[WIDTH_PIECE * HEIGHT_PIECE];
        Arrays.fill(mapData, currentImage);
    }

    public void setCurrentImage(ImageInfoBean imageInfo) {
        currentImage = imageInfo;
    }

    public String getMapString() {
        return "";
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
                    mapData[focusId] = currentImage;
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
