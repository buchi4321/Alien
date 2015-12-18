package com.cyanflxy.game.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.github.cyanflxy.magictower.R;

public class AnimateTextView extends View {

    public interface OnTextAnimationListener {
        void onAnimationEnd();
    }

    private static final int WORD_SPACE = 8;
    private static final int LINE_SPACE = 18;
    private static final int PARAGRAPH_SPACE = 60;
    private static final int PARAGRAPH_FIRST_SPACE = 54;

    private static final int ANIMATE_DURATION = 80;

    private int width;
    private int height;

    private String infoString;
    private float[] infoStringPosition;
    private int currentStringLen;

    private int wordWidth;
    private int wordHeight;
    private Rect wordRect = new Rect();
    private Paint drawPaint;
    private Bitmap cursorBitmap;
    private RectF cursorRect = new RectF();

    // 滑动手势
    private float touchY;
    private float lastY;
    private float drawTop;
    private float maxTop;
    private boolean showTouchTop;

    private ObjectAnimator animator;
    private OnTextAnimationListener listener;
    private OnClickListener onClickListener;

    public AnimateTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        drawPaint = new Paint();
        drawPaint.setTextSize(context.getResources().getDimension(R.dimen.comm_text_size));
        //noinspection deprecation
        drawPaint.setColor(context.getResources().getColor(R.color.comm_text));
        drawPaint.setFakeBoldText(true);

        cursorBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sward_cursor);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (TextUtils.isEmpty(infoString)) {
            return;
        }

        calculateWordPosition(false);

    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
        int action = event.getAction();
        float y = event.getY();

        if (action == MotionEvent.ACTION_DOWN) {
            if (isAnimationEnd()) {
                touchY = y;
                lastY = drawTop;
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (isAnimationEnd()) {
                drawTop = lastY + touchY - y;
                if (drawTop < 0) {
                    drawTop = 0;
                } else if (drawTop > maxTop) {
                    drawTop = maxTop;
                }
                showTouchTop = true;
                invalidate();
            }
        } else if (action == MotionEvent.ACTION_UP) {
            if (onClickListener != null) {
                onClickListener.onClick(this);
            }
        }

        return true;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        onClickListener = l;
    }

    private void calculateWordPosition(boolean force) {

        int w = getWidth();
        int h = getHeight();
        if (!force && w == width && h == height) {
            return;
        }

        width = w;
        height = h;

        drawPaint.getTextBounds(infoString, 0, infoString.length(), wordRect);
        wordHeight = wordRect.height();

        drawPaint.getTextBounds("字", 0, 1, wordRect);
        wordWidth = wordRect.width();

        drawPaint.getTextBounds(infoString, 0, 1, wordRect);
        int lastWordWidth = wordRect.width();

        int currentLeft = PARAGRAPH_FIRST_SPACE;
        int currentBottom = wordHeight;
        infoStringPosition[0] = currentLeft - wordRect.left;
        infoStringPosition[1] = currentBottom;

        int wordW;

        for (int i = 1; i < infoString.length(); i++) {
            if (infoString.charAt(i) == '\n') {
                currentLeft = PARAGRAPH_FIRST_SPACE;
                currentBottom += wordHeight + LINE_SPACE + PARAGRAPH_SPACE;
                lastWordWidth = 0;
                infoStringPosition[i * 2] = currentLeft;
                infoStringPosition[i * 2 + 1] = currentBottom;
                continue;
            }

            drawPaint.getTextBounds(infoString, i, i + 1, wordRect);
            wordW = wordRect.width();

            currentLeft += lastWordWidth + WORD_SPACE;

            if (currentLeft > width - wordW) {
                currentLeft = 0;
                currentBottom += wordHeight + LINE_SPACE;
            }

            lastWordWidth = wordW;

            infoStringPosition[i * 2] = currentLeft - wordRect.left;
            infoStringPosition[i * 2 + 1] = currentBottom;

        }

        int maxHeight = (int) infoStringPosition[infoStringPosition.length - 1];
        maxTop = maxHeight - height + 10;
        if (maxTop < 0) {
            maxTop = 0;
        }
        drawTop = maxTop;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (TextUtils.isEmpty(infoString) || currentStringLen <= 0) {
            return;
        }

        calculateWordPosition(false);

        float heightCorrect = 0;
        if (isAnimationEnd() && showTouchTop) {
            heightCorrect = drawTop;
        } else {
            int maxHeight = (int) infoStringPosition[currentStringLen * 2 - 1];
            int height = getHeight();

            if (maxHeight > height) {
                heightCorrect = maxHeight - height + 10;
            }
        }

        for (int i = 0; i < currentStringLen; i++) {
            if (infoStringPosition[i * 2 + 1] < heightCorrect) {
                continue;
            }

            canvas.drawText(infoString, i, i + 1, infoStringPosition[i * 2], infoStringPosition[i * 2 + 1] - heightCorrect, drawPaint);
        }

        if (currentStringLen < infoString.length()) {
            float left = infoStringPosition[currentStringLen * 2] + 30;
            float bottom = infoStringPosition[currentStringLen * 2 + 1] - heightCorrect + 10;

            cursorRect.set(left, bottom - wordHeight, left + wordWidth, bottom);

            canvas.drawBitmap(cursorBitmap, null, cursorRect, null);
        }
    }

    public void setOnTextAnimationEndListener(OnTextAnimationListener l) {
        listener = l;
    }

    public void setString(String str) {
        infoString = str;
        infoStringPosition = new float[str.length() * 2];
        drawTop = 0;
        showTouchTop = false;

        calculateWordPosition(true);
    }

    public void startAnimation(int progress) {

        if (animator != null && animator.isRunning()) {
            return;
        }

        int end = infoString.length();
        if (progress >= end) {
            return;
        }

        int duration = (end - progress) * ANIMATE_DURATION;

        animator = ObjectAnimator.ofInt(this, "currentStringLen", progress, end);
        animator.setDuration(duration);
        animator.setInterpolator(new LinearInterpolator());

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animation.removeAllListeners();
                sendAnimatorEndMessage();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                animation.removeAllListeners();
                sendAnimatorEndMessage();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            private void sendAnimatorEndMessage() {
                if (listener != null) {
                    listener.onAnimationEnd();
                }
            }
        });

        animator.start();
    }

    public void stopAnimation() {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
    }

    // 属性动画
    @SuppressWarnings("unused")
    public void setCurrentStringLen(int len) {
        currentStringLen = len;
        invalidate();

        if (currentStringLen >= infoString.length()) {
            endAnimation();
        }
    }

    public int getProgress() {
        return currentStringLen;
    }

    public boolean isAnimationEnd() {
        return currentStringLen >= infoString.length();
    }

    public void endAnimation() {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
            currentStringLen = infoString.length();
            invalidate();
        }

    }

}
