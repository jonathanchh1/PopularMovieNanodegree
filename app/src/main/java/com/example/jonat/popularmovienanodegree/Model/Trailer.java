package com.example.jonat.popularmovienanodegree.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jonat on 10/5/2016.
 */
public class Trailer implements Parcelable {
    @SuppressWarnings("unused")
    public static final String LOG_TAG = Trailer.class.getSimpleName();

    @SerializedName("id")
    private String Id;
    @SerializedName("key")
    private String Key;
    @SerializedName("name")
    private String Name;
    @SerializedName("site")
    private String Site;
    @SerializedName("size")
    private String Size;


    protected Trailer(Parcel in) {
        Id = in.readString();
        Key = in.readString();
        Name = in.readString();
        Site = in.readString();
        Size = in.readString();
    }

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    public String getId() {
        return Id;
    }

    public String getKey() {
        return Key;
    }

    public String getTrailerUrl() {
        return "http://www.youtube.com/watch?v=" + Key;
    }

    public String getName() {
        return Name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Id);
        dest.writeString(Key);
        dest.writeString(Name);
        dest.writeString(Site);
        dest.writeString(Size);
    }
}
