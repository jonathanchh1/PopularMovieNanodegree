package com.example.jonat.popularmovienanodegree.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jonat on 10/5/2016.
 */
public class Review implements Parcelable{
    @SerializedName("id")
    private String id;
    @SerializedName("author")
    private String Author;
    @SerializedName("content")
    private String Content;
    @SerializedName("url")
    private String Url;

    protected Review(Parcel in) {
        id = in.readString();
        Author = in.readString();
        Content = in.readString();
        Url = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);

        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public String getContent(){
        return Content;
    }

    public String getAuthor() {
        return Author;
    }

    public String getUrl() {
        return Url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(Author);
        dest.writeString(Content);
        dest.writeString(Url);
    }
}
