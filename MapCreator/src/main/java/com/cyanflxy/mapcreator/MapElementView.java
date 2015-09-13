package com.cyanflxy.mapcreator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.cyanflxy.mapcreator.bean.ImageInfoBean;

import java.util.List;

public class MapElementView extends View {

    public interface OnImageSelectListener {
        void onImageSelect(ImageInfoBean imageInfo);
    }

    private static final int ELEMENT_SIZE = 100;
    private static final int ELEMENT_PADDING = 10;
    private static final int ELEMENT = ELEMENT_SIZE + ELEMENT_PADDING;
    private static final int FOCUS_BORDER = 5;

    private int width;
    private int height;
    private int elementCountInLine;
    private float paddingLeftRight;

    private float startY;
    private float lastTouchY;
    private float maxStartY;

    private ImageManager imageManager;
    private List<ImageInfoBean> allImages;

    private RectF drawRect;

    private int focusId;
    private Paint focusPaint;
    private RectF focusRect;

    private OnImageSelectListener onImageSelectListener;

    public MapElementView(Context context, AttributeSet attrs) {
        super(context, attrs);

        imageManager = ImageManager.getInstance();
        allImages = imageManager.getAllImages();

        startY = 0;
        drawRect = new RectF();

        focusId = -1;
        focusPaint = new Paint();
        focusPaint.setStrokeWidth(FOCUS_BORDER);
        focusPaint.setColor(Color.RED);
        focusPaint.setStyle(Paint.Style.STROKE);
        focusRect = new RectF();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getWidth();
        if (width == this.width || width == 0) {
            return;
        }

        this.width = width;
        height = getHeight();

        elementCountInLine = width / ELEMENT;
        paddingLeftRight = (width - elementCountInLine * ELEMENT) / 2f + ELEMENT_PADDING / 2;

        int maxLine = (allImages.size() + elementCountInLine - 1) / elementCountInLine;
        maxStartY = maxLine * ELEMENT - height + ELEMENT_PADDING;

    }

    public void setOnImageSelectListener(OnImageSelectListener onImageSelectListener) {
        this.onImageSelectListener = onImageSelectListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int imageIndex = 0;

        for (float y = ELEMENT_PADDING; y < startY + height + ELEMENT_SIZE; y += ELEMENT) {

            if (y < startY - ELEMENT) {
                imageIndex += elementCountInLine;
                continue;
            }

            for (float x = paddingLeftRight; x < width - paddingLeftRight; x += ELEMENT) {
                if (imageIndex >= allImages.size()) {
                    break;
                }

                ImageInfoBean info = allImages.get(imageIndex);
                int id = info.getFirstId();
                Bitmap bitmap = imageManager.getBitmap(id);
                drawRect.set(x, y - startY, x + ELEMENT_SIZE, y + ELEMENT_SIZE - startY);
                canvas.drawBitmap(bitmap, null, drawRect, null);
                imageIndex++;
            }

        }

        // 绘制焦点,由于焦点是根据点击移动的，所以一定会在手指下面
        if (focusId != -1) {
            int row = focusId / elementCountInLine;
            int col = focusId % elementCountInLine;

            float left = paddingLeftRight + col * ELEMENT - FOCUS_BORDER / 2f;
            float right = left + ELEMENT_SIZE + FOCUS_BORDER;
            float top = ELEMENT_PADDING + row * ELEMENT - FOCUS_BORDER / 2f - startY;
            float bottom = top + ELEMENT_SIZE + FOCUS_BORDER;

            focusRect.set(left, top, right, bottom);

            canvas.drawRect(focusRect, focusPaint);
        }
    }


    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                calculateFocus(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                startY += lastTouchY - y;

                if (startY < 0) {
                    startY = 0;
                }

                if (startY > maxStartY) {
                    startY = maxStartY;
                }

                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        lastTouchY = y;

        return true;
    }

    private void calculateFocus(float x, float y) {
        y += startY;

        int row = (int) ((y - ELEMENT_PADDING) / ELEMENT);
        int col = (int) ((x - paddingLeftRight) / ELEMENT);

        int id = row * elementCountInLine + col;
        if (id == focusId) {
            focusId = -1;
        } else {
            focusId = id;
        }

        invalidate();

        if (onImageSelectListener != null) {
            if (focusId != -1) {
                onImageSelectListener.onImageSelect(allImages.get(focusId));
            } else {
                onImageSelectListener.onImageSelect(null);
            }
        }
    }
}
