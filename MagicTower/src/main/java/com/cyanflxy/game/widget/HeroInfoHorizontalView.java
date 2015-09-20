package com.cyanflxy.game.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.cyanflxy.common.Utils;

public class HeroInfoHorizontalView extends HeroInfoView {

    private static final int MAX_HEIGHT = Utils.dip2px(200);

    private float bottom;
    private float piece;

    public HeroInfoHorizontalView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void resize() {
        // 左右分成5份，头像一份，属性一份，钥匙一份
        piece = width / 5;

        float top = 0;
        bottom = height;

        // 上下位置
        if (height > MAX_HEIGHT) {
            float padding = (height - MAX_HEIGHT) / 2f;
            top = padding;
            bottom = height - padding;
        }

        //头像位置
        float avatarHeight;

        if (piece > AVATAR_SIZE) {
            float padding = (piece - AVATAR_SIZE) / 2f;
            avatarHeight = AVATAR_SIZE;
            avatarRect.set(padding, top, piece - padding, top + AVATAR_SIZE);
        } else {
            avatarHeight = piece;
            avatarRect.set(0, top, piece, top + piece);
        }

        // 楼层位置
        String floorText = getFloorString();
        textPaint.getTextBounds(floorText, 0, floorText.length(), rectTool);
        int textHeight = rectTool.height();

        // 剩余空间给功能按钮
        float restHeight = bottom - top - avatarHeight - textHeight;
        if (restHeight < GOODS_SIZE * 2 + GOODS_MARGIN * 2) {
            float imageSize = restHeight / 2;
            float topBook = top + avatarHeight;
            float imageLeft = (piece - imageSize) / 2;

            bookRect.set(imageLeft, topBook, imageLeft + imageSize, topBook + imageSize);
            flyRect.set(imageLeft, topBook + imageSize, imageLeft + imageSize, topBook + imageSize * 2);

        } else {
            float imageLeft = (piece - GOODS_SIZE) / 2;
            float topFly = bottom - textHeight - GOODS_MARGIN - GOODS_SIZE;
            bookRect.set(imageLeft, topFly - GOODS_SIZE - GOODS_MARGIN, imageLeft + GOODS_SIZE, topFly - GOODS_MARGIN);
            flyRect.set(imageLeft, topFly, imageLeft + GOODS_SIZE, topFly + GOODS_SIZE);

        }

        attributeRect.set(piece, top, piece * 3, bottom);
        keysRect.set(piece * 3, top, width, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        String floorText = getFloorString();
        textPaint.getTextBounds(floorText, 0, floorText.length(), rectTool);
        int width = rectTool.width();

        float x = (piece - width) / 2 - rectTool.left;
        float y = bottom - rectTool.bottom;
        canvas.drawText(floorText, x, y, textPaint);


    }
}
