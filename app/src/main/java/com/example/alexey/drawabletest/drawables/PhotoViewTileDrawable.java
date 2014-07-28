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
public class PhotoViewTileDrawable extends TileDrawable {

    final static String TAG = PhotoViewTileDrawable.class.getSimpleName();

    private Rect scaledRect = new Rect();

    public PhotoViewTileDrawable(ImageView imageView, ViewGroup.LayoutParams layoutParams, int imageWidth, int imageHeight) {
        super(imageView, layoutParams, imageWidth, imageHeight);
    }

    @Override
    public void onInitViewSize(int width, int height) {
        super.onInitViewSize(width,height);
        invalidateSelf();
    }

    @Override
    protected Tile[] createTiles(){
        Tile[] tiles = new ColorTile[imageHeight % imageWidth > 0 ? (imageHeight / imageWidth + 1) : (imageHeight / imageWidth)];
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < tiles.length; i++) {
            float height = i * imageWidth + imageWidth;
            if (i == tiles.length - 1 && height % imageWidth > 0) {
                height = i * imageWidth + height % imageWidth;
            }
            tiles[i] = new ColorTile(new RectF(0, i * imageWidth, imageWidth, height), (random.nextInt() >> 8) | 0xff000000);
        }
        return tiles;
    }

    @Override
    public void draw(Canvas canvas) {
        for (int i = 0; i < tiles.length; i++) {
            tiles[i].draw(canvas);
        }
    }

    public void updateDrawable(RectF imageRect) {
        boolean needRedraw = false;
        ImageView imageView = weakParent.get();
        if (imageView != null) {
            float widthScaleFactor = imageRect.width() / imageWidth;
            float heightScaleFactor = imageRect.height() / imageHeight;
            imageView.getLocalVisibleRect(visibleRect);
            for (int i = 0; i < tiles.length; i++) {
                RectF sourceRect = tiles[i].getSourceRect();
                scaledRect.set((int) imageRect.left, (int) (imageRect.top + sourceRect.top * heightScaleFactor),
                        (int) imageRect.right, (int) (imageRect.top + (sourceRect.top + sourceRect.height()) * heightScaleFactor));
                if (Rect.intersects(visibleRect, scaledRect)) {
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


}