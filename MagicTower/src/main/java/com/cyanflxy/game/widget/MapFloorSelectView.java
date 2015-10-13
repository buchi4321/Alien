package com.cyanflxy.game.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.cyanflxy.common.Utils;
import com.cyanflxy.game.bean.GameBean;
import com.cyanflxy.game.driver.GameContext;
import com.github.cyanflxy.magictower.R;

public class MapFloorSelectView extends View {

    public interface OnMapSelectListener {
        void onMapSelect(int mapFloor);
    }

    private int width;
    private int height;

    private float buttonWidth;
    private float buttonHeight;
    private float buttonPaddingHorizontal;
    private float buttonPaddingVertical;
    private float buttonRoundRadius;
    private RectF buttonRect = new RectF();

    private int buttonColorNormal;
    private int buttonColorPress;
    private int buttonColorDisable;

    private float drawLeft;
    private int itemInLine;

    private Rect textRectTool = new Rect();
    private Paint textPaint;
    private Paint buttonPaint;
    private Paint focusPaint;

    private int focusFloor = -1;
    private int currentFloor;
    private String[] mapNames;
    private OnMapSelectListener listener;

    private GameBean gameData;

    public MapFloorSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);

        buttonWidth = Utils.dip2px(80);
        buttonHeight = Utils.dip2px(40);

        buttonPaddingHorizontal = Utils.dip2px(20);
        buttonPaddingVertical = Utils.dip2px(15);

        buttonRoundRadius = Utils.dip2px(5);

        buttonColorNormal = 0xFFEFCE7B;
        buttonColorPress = 0xFFFFE7B5;
        buttonColorDisable = 0xFFB39A5C;
        int buttonColorSelected = 0xFFC23020;

        textPaint = new Paint();
        textPaint.setTextSize(Utils.sp2px(18));
        textPaint.setAntiAlias(true);
        //noinspection deprecation
        textPaint.setColor(context.getResources().getColor(R.color.comm_text));

        buttonPaint = new Paint();
        buttonPaint.setAntiAlias(true);
        buttonPaint.setStyle(Paint.Style.FILL);

        focusPaint = new Paint();
        focusPaint.setAntiAlias(true);
        focusPaint.setStyle(Paint.Style.STROKE);
        focusPaint.setStrokeWidth(Utils.dip2px(2.5f));
        focusPaint.setColor(buttonColorSelected);

    }

    public void setOnMapSelectListener(OnMapSelectListener l) {
        listener = l;
    }

    public void setCurrentFloor(int floor) {
        currentFloor = floor;
    }

    public void setMap(String[] map) {
        mapNames = map;
        gameData = GameContext.getInstance().getGameData();
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mapNames == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int w = MeasureSpec.getSize(widthMeasureSpec);
        if (w == 0) {
            return;
        }

        width = w;
        calculateSize();

        setMeasuredDimension(width, height);

    }

    private void calculateSize() {
        int mapLength = mapNames.length;

        itemInLine = (int) (width / (buttonWidth + buttonPaddingHorizontal));
        int lineNumber = (mapLength + itemInLine - 1) / itemInLine;
        height = (int) (lineNumber * (buttonHeight + buttonPaddingVertical) + buttonPaddingVertical);

        float widthTotal = itemInLine * (buttonWidth + buttonPaddingHorizontal) - buttonPaddingHorizontal;
        drawLeft = (width - widthTotal) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mapNames == null || gameData == null) {
            return;
        }

        float top = buttonPaddingVertical;

        for (int item = 0; item < mapNames.length; item += itemInLine) {
            float left = drawLeft;
            for (int i = 0; i < itemInLine; i++) {
                int floorNumber = item + i;

                if (floorNumber >= mapNames.length) {
                    continue;
                }

                if (focusFloor == floorNumber) {
                    buttonPaint.setColor(buttonColorPress);
                } else if (!gameData.mapOpen[floorNumber]) {
                    buttonPaint.setColor(buttonColorDisable);
                } else {
                    buttonPaint.setColor(buttonColorNormal);
                }

                buttonRect.set(left, top, left + buttonWidth, top + buttonHeight);
                canvas.drawRoundRect(buttonRect, buttonRoundRadius, buttonRoundRadius, buttonPaint);

                if (currentFloor == floorNumber) {
                    canvas.drawRoundRect(buttonRect, buttonRoundRadius, buttonRoundRadius, focusPaint);
                }

                String floor = String.valueOf(floorNumber);
                textPaint.getTextBounds(floor, 0, floor.length(), textRectTool);
                float textX = left + (buttonWidth - textRectTool.width()) / 2 - textRectTool.left;
                float textY = top + (buttonHeight + textRectTool.height()) / 2;
                canvas.drawText(floor, textX, textY, textPaint);

                left += buttonWidth + buttonPaddingHorizontal;
            }
            top += buttonHeight + buttonPaddingVertical;
        }

    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
        int action = event.getAction();

        float x = event.getX();
        float y = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                focusFloor = getLocationId(x, y);
                if (focusFloor >= 0) {
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (focusFloor != getLocationId(x, y)) {
                    focusFloor = -1;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (focusFloor >= 0) {
                    if (listener != null) {
                        listener.onMapSelect(focusFloor);
                    }
                    focusFloor = -1;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                focusFloor = -1;
                invalidate();
                break;
        }

        return true;
    }

    private int getLocationId(float x, float y) {
        int idInLine = -1;

        float left = drawLeft;
        for (int i = 0; i < itemInLine; i++) {
            if (x > left && x < (left + buttonWidth)) {
                idInLine = i;
                break;
            }
            left += buttonWidth + buttonPaddingHorizontal;
        }

        if (idInLine < 0) {
            return -1;
        }

        int idInRow = -1;
        float top = buttonPaddingVertical;
        int lines = (mapNames.length + itemInLine - 1) / itemInLine;

        for (int i = 0; i < lines; i++) {
            if (y > top && y < (top + buttonHeight)) {
                idInRow = i;
                break;
            }
            top += buttonHeight + buttonPaddingVertical;
        }

        if (idInRow < 0) {
            return -1;
        }

        int id = idInRow * itemInLine + idInLine;
        if (id >= mapNames.length) {
            return -1;
        }

        return id;
    }
}
