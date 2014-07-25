package com.example.alexey.drawabletest;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.Random;

/**
 * Created by Alexey on 25.07.2014.
 */
public class ResizableDrawable extends Drawable implements ViewTreeObserver.OnGlobalLayoutListener {

    private final WeakReference<ImageView> weakParent;
    private final WeakReference<ViewGroup.LayoutParams> weakParentLayoutParams;
    private int squareSize = 0;
    private int currentSquareCount = 0;
    private final int targetSquareCount;
    private Handler handler = new Handler();
    private RectF lastDisplayRectF = null;


    private Rect visibleRect = new Rect();
    private Rect tempRect = new Rect();
    private RectF tempRectF = new RectF();
    private Paint paint = new Paint();
    private boolean[] visibleSquares = new boolean[0];
    private Paint[] paints = new Paint[0];

    public ResizableDrawable(ImageView parent, ViewGroup.LayoutParams layoutParams, int squareCount) {
        this.weakParent = new WeakReference<ImageView>(parent);
        this.weakParentLayoutParams = new WeakReference<ViewGroup.LayoutParams>(layoutParams);
        this.targetSquareCount = squareCount;

        this.paint.setAntiAlias(true);
        this.paint.setColor(Color.RED);

        ImageView iv = weakParent.get();
        if (iv != null) {
            if (iv.getMeasuredWidth() == 0 && iv.getMeasuredHeight() == 0) {
                ViewTreeObserver observer = iv.getViewTreeObserver();
                if (observer != null) {
                    observer.addOnGlobalLayoutListener(ResizableDrawable.this);
                }
            } else {
                setSquareCount(targetSquareCount);
            }
        }
//        this.handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                ImageView iv = weakParent.get();
//                if (iv != null) {
//                    if (iv.getMeasuredWidth() == 0 && iv.getMeasuredHeight() == 0) {
//                        ViewTreeObserver observer = iv.getViewTreeObserver();
//                        if (observer != null) {
//                            observer.addOnGlobalLayoutListener(ResizableDrawable.this);
//                        }
//                    } else {
//                        setSquareCount(targetSquareCount);
//                    }
//                }
//            }
//        }, 500);
    }

    @Override
    public void draw(Canvas canvas) {
        if (lastDisplayRectF != null) {
            paint.setColor(Color.CYAN);
            for (int i = 0; i < visibleSquares.length; i++) {
                if (visibleSquares[i] == true) {

                    getTileRect(lastDisplayRectF, tempRectF, i);
                    //paint.setColor(color[i]);//(paint.getColor() + 0xff);//(new Random().nextInt() >> 8) ^ 0xff000000);
                    canvas.drawRect(tempRectF, paints[i]);
                    canvas.drawRect(tempRectF.left, tempRectF.top, tempRectF.left + 4, tempRectF.top + 4, paint);
                    canvas.drawRect(tempRectF.right - 4, tempRectF.top, tempRectF.right, tempRectF.top + 4, paint);
                    canvas.drawRect(tempRectF.left, tempRectF.bottom - 4, tempRectF.left + 4, tempRectF.bottom, paint);
                    canvas.drawRect(tempRectF.right - 4, tempRectF.bottom - 4, tempRectF.right, tempRectF.bottom, paint);
                    Log.d("ResizableDrawable", String.format("Draw rect %s", tempRectF.toShortString()));
                }
            }
        } else {
            for (int i = 0; i < visibleSquares.length; i++) {
                if (visibleSquares[i] == true) {
                    tempRect.set(0, i * squareSize, squareSize, i * squareSize + squareSize);
                    paint.setColor(new Random().nextInt());
                    canvas.drawRect(tempRect, paint);
                }
            }
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
    public int getIntrinsicWidth() {
        return getBounds().width();
    }

    @Override
    public int getIntrinsicHeight() {
        return getBounds().height();
    }

    @Override
    public void onGlobalLayout() {
        ImageView imageView = weakParent.get();
        if (imageView != null) {
            ViewTreeObserver observer = imageView.getViewTreeObserver();
            if (observer != null) {
                if (Build.VERSION.SDK_INT > 16) {
                    observer.removeOnGlobalLayoutListener(ResizableDrawable.this);
                } else {
                    observer.removeGlobalOnLayoutListener(ResizableDrawable.this);
                }
            }
            setSquareCount(targetSquareCount);
        }
    }

    public void updateDrawable() {
        boolean needRedraw = false;
        ImageView imageView = weakParent.get();
        if (imageView != null) {
            imageView.getLocalVisibleRect(visibleRect);
            Log.d("ResizableDrawable", String.format("View %d Visible rect is %s", imageView.getId(), visibleRect.toShortString()));
            for (int i = 0; i < currentSquareCount; i++) {
                tempRect.set(0, i * squareSize, squareSize, i * squareSize + squareSize);
                if (Rect.intersects(visibleRect, tempRect)) {
                    if (visibleSquares[i] == false) {
                        visibleSquares[i] = true;
                        needRedraw = true;
                    }
                } else {
                    if (visibleSquares[i] == true) {
                        visibleSquares[i] = false;
                    }
                }
            }
        }

        StringBuilder builder = new StringBuilder(currentSquareCount * 2);
        for (int i = 0; i < visibleSquares.length; i++) {
            if (i != 0) {
                builder.append(',');
            }
            builder.append(visibleSquares[i] == true ? "1" : "0");
        }
        Log.d("ResizableDrawable", String.format("View %d Visible parts %s", imageView.getId(), builder.toString()));

        if (needRedraw) {
            Log.d("ResizableDrawable", "!REDRAW!");
            invalidateSelf();
        }
    }

    public void updateDrawable(RectF displayRect) {
        this.lastDisplayRectF = displayRect;
        boolean needRedraw = false;
        ImageView imageView = weakParent.get();
        if (imageView != null) {
            imageView.getLocalVisibleRect(visibleRect);
            //Log.d("ResizableDrawableF", String.format("View %d Visible rect is %s", imageView.getId(), visibleRect.toShortString()));
            for (int i = 0; i < currentSquareCount; i++) {
                getTileRect(displayRect, tempRectF, i);
                //tempRectF.set(0, i * squareSize, squareSize, i * squareSize + squareSize);
                if (RectF.intersects(new RectF(visibleRect), tempRectF)) {
                    if (visibleSquares[i] == false) {
                        visibleSquares[i] = true;
                        needRedraw = true;
                    }
                } else {
                    if (visibleSquares[i] == true) {
                        visibleSquares[i] = false;
                    }
                }
            }
        }

        StringBuilder builder = new StringBuilder(currentSquareCount * 2);
        for (int i = 0; i < visibleSquares.length; i++) {
            if (i != 0) {
                builder.append(',');
            }
            builder.append(visibleSquares[i] == true ? "1" : "0");
        }
        Log.d("ResizableDrawableF", String.format("View %d Visible parts %s", imageView.getId(), builder.toString()));

        if (needRedraw) {
            Log.d("ResizableDrawable", "!REDRAW!");
            invalidateSelf();
        }
    }

    private void getTileRect(RectF displayRect, RectF destRect, int tileIndex) {
        float widthFactor = displayRect.width() / getBounds().width();
        float heightFactor = displayRect.height() / getBounds().height();

        destRect.left = displayRect.left;
        destRect.right = displayRect.left + squareSize * widthFactor;
        destRect.top = displayRect.top + (tileIndex * squareSize) * heightFactor;
        destRect.bottom = destRect.top + (squareSize) * heightFactor;
    }

    private void setSquareCount(int count) {
        currentSquareCount = count;
        ImageView imageView = weakParent.get();
        ViewGroup.LayoutParams layoutParams = weakParentLayoutParams.get();
        if (imageView != null && layoutParams != null) {
            this.squareSize = imageView.getMeasuredWidth();
//            layoutParams.width = imageView.getMeasuredWidth();
//            layoutParams.height = imageView.getMeasuredWidth() * currentSquareCount;
            squareSize = imageView.getMeasuredWidth();
            setBounds(new Rect(0, 0, imageView.getMeasuredWidth(), imageView.getMeasuredWidth() * currentSquareCount));
            visibleSquares = new boolean[currentSquareCount];
            paints = new Paint[currentSquareCount];
            Random rnd = new Random();
            for (int i = 0; i < currentSquareCount; i++) {
                LinearGradient gradient = new LinearGradient(0, 0, squareSize, squareSize, Color.RED, Color.YELLOW, Shader.TileMode.CLAMP);
                paints[i] = new Paint();
                //paints[i].setShader(gradient);
                paints[i].setColor(rnd.nextInt() ^ 0xff000000);
            }
            //imageView.requestLayout();
            invalidateSelf();
        }
    }
}
