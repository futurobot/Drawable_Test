package com.example.alexey.drawabletest;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
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

    private final static boolean NOT_DEBUG = true;

    private final WeakReference<ImageView> weakParent;
    private final WeakReference<ViewGroup.LayoutParams> weakParentLayoutParams;
    private int squareSize = 0;
    private int currentSquareCount = 0;
    private final int targetSquareCount;
    private Handler handler = new Handler();
    private RectF lastDisplayRectF = null;


    private RectF sourceRect = null;
    private Rect visibleRect = new Rect();
    private Rect tempRect = new Rect();
    private RectF tempRectF = new RectF();
    private Paint paint = new Paint();
    private boolean[] visibleSquares = new boolean[0];
    private Paint[] paints = new Paint[0];

    private Matrix matrix = new Matrix();

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
            paint.setColor(Color.RED);

//            canvas.drawRect(lastDisplayRectF, paint);
//            paint.setColor(Color.CYAN);

            float size = 20;
            float widthSide = (getBounds().width() * lastDisplayRectF.width()) / sourceRect.width();
            float heightSide = (getBounds().height() * lastDisplayRectF.height()) / sourceRect.height();
            canvas.drawRect(lastDisplayRectF.centerX() - widthSide / 2,lastDisplayRectF.top,lastDisplayRectF.centerX() + widthSide / 2, lastDisplayRectF.top + heightSide, paint);
            paint.setColor(Color.CYAN);

            Log.d("Draw", String.format("Drawing rect size %s canvas size %s", lastDisplayRectF.toShortString(), canvas.getClipBounds().toShortString()));
            for (int i = 0; i < visibleSquares.length; i++) {
                //if (visibleSquares[i] == true) {

                getTileRect(lastDisplayRectF, tempRectF, i);
                //paint.setColor(color[i]);//(paint.getColor() + 0xff);//(new Random().nextInt() >> 8) ^ 0xff000000);
                canvas.drawRect(tempRectF, paints[i]);
                canvas.drawRect(tempRectF.left, tempRectF.top, tempRectF.left + size, tempRectF.top + size, paint);
                canvas.drawRect(tempRectF.right - size, tempRectF.top, tempRectF.right, tempRectF.top + size, paint);
                canvas.drawRect(tempRectF.left, tempRectF.bottom - size, tempRectF.left + size, tempRectF.bottom, paint);
                canvas.drawRect(tempRectF.right - size, tempRectF.bottom - size, tempRectF.right, tempRectF.bottom, paint);
                paint.setStrokeWidth(20);
                canvas.drawLine(tempRect.left,tempRect.top,tempRect.right,tempRect.bottom,paint);
                paint.setStrokeWidth(1);
                if (!NOT_DEBUG) {
                    Log.d("ResizableDrawable", String.format("Draw rect %s", tempRectF.toString()));
                }
                //}
            }
            paint.setColor(Color.GREEN);
            canvas.drawRect(0, 0, 30, 30, paint);
        } else {
            for (int i = 0; i < visibleSquares.length; i++) {
                //if (visibleSquares[i] == true) {
                tempRect.set(0, i * squareSize, squareSize, i * squareSize + squareSize);
                paint.setColor(new Random().nextInt());
                canvas.drawRect(tempRect, paint);
                //}
                //}
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
            if (!NOT_DEBUG) {
                Log.d("ResizableDrawable", String.format("View %d Visible rect is %s", imageView.getId(), visibleRect.toShortString()));
            }
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
        if (!NOT_DEBUG) {
            Log.d("ResizableDrawable", String.format("View %d Visible parts %s", imageView.getId(), builder.toString()));
        }

        if (needRedraw) {
            if (!NOT_DEBUG) {
                Log.d("ResizableDrawable", "!REDRAW!");
            }
            invalidateSelf();
        }
    }

    public void updateDrawable(RectF displayRect) {
        if (sourceRect == null) {
            sourceRect = new RectF(displayRect);
        }

        this.lastDisplayRectF =     displayRect;
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

        if (!NOT_DEBUG) {
            StringBuilder builder = new StringBuilder(currentSquareCount * 2);
            for (int i = 0; i < visibleSquares.length; i++) {
                if (i != 0) {
                    builder.append(',');
                }
                builder.append(visibleSquares[i] == true ? "1" : "0");
            }

            Log.d("ResizableDrawableF", String.format("View %d Visible parts %s", imageView.getId(), builder.toString()));
        }

        if (needRedraw) {
            if (!NOT_DEBUG) {
                Log.d("ResizableDrawable", "!REDRAW!");
            }
            invalidateSelf();
        }
    }

    private void getTileRect(RectF displayRect, RectF destRect, int tileIndex) {
        float widthFactor = getBounds().width() / displayRect.width();
        float heightFactor = getBounds().height() / displayRect.height();

        float widthSide = (getBounds().width() * displayRect.width()) / sourceRect.width();
        float heightSide = (getBounds().height() * displayRect.height()) / sourceRect.height();

        destRect.left = displayRect.centerX() - widthSide / 2;
        destRect.top = displayRect.top + tileIndex * widthSide;
        destRect.right = displayRect.centerX() + widthSide / 2;
        destRect.bottom = destRect.top + widthSide;
    }

    private void setSquareCount(int count) {
        currentSquareCount = count;
        ImageView imageView = weakParent.get();
        ViewGroup.LayoutParams layoutParams = weakParentLayoutParams.get();
        if (imageView != null && layoutParams != null) {
            this.squareSize = imageView.getMeasuredWidth();
//            layoutParams.width = imageView.getMeasuredWidth();
//            layoutParams.height = imageView.getMeasuredWidth() * currentSquareCount;
            setBounds(new Rect(0, 0, imageView.getMeasuredWidth(), imageView.getMeasuredWidth() * currentSquareCount));
            visibleSquares = new boolean[currentSquareCount];
            paints = new Paint[currentSquareCount];
            Random rnd = new Random();
            for (int i = 0; i < currentSquareCount; i++) {
                LinearGradient gradient = new LinearGradient(0, 0, squareSize, squareSize, Color.RED, Color.YELLOW, Shader.TileMode.CLAMP);
                paints[i] = new Paint();
                //paints[i].setShader(gradient);
                paints[i].setColor((rnd.nextInt() >> 8) | 0xff000000);
            }
//
//            RectF mSrcDrawableRect = new RectF(0, 0, getBounds().width(), getBounds().height());
//            RectF mDstDrawableRect = new RectF(0, 0, imageView.getMeasuredWidth(), imageView.getMeasuredHeight());
//
//            Matrix matrix = new Matrix();
//            matrix.setRectToRect(mSrcDrawableRect, mDstDrawableRect, Matrix.ScaleToFit.CENTER);
//            sourceRect = mDstDrawableRect;
//            matrix.mapRect(sourceRect);
        }
    }
}
