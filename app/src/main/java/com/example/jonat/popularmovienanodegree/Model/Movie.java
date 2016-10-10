package com.example.jonat.popularmovienanodegree.Model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jonat on 8/9/2016.
 */
public class Movie implements Parcelable {
    private int id;
    private String title;
    private String release_date;
    private String poster;
    private String vote_average;
    private String overview;
    private String backdrop;

    public static final int COL_ID = 0;
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_MOVIE_TITLE = 2;
    public static final int COL_RELEASE_DATE = 3;
    public static final int COL_POSTER_PATH = 4;
    public static final int COL_VOTE_AVERAGE = 5;
    public static final int COL_PLOT = 6;
    public static final int COL_BACKDROP = 7;

    //movie() is overloaded
    public Movie() {
    }

    public Movie(JSONObject movie) throws JSONException {
        this.id = movie.getInt("id");
        this.title = movie.getString("original_title");
        this.release_date = movie.getString("release_date");
        this.poster = movie.getString("poster_path");
        this.vote_average = movie.getString("vote_average");
        this.overview = movie.getString("overview");
        this.backdrop = movie.getString("backdrop_path");

    }

    //when we ask for favorite movies we will do that via Cursor
    public Movie(Cursor cursor) {
        this.id = cursor.getInt(COL_MOVIE_ID);
        this.title = cursor.getString(COL_MOVIE_TITLE);
        this.release_date = cursor.getString(COL_RELEASE_DATE);
        this.poster = cursor.getString(COL_POSTER_PATH);
        this.vote_average = cursor.getString(COL_VOTE_AVERAGE);
        this.overview = cursor.getString(COL_PLOT);
        this.backdrop = cursor.getString(COL_BACKDROP);

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(release_date);
        dest.writeString(poster);
        dest.writeString(vote_average);
        dest.writeString(overview);
        dest.writeString(backdrop);
    }


    private Movie(Parcel in) {
        id = in.readInt();
        title  = in.readString();
        release_date = in.readString();
        poster = in.readString();
        vote_average = in.readString();
        overview = in.readString();
        backdrop = in.readString();
    }

    public String getBackdrop() {
        return backdrop;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPoster() {
        return poster;
    }

    public String getOverview() {
        return overview;
    }

    public String getVoteAverage() {
        return vote_average;
    }

    public String getReleaseDate() {
        return release_date;
    }
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}