package com.example.alexey.drawabletest;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Alexey on 25.07.2014.
 */
public class PhotoViewLibraryActivity extends Activity implements PhotoViewAttacher.OnMatrixChangedListener {
    PhotoView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photoview_activity);

        imageView = (PhotoView) findViewById(R.id.imageView);

        imageView.setOnMatrixChangeListener(PhotoViewLibraryActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_first_pic:
                try { imageView.setImageDrawable(new BitmapDrawable(getResources(),getAssets().open("1.jpg")));} catch (IOException e) {}
                return true;
            case R.id.action_second_pic:
                try { imageView.setImageDrawable(new BitmapDrawable(getResources(),getAssets().open("2.jpg")));} catch (IOException e) {}
                return true;
            case R.id.action_third_pic:
                try { imageView.setImageDrawable(new BitmapDrawable(getResources(),getAssets().open("3.jpg")));} catch (IOException e) {}
                return true;
            case R.id.action_custom_pic:
                imageView.setImageDrawable(new ResizableDrawable(imageView, imageView.getLayoutParams(), 8));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

    }


    @Override
    public void onMatrixChanged(RectF rect) {
        Log.d("Activity", String.format("Scale %f Rect %s", imageView.getScale(), rect.toShortString()));
        Drawable drawable = imageView.getDrawable();
        if (drawable instanceof ResizableDrawable) {
            ((ResizableDrawable) drawable).updateDrawable(rect);
        }
    }
}
