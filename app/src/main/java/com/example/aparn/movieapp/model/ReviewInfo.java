package com.example.aparn.movieapp.model;


import android.os.Parcel;
import android.os.Parcelable;

public class ReviewInfo implements Parcelable {
    private final String author;
    private final String content;

    public ReviewInfo(String author, String content) {
        this.author = author;
        this.content = content;
    }

    private ReviewInfo(Parcel in) {
        this.author = in.readString();
        this.content = in.readString();
    }

    public static final Creator<ReviewInfo> CREATOR = new Creator<ReviewInfo>() {
        @Override
        public ReviewInfo createFromParcel(Parcel in) {
            return new ReviewInfo(in);
        }

        @Override
        public ReviewInfo[] newArray(int size) {
            return new ReviewInfo[size];
        }
    };

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(content);
    }
}

