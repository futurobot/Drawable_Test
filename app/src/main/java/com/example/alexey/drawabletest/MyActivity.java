package com.example.alexey.drawabletest;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;


public class MyActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        ListView listView = (ListView) findViewById(R.id.list);
        MyAdapter adapter = new MyAdapter();
        listView.setAdapter(adapter);
        listView.setOnScrollListener(adapter);
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class MyAdapter extends BaseAdapter implements AbsListView.OnScrollListener {

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Integer getItem(int position) {
            return position + 1;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item, parent, false);
            }
            final ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
            imageView.setImageDrawable(new ResizableDrawable(imageView, imageView.getLayoutParams(), getItem(position)));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageView.invalidate();
                }
            });

            return convertView;
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            int last = firstVisibleItem + visibleItemCount;
            for (int i = firstVisibleItem; i < last; i++) {
                View itemView = view.getChildAt(i - firstVisibleItem);
                ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
                Drawable drawable = imageView.getDrawable();
                if(drawable instanceof ResizableDrawable){
                    ((ResizableDrawable)drawable).updateDrawable();
                }
            }
        }
    }
}
