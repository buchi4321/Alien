package com.cyanflxy.game.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.cyanflxy.common.Utils;

public class HeroInfoVerticalView extends HeroInfoView {

    private static final int MAX_WIDTH = Utils.dip2px(150);

    private float right;
    private float avatarBottom;

    public HeroInfoVerticalView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void resize() {

        float piece = height / 7.5f;

        float left = 0;
        right = width;

        if (width > MAX_WIDTH) {
            float padding = (width - MAX_WIDTH) / 2f;
            left = padding;
            right = width - padding;
        }

        //头像位置
        float avatarHeight;
        float avatarTop;

        if (piece * 1.5f > AVATAR_SIZE) {
            float padding = (piece * 1.5f - AVATAR_SIZE) / 2f;
            avatarHeight = AVATAR_SIZE;
            avatarTop = padding;
            avatarBottom = avatarTop + avatarHeight;
            avatarRect.set(left, padding, left + AVATAR_SIZE, avatarBottom);
        } else {
            avatarHeight = piece * 1.5f;
            avatarTop = 0;
            avatarBottom = avatarHeight;
            avatarRect.set(left, piece * 0.5f, left + piece, avatarBottom);
        }

        // 楼层位置
        String floorText = getFloorString();
        textPaint.getTextBounds(floorText, 0, floorText.length(), rectTool);
        int textHeight = rectTool.height();

        // 剩余空间给功能按钮
        float restHeight = avatarHeight - textHeight;
        if (restHeight < GOODS_SIZE + GOODS_MARGIN) {
            float imageLeft = right - GOODS_SIZE * 2 + GOODS_MARGIN;

            bookRect.set(imageLeft, avatarTop, imageLeft + restHeight, avatarTop + restHeight);
            flyRect.set(imageLeft + GOODS_SIZE + GOODS_MARGIN, avatarTop,
                    imageLeft + GOODS_SIZE * 2 + GOODS_MARGIN, avatarTop + restHeight);

        } else {
            float imageLeft = right - GOODS_SIZE * 2 + GOODS_MARGIN;

            bookRect.set(imageLeft, avatarTop, imageLeft + GOODS_SIZE, avatarTop + GOODS_SIZE);
            flyRect.set(imageLeft + GOODS_SIZE + GOODS_MARGIN, avatarTop,
                    imageLeft + GOODS_SIZE * 2 + GOODS_MARGIN, avatarTop + GOODS_SIZE);
        }

        attributeRect.set(left, piece * 1.5f, right, piece * 4.5f);
        keysRect.set(left, piece * 4.5f, right, height);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        String floorText = getFloorString();
        textPaint.getTextBounds(floorText, 0, floorText.length(), rectTool);

        float x = right - rectTool.width() - rectTool.left;
        float y = avatarBottom - rectTool.bottom;
        canvas.drawText(floorText, x, y, textPaint);
    }
}
