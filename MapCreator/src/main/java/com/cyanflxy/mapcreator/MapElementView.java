package com.cyanflxy.mapcreator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.cyanflxy.mapcreator.bean.ImageInfoBean;

import java.util.List;

public class MapElementView extends View {

    private static final int ELEMENT_SIZE = 100;
    private static final int ELEMENT_PADDING = 10;

    private int width;
    private int height;
    private int elementCountInLine;
    private float paddingLeftRight;
    private float startY;

    private ImageManager imageManager;
    private List<ImageInfoBean> allImages;

    private RectF drawRect;

    public MapElementView(Context context, AttributeSet attrs) {
        super(context, attrs);

        imageManager = ImageManager.getInstance();
        allImages = imageManager.getAllImages();

        startY = 0;
        drawRect = new RectF();
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

        elementCountInLine = width / (ELEMENT_SIZE + ELEMENT_PADDING);
        paddingLeftRight = (width - elementCountInLine * (ELEMENT_SIZE + ELEMENT_PADDING)) / 2f + ELEMENT_PADDING / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int imageIndex = 0;

        for (float y = 0; y < height + ELEMENT_SIZE; y += ELEMENT_SIZE + ELEMENT_PADDING) {

            for (float x = paddingLeftRight; x < width; x += ELEMENT_PADDING + ELEMENT_SIZE) {
                ImageInfoBean info = allImages.get(imageIndex);
                int id = info.getFirstId();
                Bitmap bitmap = imageManager.getBitmap(id);
                drawRect.set(x, y, x + ELEMENT_SIZE, y + ELEMENT_SIZE);
                canvas.drawBitmap(bitmap, null, drawRect, null);
            }

        }
    }
}
