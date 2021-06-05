package com.example.mandatorysnapchat.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Snap implements Parcelable {

    private String ID;
    private String title;

    public Snap(String ID, String title) {
        this.ID = ID;
        this.title = title;
    }

    protected Snap(Parcel in) {
        ID = in.readString();
        title = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ID);
        dest.writeString(title);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Snap> CREATOR = new Creator<Snap>() {
        @Override
        public Snap createFromParcel(Parcel in) {
            return new Snap(in);
        }

        @Override
        public Snap[] newArray(int size) {
            return new Snap[size];
        }
    };

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    @Override
    public String toString() {
        return "Snap{" +
                "title='" + title + '\'' +
                '}';
    }
}
