package com.example.alexey.drawabletest.drawables;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.example.alexey.drawabletest.drawables.tiles.Tile;

import java.lang.ref.WeakReference;

/**
 * Created by Alexey on 28.07.2014.
 */
public abstract class TileDrawable extends Drawable implements ViewTreeObserver.OnGlobalLayoutListener {

    final static String TAG = TileDrawable.class.getSimpleName();
    protected final static boolean DEBUG = true;

    protected final WeakReference<ImageView> weakParent;
    protected final WeakReference<ViewGroup.LayoutParams> weakParentLayoutParams;
    protected final Tile[] tiles;
    protected final int imageWidth;
    protected final int imageHeight;

    protected Rect visibleRect = new Rect();

    public TileDrawable(ImageView imageView, ViewGroup.LayoutParams layoutParams, int imageWidth, int imageHeight) {
        this.weakParent = new WeakReference<ImageView>(imageView);
        this.weakParentLayoutParams = new WeakReference<ViewGroup.LayoutParams>(layoutParams);
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.tiles = createTiles();

        setBounds(0, 0, imageWidth, imageHeight);

        if (imageView.getMeasuredWidth() == 0 && imageView.getMeasuredHeight() == 0) {
            ViewTreeObserver observer = imageView.getViewTreeObserver();
            if (observer != null) {
                observer.addOnGlobalLayoutListener(this);
            }
        } else {
            onInitViewSize(imageView.getMeasuredWidth(), imageView.getMeasuredHeight());
        }
    }

    protected abstract Tile[] createTiles();

    public void onInitViewSize(int width, int height){
        ImageView imageView = weakParent.get();
        if(imageView != null){
            imageView.getLocalVisibleRect(visibleRect);
        }
    }

    @Override
    public int getIntrinsicWidth() {
        return imageWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return imageHeight;
    }

    @Override
    public void draw(Canvas canvas) {
        for (int i = 0; i < tiles.length; i++) {
            tiles[i].draw(canvas);
        }
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }

    @Override
    public void onGlobalLayout() {
        ImageView imageView = weakParent.get();
        if (imageView != null) {
            ViewTreeObserver observer = imageView.getViewTreeObserver();
            if (observer != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    observer.removeOnGlobalLayoutListener(this);
                } else {
                    observer.removeGlobalOnLayoutListener(this);
                }
            }
            imageView.getLocalVisibleRect(visibleRect);
            onInitViewSize(imageView.getMeasuredWidth(), imageView.getMeasuredHeight());
        }
    }
}
