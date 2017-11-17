package com.example.qiaowenhao.weblog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by qiaowenhao on 17-11-17.
 */

public class MyAdapter extends ArrayAdapter {
    private Context mContext;
    LayoutInflater mLayoutInflater;
    ListView mListView;
    LoadingImageAsyncTask mLoadingImageAsyncTask;


    public MyAdapter(Context context) {
        super(context, R.layout.list_item, Images.imageUrls);
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mLoadingImageAsyncTask = new LoadingImageAsyncTask();
    }

    @Override
    public int getCount() {
        return Images.imageUrls.length;
    }

    @Override
    public Object getItem(int i) {
        return Images.imageUrls[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        mListView = (ListView) viewGroup;
        if (view == null) {
            //view可以避免多次多次使用inflate加载布局
            view = mLayoutInflater.inflate(R.layout.list_item, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = view.findViewById(R.id.image);
            view.setTag(viewHolder);
        }
        //viewHolder避免多次findViewById
        viewHolder = (ViewHolder) view.getTag();
        viewHolder.imageView.setTag(Images.imageUrls[i]);
        showImageByAsyncTask(Images.imageUrls[i]);
        return view;
    }

    public class ViewHolder {
        ImageView imageView;
    }


    private void showImageByAsyncTask(String imageUrl) {
        mLoadingImageAsyncTask.execute(imageUrl);
    }

    public class LoadingImageAsyncTask extends AsyncTask<String, Void, Bitmap> {
        private String url;
        @Override
        protected Bitmap doInBackground(String... strings) {
            url = strings[0];
            Bitmap bitmap = getBitmapFromUrl(url);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            //避免由于view的复用导致的图片的错乱
            ImageView imageView = mListView.findViewWithTag(url);
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    private Bitmap getBitmapFromUrl(String url) {
        Bitmap bitmap = null;
        URL realUrl;
        try {
            realUrl = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) realUrl.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            bitmap = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


}
