package com.cyanflxy.game.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.github.cyanflxy.magictower.R;

public class PageIndicatorView extends View {

    private int height;

    private float indicatorSize;
    private float indicatorMargin;
    private int indicatorCount;
    private Drawable indicatorDrawable;

    private RectF drawRect = new RectF();

    private int focusIndex;
    private float focusOffset;

    private Paint focusPaint;
    private Paint drawPaint;

    public PageIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PageIndicatorView);

        indicatorSize = a.getDimensionPixelSize(R.styleable.PageIndicatorView_indicator_size, 10);
        indicatorMargin = a.getDimensionPixelSize(R.styleable.PageIndicatorView_indicator_margin, 10);
        indicatorCount = a.getInt(R.styleable.PageIndicatorView_indicator_count, 0);
        ColorStateList indicatorColor = a.getColorStateList(R.styleable.PageIndicatorView_indicator_color);
        indicatorDrawable = a.getDrawable(R.styleable.PageIndicatorView_indicator_drawable);

        a.recycle();

        if (indicatorDrawable == null) {
            // 使用色值与默认的圆形标记
            drawPaint = new Paint();
            drawPaint.setStyle(Paint.Style.FILL);
            drawPaint.setAntiAlias(true);
            focusPaint = new Paint(drawPaint);

            if (indicatorColor != null) {
                drawPaint.setColor(indicatorColor.getDefaultColor());
                focusPaint.setColor(indicatorColor.getColorForState(View.SELECTED_STATE_SET, Color.GRAY));
            } else {
                drawPaint.setColor(Color.WHITE);
                focusPaint.setColor(Color.GRAY);
            }
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = (int) (indicatorCount * (indicatorSize + indicatorMargin));
        height = (int) (indicatorSize + indicatorMargin);
        setMeasuredDimension(width, height);
    }

    public void setIndicatorCount(int count) {
        indicatorCount = count;
        requestLayout();
    }

    public void setFocusIndex(int index, float offset) {
        focusIndex = index;
        focusOffset = offset;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (indicatorCount <= 0) {
            return;
        }

        float top = indicatorMargin / 2;
        float bottom = height - indicatorMargin / 2;

        float left = indicatorMargin / 2;

        for (int i = 0; i < indicatorCount; i++) {
            drawRect.set(left, top, left + indicatorSize, bottom);
            left += indicatorSize + indicatorMargin;

            if (indicatorDrawable == null) {
                drawIndicator(canvas, drawRect);
            } else {
                drawDrawableIndicator(canvas, drawRect);
            }
        }

        float focusLeft = indicatorMargin / 2 + (focusIndex + focusOffset) * (indicatorSize + indicatorMargin);
        drawRect.set(focusLeft, top, focusLeft + indicatorSize, bottom);

        if (indicatorDrawable == null) {
            drawFocus(canvas, drawRect);
        } else {
            drawDrawableFocus(canvas, drawRect);
        }

    }

    private void drawIndicator(Canvas canvas, RectF rectF) {
        float cx = (rectF.left + rectF.right) / 2;
        float cy = (rectF.top + rectF.bottom) / 2;
        canvas.drawCircle(cx, cy, indicatorSize / 2, drawPaint);
    }

    private void drawFocus(Canvas canvas, RectF rectF) {
        float cx = (rectF.left + rectF.right) / 2;
        float cy = (rectF.top + rectF.bottom) / 2;
        canvas.drawCircle(cx, cy, indicatorSize / 2, focusPaint);
    }

    private void drawDrawableIndicator(Canvas canvas, RectF rectF) {
        indicatorDrawable.setState(View.EMPTY_STATE_SET);
        indicatorDrawable.setBounds((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
        indicatorDrawable.draw(canvas);
    }

    private void drawDrawableFocus(Canvas canvas, RectF rectF) {
        indicatorDrawable.setState(View.SELECTED_STATE_SET);
        indicatorDrawable.setBounds((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
        indicatorDrawable.draw(canvas);
    }
}
