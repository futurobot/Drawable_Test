package com.example.alexey.drawabletest;

import android.app.Activity;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Alexey on 25.07.2014.
 */
public class PhotoViewLibraryActivity extends Activity implements PhotoViewAttacher.OnMatrixChangedListener{
    PhotoView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photoview_activity);

        imageView = (PhotoView) findViewById(R.id.imageView);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        imageView.setImageDrawable(new ResizableDrawable(imageView, imageView.getLayoutParams(), 8));
        imageView.setMaximumScale(10f);
        imageView.setMediumScale(7f);
        imageView.setMinimumScale(5f);
        imageView.setOnMatrixChangeListener(PhotoViewLibraryActivity.this);
    }


    @Override
    public void onMatrixChanged(RectF rect) {
        Drawable drawable = imageView.getDrawable();
        if(drawable instanceof ResizableDrawable){
            ((ResizableDrawable)drawable).updateDrawable(rect);
        }
    }
}
