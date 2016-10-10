package com.example.jonat.popularmovienanodegree.Data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


/**
 * Created by jonat on 10/3/2016.
 */
public class MovieContract {
    //it should be unique in system,we use package name because it is unique
    public static final String CONTENT_AUTHORITY = "com.example.jonat.popularmovienanodegree.Data";

    //base URI for content provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //URI end points for Content provider
    public static final String PATH_FAV = "movies";


    //for favorites
    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAV).build();

        //these are MIME types ,not really but they are similar to MIME types
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAV;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAV;

        // Table name
        public static final String TABLE_NAME = "movies";
        //TMDB Movie id ; we will need this reviews and Trailer
        public static final String COLUMN_MOVIE_ID = "movie_id";
        //Movie title
        public static final String COLUMN_MOVIE_TITLE = "original_title";
        //Movie release date
        public static final String COLUMN_RELEASE_DATE = "release_date";
        //path for poster ; it's not actual URL, append it with base poster path with size. example
        // http://image.tmdb.org/t/p/{size}/{poster_path}
        public static final String COLUMN_POSTER_PATH = "poster_path";
        //vote average for Movie
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        //plot synopsis of Movie
        public static final String COLUMN_PLOT = "overview";

        //wallpaper for movies
        public static final String COLUMN_BACKDROP = "backdrop_path";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }
}
