package com.example.alexey.drawabletest.drawables.tiles;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by Alexey on 28.07.2014.
 */
public class ColorTile extends Tile {

    public final int color;

    private Paint paint;
    private Paint linePaint;


    public ColorTile(RectF sourceRect, int color) {
        super(sourceRect);
        this.color = color;

        paint = new Paint();
        paint.setColor(color);

        linePaint = new Paint();
        linePaint.setColor(Color.CYAN);
        linePaint.setStrokeWidth(5f);
    }


    @Override
    public void draw(Canvas canvas) {
        if(isVisible) {
            canvas.drawRect(sourceRect, paint);
            canvas.drawLine(sourceRect.left, sourceRect.top, sourceRect.right, sourceRect.bottom, linePaint);
            canvas.drawLine(sourceRect.right, sourceRect.top, sourceRect.left, sourceRect.bottom, linePaint);
        }
    }
}
