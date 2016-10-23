package com.wulinpeng.weicommunity.repost.entity;

import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author wulinpeng
 * @datetime: 16/10/23 下午4:09
 * @description:
 */
public class ImageInfo implements Parcelable {

    private boolean selected = false;

    private String path;

    protected ImageInfo(Parcel in) {
        selected = in.readByte() != 0;
        path = in.readString();
    }

    public ImageInfo(){}

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public static final Creator<ImageInfo> CREATOR = new Creator<ImageInfo>() {
        @Override
        public ImageInfo createFromParcel(Parcel in) {
            return new ImageInfo(in);
        }

        @Override
        public ImageInfo[] newArray(int size) {
            return new ImageInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (selected ? 1 : 0));
        dest.writeString(path);
    }
}
