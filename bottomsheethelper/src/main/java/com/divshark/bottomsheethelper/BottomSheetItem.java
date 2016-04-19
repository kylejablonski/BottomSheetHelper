package com.divshark.bottomsheethelper;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kyle.jablonski on 4/18/16.
 */
public class BottomSheetItem implements Parcelable {

    private Bitmap bitmap;
    public Drawable drawable;
    public String title;
    public String className;
    public String packageName;

    public BottomSheetItem(){}

    public BottomSheetItem(Parcel source){
        bitmap = source.readParcelable(getClass().getClassLoader());
        drawable = new BitmapDrawable(bitmap);
        title = source.readString();
        className = source.readString();
        packageName = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        dest.writeParcelable(bitmap, flags);
        dest.writeString(title);
        dest.writeString(className);
        dest.writeString(packageName);
    }

    public static final Parcelable.Creator<BottomSheetItem> CREATOR = new Parcelable.Creator<BottomSheetItem>() {
        @Override
        public BottomSheetItem createFromParcel(Parcel source) {
            return new BottomSheetItem(source);
        }

        @Override
        public BottomSheetItem[] newArray(int size) {
            return new BottomSheetItem[size];
        }
    };
}
