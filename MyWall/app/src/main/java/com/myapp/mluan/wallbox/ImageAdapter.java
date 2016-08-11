package com.myapp.mluan.wallbox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by MinhLuan on 13-Jul-16.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Item> item = new ArrayList<>();
    int width;
    ViewHolder viewHolder;

    public ImageAdapter(Context context, ArrayList<Item> uriArrayList) {
        mContext = context;
        item = uriArrayList;
    }

    @Override
    public int getCount() {
        return item.size();
    }


    @Override
    public Object getItem(int position) {
        return item.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        final ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item,null);
            viewHolder = new ViewHolder();
            viewHolder.imgItem = (ImageView) convertView.findViewById(R.id.img);



            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }


        width= mContext.getResources().getDisplayMetrics().widthPixels;
        Picasso.with(mContext).load(item.get(position).getImageId()).resize(width/4,width/4).into(viewHolder.imgItem);




        return convertView;
    }



    private static class ViewHolder {
        ImageView imgItem;


    }


}
