package com.myapp.mluan.wallbox;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.ArrayList;

/**
 * Created by MinhLuan on 17-Jul-16.
 */
public class ViewPagerAdapter extends PagerAdapter {
    Context mContext;
    ArrayList<Item> mArrayList;
    public int pos;
    ProgressDialog pDialog;
    ImageView imgView;
    int width;
    public static final String KeySharedReferences = "com.myapp.mluan.keypref";
    public static final String KEY_POSITION_DOWNLOAD = "KEY_POSITION_DOWNLOAD";


    public ViewPagerAdapter(Context context, ArrayList<Item> array){
        mContext = context;
        mArrayList = array;
    }





    public Object instantiateItem(View collection, int position){
        imgView = new ImageView(mContext);
        imgView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        ((Activity) mContext).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        width = mContext.getResources().getDisplayMetrics().widthPixels;



        new loadImage().execute(mArrayList.get(position).getImageId());

        SharedPreferences sharedPref = mContext.getSharedPreferences(KeySharedReferences,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(KEY_POSITION_DOWNLOAD, position);
        editor.commit();

        ((ViewPager) collection).addView(imgView, 0);
        boolean run = false;
        if(imgView != null){
            run = true;
        }
        return imgView;
    }




    @Override
    public int getCount() {
        return mArrayList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public Parcelable saveState() {
        return null;
    }


    public class loadImage extends AsyncTask<Uri, Void, Uri> {
        ProgressDialog mDialog = new ProgressDialog(mContext);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.setMessage("Loading");
            mDialog.setIndeterminate(false);
            mDialog.setMax(100);
            mDialog.setCancelable(true);
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDialog.show();


        }




        @Override
        protected Uri doInBackground(Uri... params) {
            return params[0];
        }

        @Override
        protected void onPostExecute(Uri uri) {
            super.onPostExecute(uri);

            DisplayMetrics displaymetrics = new DisplayMetrics();
            ((Activity) mContext).getWindowManager()
                    .getDefaultDisplay()
                    .getMetrics(displaymetrics);
            int height = displaymetrics.heightPixels;
            int width = displaymetrics.widthPixels;

            if (uri.toString().contains("mobileswall") || uri.toString().contains("simpledesktops")) {
                imgView.setScaleType(ImageView.ScaleType.FIT_XY);
                Glide.with(mContext).load(uri).override(width/3, height/3).centerCrop().
                        diskCacheStrategy(DiskCacheStrategy.RESULT).
                        into(new GlideDrawableImageViewTarget(imgView) {
                            @Override
                            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                Toast.makeText(mContext, "cannot load this image", Toast.LENGTH_SHORT).show();
                                Glide.with(mContext).load(mArrayList.get(pos).getImageId()).error(android.R.color.darker_gray)
                                        .fitCenter().into(imgView);
                            }

                            @Override
                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                                super.onResourceReady(resource, animation);
                                if (mDialog.isShowing()) {
                                    mDialog.dismiss();
                                }

                            }
                        });


            }
            else{
                imgView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                Glide.with(mContext).load(uri).override(width / 3, height / 3).fitCenter().
                        diskCacheStrategy(DiskCacheStrategy.RESULT).
                        into(new GlideDrawableImageViewTarget(imgView) {
                            @Override
                            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                Toast.makeText(mContext, "cannot load this image", Toast.LENGTH_SHORT).show();
                                Glide.with(mContext).load(mArrayList.get(pos).getImageId()).error(android.R.color.darker_gray)
                                        .fitCenter().into(imgView);
                            }

                            @Override
                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                                super.onResourceReady(resource, animation);
                                if (mDialog.isShowing()) {
                                    mDialog.dismiss();
                                }
                            }
                        });
            }




        }
    }

}
