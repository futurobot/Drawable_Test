package com.example.alexey.drawabletest.drawables;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.alexey.drawabletest.drawables.tiles.ColorTile;
import com.example.alexey.drawabletest.drawables.tiles.Tile;

import java.util.Random;

/**
 * Created by Alexey on 28.07.2014.
 */
public class ListViewTileDrawable extends TileDrawable {
    final static String TAG = ListViewTileDrawable.class.getSimpleName();

    private boolean isInitialized;

    public ListViewTileDrawable(ImageView imageView, ViewGroup.LayoutParams layoutParams, int imageWidth, int imageHeight) {
        super(imageView, layoutParams, imageWidth, imageHeight);
    }

    @Override
    public void onInitViewSize(int width, int height) {
        super.onInitViewSize(width, height);
        ImageView imageView = weakParent.get();
        if (imageView == null) {
            return;
        }
        isInitialized = true;
        float scaleFactor = (float) width / imageWidth;
        for (int i = 0; i < tiles.length; i++) {
            float tileHeight = width;
            if (i == tiles.length - 1 && imageHeight % imageWidth > 0) {
                tileHeight = width + scaleFactor * (imageHeight % imageWidth);
            }
            tiles[i].setDrawingRect(0f, i * width, width, tileHeight);
        }

        ViewGroup.LayoutParams layoutParams = weakParentLayoutParams.get();
        if (layoutParams == null) {
            return;
        }
        layoutParams.height = (int) (imageHeight * scaleFactor);
        imageView.setLayoutParams(layoutParams);
        imageView.requestLayout();
        updateDrawable();
    }

    @Override
    public void draw(Canvas canvas) {
        if (isInitialized) {
            super.draw(canvas);
        }
    }

    public void updateDrawable() {
        if (!isInitialized) {
            return;
        }

        boolean needRedraw = false;
        ImageView imageView = weakParent.get();
        if (imageView != null) {
            imageView.getLocalVisibleRect(visibleRect);
            if (DEBUG) {
                Log.d("ResizableDrawable", String.format("View %d Visible rect is %s", imageView.getId(), visibleRect.toShortString()));
            }
            for (int i = 0; i < tiles.length; i++) {
                if (Rect.intersects(visibleRect, tiles[i].getDrawingRectRound())) {
                    if (tiles[i].isVisible() == false) {
                        tiles[i].setVisible(true);
                        needRedraw = true;
                    }
                } else {
                    if (tiles[i].isVisible() == true) {
                        tiles[i].setVisible(false);
                    }
                }
            }
        }

        if (DEBUG) {
            StringBuilder builder = new StringBuilder(tiles.length * 2);
            for (int i = 0; i < tiles.length; i++) {
                if (i != 0) {
                    builder.append(',');
                }
                builder.append(tiles[i].isVisible() ? "1" : "0");
            }

            Log.d("ResizableDrawableF", String.format("View %d Visible parts %s", imageView.getId(), builder.toString()));
        }

        if (needRedraw) {
            if (DEBUG) {
                Log.d("ResizableDrawable", "!REDRAW!");
            }
            invalidateSelf();
        }
    }

    @Override
    protected Tile[] createTiles() {
        Tile[] tiles = new Tile[imageHeight % imageWidth > 0 ? (imageHeight / imageWidth + 1) : (imageHeight / imageWidth)];

        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < tiles.length; i++) {
            float imageHeight = i * imageWidth + imageWidth;
            if (i == tiles.length - 1 && imageHeight % imageWidth > 0) {
                imageHeight = i * imageWidth + imageHeight % imageWidth;
            }
            tiles[i] = new ColorTile(new RectF(0, i * imageWidth, imageWidth, imageHeight), (random.nextInt() >> 8) | 0xff000000);
        }
        return tiles;
    }
}
