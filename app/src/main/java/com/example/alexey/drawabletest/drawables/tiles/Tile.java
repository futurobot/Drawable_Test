package com.example.alexey.drawabletest.drawables.tiles;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by Alexey on 28.07.2014.
 */
public abstract class Tile {

    public final RectF sourceRect;
    protected final RectF scaledRect = new RectF();
    protected final Rect scaledRectRound = new Rect();

    protected boolean isVisible = false;

    public Tile(RectF sourceRect){
        this.sourceRect = sourceRect;
        setDrawingRect(sourceRect);
    }

    public abstract void draw(Canvas canvas);

    public void setDrawingRect(RectF rect){
        setDrawingRect(rect.left,rect.top,rect.right, rect.bottom);
    }

    public void setDrawingRect(float left, float top, float right, float bottom){
        this.scaledRect.set(left, top, right, bottom);
        this.scaledRect.round(scaledRectRound);
    }

    public void setVisible(boolean isVisible){
        this.isVisible = isVisible;
    }

    public RectF getDrawingRect() {
        return scaledRect;
    }

    public Rect getDrawingRectRound() {
        return scaledRectRound;
    }

    public RectF getSourceRect(){
        return sourceRect;
    }

    public boolean isVisible(){
        return isVisible;
    }

}
