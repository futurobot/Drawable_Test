package com.example.alexey.drawabletest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.alexey.drawabletest.drawables.PhotoViewTileDrawable;

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

        imageView.setImageDrawable(new PhotoViewTileDrawable(imageView, imageView.getLayoutParams(), 800, 8 * 800));
        imageView.setOnMatrixChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_list_drawable) {
            startActivity(new Intent(this, MyActivity.class));
            return true;
        } else if (id == R.id.action_photoview_drawable) {
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
        if (drawable instanceof PhotoViewTileDrawable) {
            ((PhotoViewTileDrawable) drawable).updateDrawable(rect);
        }
    }
}
