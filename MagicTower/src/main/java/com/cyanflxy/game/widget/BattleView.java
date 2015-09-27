package com.cyanflxy.game.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.cyanflxy.game.bean.EnemyProperty;
import com.cyanflxy.game.bean.HeroBean;
import com.cyanflxy.game.driver.ImageResourceManager;
import com.github.cyanflxy.magictower.R;

public class BattleView extends View {

    private static final int TOTAL_WIDTH = 345;
    private static final int TOTAL_HEIGHT = 150;

    private static final int FRAME_SIZE = 58;
    private static final int FRAME_TOP = 20;
    private static final int FRAME_LEFT = 8;
    private static final int FRAME_RIGHT = 337;

    private static final int ENEMY_ATTR_RIGHT = 120;
    private static final int HERO_ATTR_LEFT = 225;

    private static final int HP_ATTR_TOP = 31;
    private static final int DAMAGE_ATTR_TOP = 66;
    private static final int DEFENCE_ATTR_TOP = 102;

    private int width;
    private int height;

    private RectF enemyFrame = new RectF();
    private RectF heroFrame = new RectF();
    private Rect textBoundTool = new Rect();

    private float enemyAttrRight;
    private float heroAttrLeft;

    private float hpAttrTop;
    private float damageAttrTop;
    private float defenceAttrTop;

    private Paint textPaint;

    private HeroBean hero;
    private EnemyProperty enemy;
    private ImageResourceManager imageManager;

    public BattleView(Context context) {
        this(context, null);
    }

    public BattleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundResource(R.drawable.battle_bg);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);

    }

    public void setImageManager(ImageResourceManager imageManager) {
        this.imageManager = imageManager;
    }

    public void setInfo(HeroBean hero, EnemyProperty enemy) {
        this.hero = hero;
        this.enemy = enemy;

        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        float currentRate = (float) w / h;
        float bgRate = (float) TOTAL_WIDTH / TOTAL_HEIGHT;

        if (currentRate < bgRate - 0.01) {//宽度小
            h = (int) (w / bgRate);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY);
        } else if (currentRate > bgRate + 0.01) {
            w = (int) (h * bgRate);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (w == 0 || h == 0) {
            return;
        }

        if (width == w && height == h) {
            return;
        }

        width = w;
        height = h;
        calculateSize();
    }

    private void calculateSize() {
        float rate = (float) width / TOTAL_WIDTH;

        float frameSize = FRAME_SIZE * rate;
        float frameTop = FRAME_TOP * rate;
        float frameLeft = FRAME_LEFT * rate;
        float frameRight = FRAME_RIGHT * rate;

        enemyAttrRight = ENEMY_ATTR_RIGHT * rate;
        heroAttrLeft = HERO_ATTR_LEFT * rate;

        float textBottom = rate * 5;

        hpAttrTop = HP_ATTR_TOP * rate + textBottom;
        damageAttrTop = DAMAGE_ATTR_TOP * rate + textBottom;
        defenceAttrTop = DEFENCE_ATTR_TOP * rate + textBottom;

        float bitmapPadding = frameSize * 0.25f;
        enemyFrame.set(frameLeft + bitmapPadding, frameTop + bitmapPadding,
                frameLeft + frameSize - bitmapPadding, frameTop + frameSize - bitmapPadding);
        heroFrame.set(frameRight - frameSize + bitmapPadding, frameTop + bitmapPadding,
                frameRight - bitmapPadding, frameTop + frameSize - bitmapPadding);

        textPaint.setTextSize(12 * rate);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (hero == null || enemy == null || imageManager == null) {
            return;
        }

        Bitmap enemyBitmap = imageManager.getBitmap(enemy.resourceName);
        canvas.drawBitmap(enemyBitmap, null, enemyFrame, null);

        Bitmap heroBitmap = imageManager.getBitmap(hero.avatar);
        canvas.drawBitmap(heroBitmap, null, heroFrame, null);

        String text;

        text = String.valueOf(enemy.hp);
        textPaint.getTextBounds(text, 0, text.length(), textBoundTool);
        canvas.drawText(text, enemyAttrRight - textBoundTool.right,
                hpAttrTop - textBoundTool.top, textPaint);

        text = String.valueOf(enemy.damage);
        textPaint.getTextBounds(text, 0, text.length(), textBoundTool);
        canvas.drawText(text, enemyAttrRight - textBoundTool.right,
                damageAttrTop - textBoundTool.top, textPaint);

        text = String.valueOf(enemy.defense);
        textPaint.getTextBounds(text, 0, text.length(), textBoundTool);
        canvas.drawText(text, enemyAttrRight - textBoundTool.right,
                defenceAttrTop - textBoundTool.top, textPaint);


        text = String.valueOf(hero.hp);
        textPaint.getTextBounds(text, 0, text.length(), textBoundTool);
        canvas.drawText(text, heroAttrLeft - textBoundTool.left,
                hpAttrTop - textBoundTool.top, textPaint);

        text = String.valueOf(hero.damage);
        textPaint.getTextBounds(text, 0, text.length(), textBoundTool);
        canvas.drawText(text, heroAttrLeft - textBoundTool.left,
                damageAttrTop - textBoundTool.top, textPaint);

        text = String.valueOf(hero.defense);
        textPaint.getTextBounds(text, 0, text.length(), textBoundTool);
        canvas.drawText(text, heroAttrLeft - textBoundTool.left,
                defenceAttrTop - textBoundTool.top, textPaint);
    }
}
