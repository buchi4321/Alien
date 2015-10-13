package com.cyanflxy.game.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.cyanflxy.common.Utils;
import com.cyanflxy.game.bean.HeroBean;
import com.cyanflxy.game.data.GameSharedPref;
import com.cyanflxy.game.driver.GameContext;
import com.cyanflxy.game.driver.ImageResourceManager;
import com.github.cyanflxy.magictower.BuildConfig;
import com.github.cyanflxy.magictower.R;

import static com.github.cyanflxy.magictower.AppApplication.baseContext;

public abstract class HeroInfoView extends View {

    public interface OnFunctionClickListener {
        void onEnemyProperty();

        void onJumpFloor();

        void onShopShortcut();
    }

    protected static final int AVATAR_SIZE = Utils.dip2px(45);
    protected static final int GOODS_SIZE = Utils.dip2px(30);
    protected static final int GOODS_MARGIN = Utils.dip2px(8);

    private static final String[] ATTRIBUTE_NAME = new String[]{
            baseContext.getString(R.string.level),
            baseContext.getString(R.string.hp),
            baseContext.getString(R.string.damage),
            baseContext.getString(R.string.defense),
            baseContext.getString(R.string.money),
            baseContext.getString(R.string.exp),
    };

    protected int width;
    protected int height;

    protected GameContext gameContext;
    protected ImageResourceManager imageManager;
    protected HeroBean heroBean;
    private int heroHashCode;

    protected RectF avatarRect;
    protected RectF bookRect;
    protected RectF flyRect;
    protected Rect rectTool;

    protected RectF attributeRect;
    protected RectF keysRect;

    protected Paint textPaint;

    private Path dotPath;
    private Paint dotPaint;

    private boolean focusHelp;
    private boolean focusFly;
    private boolean focusHero;
    private Paint focusPaint;

    private OnFunctionClickListener listener;

    public HeroInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setBackgroundResource(R.drawable.attribute_bg);

        avatarRect = new RectF();
        bookRect = new RectF();
        flyRect = new RectF();
        rectTool = new Rect();

        attributeRect = new RectF();
        keysRect = new RectF();

        textPaint = new Paint();
        textPaint.setTextSize(Utils.sp2px(14));
        textPaint.setAntiAlias(true);
        //noinspection deprecation
        textPaint.setColor(context.getResources().getColor(R.color.comm_text));

        dotPath = new Path();
        dotPaint = new Paint();
        dotPaint.setColor(Color.BLACK);
        dotPaint.setStrokeWidth(1);
        dotPaint.setAntiAlias(true);
        dotPaint.setStyle(Paint.Style.STROKE);
        dotPaint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));

        focusPaint = new Paint();
        focusPaint.setAntiAlias(true);
        focusPaint.setStyle(Paint.Style.STROKE);
        focusPaint.setStrokeWidth(2);
        //noinspection deprecation
        focusPaint.setColor(context.getResources().getColor(R.color.setting_selected_color));

    }

    public void setGameContext(GameContext gameContext) {
        this.gameContext = gameContext;
        imageManager = gameContext.getImageResourceManager();
        heroBean = gameContext.getHero();
    }

    public void setOnFunctionClickListener(OnFunctionClickListener l) {
        listener = l;
    }

    public void refreshInfo() {
        heroBean = gameContext.getHero();
        int hash = heroBean.hashCode();
        if (heroHashCode != hash || BuildConfig.DEBUG) {
            heroHashCode = hash;
            invalidate();
        }
    }

    protected String getFloorString() {
        int floor = gameContext.getCurrentMap().mapFloor;
        return getContext().getString(R.string.floor, floor);
    }

    protected Bitmap getHeroAvatar() {
        return imageManager.getBitmap(heroBean.avatar);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);

        int modeW = MeasureSpec.getMode(widthMeasureSpec);
        int modeH = MeasureSpec.getMode(heightMeasureSpec);

        if (modeW != MeasureSpec.EXACTLY || modeH != MeasureSpec.EXACTLY) {
            return;
        }

        if (w == 0 || h == 0) {
            return;
        }

        if (w == width && h == height) {
            return;
        }

        width = w;
        height = h;

        resize();
        calculatePath();
    }

    protected abstract void resize();

    private void calculatePath() {

        float height = attributeRect.height();
        float heightPiece = height / ATTRIBUTE_NAME.length;

        dotPath.reset();
        for (int i = 0; i < ATTRIBUTE_NAME.length; i++) {
            dotPath.moveTo(attributeRect.left, attributeRect.top + heightPiece * (i + 1));
            dotPath.lineTo(attributeRect.right, attributeRect.top + heightPiece * (i + 1));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (imageManager == null || imageManager.isDestroy()) {
            return;
        }

        canvas.drawBitmap(getHeroAvatar(), null, avatarRect, null);
        if (focusHero) {
            canvas.drawRoundRect(avatarRect, 5, 5, focusPaint);
        }

        if (showHelpButton()) {
            canvas.drawBitmap(imageManager.getBitmap("help_book"), null, bookRect, null);
            if (focusHelp) {
                canvas.drawRoundRect(bookRect, 5, 5, focusPaint);
            }
        }

        if (showFlyButton()) {
            canvas.drawBitmap(imageManager.getBitmap("swing"), null, flyRect, null);
            if (focusFly) {
                canvas.drawRoundRect(flyRect, 5, 5, focusPaint);
            }
        }

        // ClipXX方法对画图片和颜色有效，对划线就有问题了,还是自己算吧
        drawAttribute(canvas, attributeRect);
        drawKeys(canvas, keysRect);

        canvas.drawPath(dotPath, dotPaint);
    }

    private boolean showHelpButton() {
        return GameSharedPref.isOpenAllFunction() || heroBean.help;
    }

    private boolean showFlyButton() {
        return GameSharedPref.isOpenAllFunction() || heroBean.fly;
    }

    private void drawAttribute(Canvas canvas, RectF rect) {
        float height = rect.height();
        float heightPiece = height / 6;

        int[] attrValue = heroBean.getHeroAttribute();

        for (int i = 0; i < ATTRIBUTE_NAME.length; i++) {

            drawSingleAttribute(canvas, ATTRIBUTE_NAME[i], String.valueOf(attrValue[i]),
                    rect.left, rect.top + heightPiece * i, rect.right, rect.top + heightPiece * (i + 1));
        }

    }

    private void drawSingleAttribute(Canvas canvas, String name, String value,
                                     float left, float top, float right, float bottom) {
        float width = right - left;
        float height = bottom - top;
        float middle = width / 2f;

        textPaint.getTextBounds(name, 0, name.length(), rectTool);
        int w = rectTool.width();
        float nameLeft = (middle - w) / 2 + left;
        float textBottom = bottom - (height - rectTool.height()) / 2;
        canvas.drawText(name, nameLeft - rectTool.left, textBottom - rectTool.bottom, textPaint);

        textPaint.getTextBounds(value, 0, value.length(), rectTool);
        canvas.drawText(value, left + middle - rectTool.left, textBottom - rectTool.bottom, textPaint);

    }

    private void drawKeys(Canvas canvas, RectF rect) {

        Bitmap[] keyBitmap = getKeyBitmap();
        int[] keyValue = heroBean.getKeysValue();

        float height = rect.height();
        float heightPiece = height / keyValue.length;

        for (int i = 0; i < keyValue.length; i++) {

            drawSingleKeyAttribute(canvas, keyBitmap[i], String.valueOf(keyValue[i]),
                    rect.left, rect.top + heightPiece * i, rect.right, rect.top + heightPiece * (i + 1));
        }
    }

    private Bitmap[] getKeyBitmap() {
        Bitmap[] bitmaps = new Bitmap[heroBean.keyImage.length];

        for (int i = 0; i < heroBean.keyImage.length; i++) {
            bitmaps[i] = imageManager.getBitmap(heroBean.keyImage[i]);
        }
        return bitmaps;
    }

    private void drawSingleKeyAttribute(Canvas canvas, Bitmap keyImage, String value,
                                        float left, float top, float right, float bottom) {
        float width = right - left;
        float height = bottom - top;
        float middle = width / 2f;

        float size = Math.min(height, middle) * 0.8f;
        float imageLeft = left + (middle - size) / 2f;
        float imageTop = top + (height - size) / 2f;
        rectTool.set((int) imageLeft, (int) imageTop, (int) (imageLeft + size), (int) (imageTop + size));
        canvas.drawBitmap(keyImage, null, rectTool, null);

        textPaint.getTextBounds(value, 0, value.length(), rectTool);
        float textBottom = bottom - (height - rectTool.height()) / 2;
        canvas.drawText(value, left + middle - rectTool.left, textBottom - rectTool.bottom, textPaint);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        boolean oldFocusHelp = focusHelp;
        boolean oldFocusFly = focusFly;
        boolean oldFocusHero = focusHero;

        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (bookRect.contains(x, y) && showHelpButton()) {
                    focusHelp = true;
                } else if (flyRect.contains(x, y) && showFlyButton()) {
                    focusFly = true;
                } else if (avatarRect.contains(x, y) && GameSharedPref.isOpenShopShortcut()) {
                    focusHero = true;
                } else {
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (focusHelp && !bookRect.contains(x, y)) {
                    focusHelp = false;
                } else if (focusFly && !flyRect.contains(x, y)) {
                    focusFly = false;
                } else if (focusHero && !avatarRect.contains(x, y)) {
                    focusHero = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (focusHelp) {
                    if (listener != null) {
                        listener.onEnemyProperty();
                    }
                    focusHelp = false;
                } else if (focusFly) {
                    if (listener != null) {
                        listener.onJumpFloor();
                    }
                    focusFly = false;
                } else if (focusHero) {
                    if (listener != null) {
                        listener.onShopShortcut();
                    }
                    focusHero = false;
                }
                break;
        }

        if (oldFocusFly != focusFly
                || oldFocusHelp != focusHelp
                || oldFocusHero != focusHero) {
            invalidate();
        }

        return true;
    }
}
