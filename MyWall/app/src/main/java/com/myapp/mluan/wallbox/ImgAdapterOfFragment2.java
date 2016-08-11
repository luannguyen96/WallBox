package com.myapp.mluan.wallbox;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

/**
 * Created by MinhLuan on 27-Jul-16.
 */
public class ImgAdapterOfFragment2 extends BaseAdapter {
    private Context mContext;
    private ArrayList<ItemOfFragment2> item = new ArrayList<>();
    ViewHolder viewHolder;


    public ImgAdapterOfFragment2(Context context, ArrayList<ItemOfFragment2> temp) {
        mContext = context;
        item = temp;
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
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_listview,null);
            viewHolder = new ViewHolder();
            viewHolder.imgItem = (ImageView) convertView.findViewById(R.id.img_listview);
            viewHolder.txtItem = (TextView) convertView.findViewById(R.id.text_listview);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

/*        viewHolder.imgItem.setScaleType(ImageView.ScaleType.FIT_CENTER);*/
        int height= mContext.getResources().getDisplayMetrics().heightPixels;
        Picasso.with(mContext).load(item.get(position).getImgId()).resize(parent.getWidth(), height/5).
                transform(new BlurTransformation(mContext)).centerCrop()
                .error(android.R.color.darker_gray).into(viewHolder.imgItem);
        viewHolder.txtItem.setText(item.get(position).getTxtId());



        return convertView;
    }

    private class ViewHolder {
        ImageView imgItem;
        TextView txtItem;
    }


    public class BlurTransformation implements Transformation {

        RenderScript rs;

        public BlurTransformation(Context context) {
            super();
            rs = RenderScript.create(context);
        }

        @Override
        public Bitmap transform(Bitmap bitmap) {
            // Create another bitmap that will hold the results of the filter.
            Bitmap blurredBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

            // Allocate memory for Renderscript to work with
            Allocation input = Allocation.createFromBitmap(rs, blurredBitmap, Allocation.MipmapControl.MIPMAP_FULL, Allocation.USAGE_SHARED);
            Allocation output = Allocation.createTyped(rs, input.getType());

            // Load up an instance of the specific script that we want to use.
            ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setInput(input);

            // Set the blur radius
            script.setRadius(10);

            // Start the ScriptIntrinisicBlur
            script.forEach(output);

            // Copy the output to the blurred bitmap
            output.copyTo(blurredBitmap);

            bitmap.recycle();

            return blurredBitmap;
        }

        @Override
        public String key() {
            return "blur";
        }
    }
}
