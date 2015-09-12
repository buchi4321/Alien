package com.cyanflxy.mapcreator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class MapCreateView extends View {

    private int widthPiece = 11;
    private int heightPiece = 11;
    private float pieceSize;

    private Paint linePaint;
    private Paint dotPaint;

    private Path outBorder;
    private Path dotPath;

    public MapCreateView(Context context, AttributeSet attrs) {
        super(context, attrs);

        linePaint = new Paint();
        linePaint.setColor(Color.GRAY);
        linePaint.setStrokeWidth(1);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);

        dotPaint = new Paint();
        dotPaint.setColor(Color.GRAY);
        dotPaint.setStrokeWidth(1);
        dotPaint.setAntiAlias(true);
        dotPaint.setStyle(Paint.Style.STROKE);
        dotPaint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));

        outBorder = new Path();
        dotPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        float size = Math.min(width / (float) widthPiece, height / (float) heightPiece);
        float fWidth = size * widthPiece;
        float fHeight = size * heightPiece;
        widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) fWidth, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) fHeight, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (Float.compare(size, pieceSize) == 0 || Float.compare(size, 0) <= 0) {
            return;
        }

        pieceSize = size;

        // 外环
        outBorder.reset();
        outBorder.moveTo(0, 0);
        outBorder.lineTo(0, fHeight);
        outBorder.lineTo(fWidth, fHeight);
        outBorder.lineTo(fWidth, 0);
        outBorder.close();

        //中间虚线
        dotPath.reset();
        for (int i = 1; i < widthPiece; i++) {
            float w = i * pieceSize;
            dotPath.moveTo(w, 0);
            dotPath.lineTo(w, fHeight);
        }

        for (int i = 1; i < heightPiece; i++) {
            float h = i * pieceSize;
            dotPath.moveTo(0, h);
            dotPath.lineTo(fWidth, h);
        }
    }

    public void setPiece(int w, int h) {
        widthPiece = w;
        heightPiece = h;

        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawPath(outBorder, linePaint);
        canvas.drawPath(dotPath, dotPaint);

    }
}
