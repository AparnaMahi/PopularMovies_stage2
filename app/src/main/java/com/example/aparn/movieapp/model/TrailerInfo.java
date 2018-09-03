package com.example.aparn.movieapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TrailerInfo implements Parcelable{

    final String trailer_id;
    final String trailer_key;

    public String getId() {
        return trailer_id;
    }

    public String getKey() {
        return trailer_key;
    }

    public TrailerInfo(String trailer_id, String trailer_key) {
        this.trailer_id = trailer_id;
        this.trailer_key = trailer_key;
    }

    protected TrailerInfo(Parcel in) {
        trailer_id = in.readString();
        trailer_key = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(trailer_id);
        dest.writeString(trailer_key);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TrailerInfo> CREATOR = new Creator<TrailerInfo>() {
        @Override
        public TrailerInfo createFromParcel(Parcel in) {
            return new TrailerInfo(in);
        }

        @Override
        public TrailerInfo[] newArray(int size) {
            return new TrailerInfo[size];
        }
    };
}
