package com.cyanflxy.game.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.github.cyanflxy.magictower.R;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

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

    private Handler animateHandler;
    private OnTextAnimationListener listener;

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

        calculateWordPosition();

    }

    private void calculateWordPosition() {
        int w = getWidth();
        int h = getHeight();
        if (w == width && h == height) {
            return;
        }

        width = w;
        height = h;

        drawPaint.getTextBounds(infoString, 0, 1, wordRect);
        wordHeight = wordRect.height();
        wordWidth = wordRect.width();
        int lastWordWidth = wordWidth;

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

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (TextUtils.isEmpty(infoString) || currentStringLen <= 0) {
            return;
        }

        calculateWordPosition();

        int heightCorrect = 0;
        int maxHeight = (int) infoStringPosition[currentStringLen * 2 - 1];
        int height = getHeight();

        if (maxHeight > height) {
            heightCorrect = maxHeight - height + 10;
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

        requestLayout();
    }

    public void startAnimation(int progress) {
        if (animateHandler != null && animateHandler.hasMessages(0)) {
            return;
        }

        if (animateHandler == null) {
            animateHandler = new AnimateHandler(this);
        }

        currentStringLen = progress;
        animateHandler.sendEmptyMessageDelayed(0, ANIMATE_DURATION);
        invalidate();
    }

    public int getProgress() {
        return currentStringLen;
    }

    public boolean isAnimationEnd() {
        return currentStringLen >= infoString.length();
    }

    public void endAnimation() {
        if (animateHandler != null && animateHandler.hasMessages(0)) {
            animateHandler.removeMessages(0);
        }

        currentStringLen = infoString.length();
        invalidate();

        if (listener != null) {
            listener.onAnimationEnd();
        }
    }

    private static class AnimateHandler extends Handler {

        private Reference<AnimateTextView> viewRef;

        public AnimateHandler(AnimateTextView textView) {
            viewRef = new WeakReference<>(textView);
        }

        @Override
        public void handleMessage(Message msg) {
            AnimateTextView view = viewRef.get();
            if (view == null || TextUtils.isEmpty(view.infoString)) {
                return;
            }

            view.currentStringLen++;
            if (view.currentStringLen <= view.infoString.length()) {
                view.invalidate();

                sendEmptyMessageDelayed(0, ANIMATE_DURATION);
            } else {
                view.endAnimation();
            }
        }
    }
}
