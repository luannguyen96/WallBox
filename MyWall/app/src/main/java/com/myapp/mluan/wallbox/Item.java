package com.myapp.mluan.wallbox;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MinhLuan on 13-Jul-16.
 */
public class Item implements Parcelable {

    private Uri mImageId;


    public Item(Uri imageId) {

        mImageId = imageId;
    }

    public Uri getImageId() {
        return mImageId;
    }

    public void setImageId(Uri imageId) {
        mImageId = imageId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private Item(Parcel source){
        mImageId = Uri.parse(source.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mImageId.toString());
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>(){

        @Override
        public Item createFromParcel(Parcel source) {
            return new Item(source);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}
