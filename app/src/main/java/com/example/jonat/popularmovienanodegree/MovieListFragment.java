package com.example.jonat.popularmovienanodegree;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.jonat.popularmovienanodegree.Adapters.ImageAdapter;
import com.example.jonat.popularmovienanodegree.Data.Constants;
import com.example.jonat.popularmovienanodegree.Data.MovieContract;
import com.example.jonat.popularmovienanodegree.Model.Movie;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonat on 9/29/2016.
 */public class MovieListFragment extends Fragment {
    private final String LOG_TAG = MovieListFragment.class.getSimpleName();
    private ImageAdapter mMoviePosterAdapter;
    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_PLOT,
            MovieContract.MovieEntry.COLUMN_BACKDROP

    };


    private static final String MOVIES_DATA_KEY = "movies";
    private static final String STORED_KEY = "choice";
    private static final String MOST_POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";
    private static final String FAVORITE = "favorite";
    private String sortOrder = MOST_POPULAR;
    ArrayList<Movie> mMovies = new ArrayList<Movie>();


    public interface Callback {
        void loadItem(Movie movie);
    }

    public MovieListFragment() {
        setHasOptionsMenu(true);
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(!sortOrder.contentEquals(MOST_POPULAR)){
            outState.putString(STORED_KEY, sortOrder);
        }
        if (mMovies != null) {
            outState.putParcelableArrayList(MOVIES_DATA_KEY, mMovies);
        }
        super.onSaveInstanceState(outState);
    }




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_sort_popularity:
                Log.d(LOG_TAG, "most popular pressed");
                sortOrder = MOST_POPULAR;
                updateMovies(sortOrder);
                return true;

            case R.id.action_sort_user_rating:
                Log.d(LOG_TAG, "most highest Rated pressed");
                sortOrder = TOP_RATED;
                updateMovies(sortOrder);
                return true;
            case R.id.action_favorite:
                Log.d(LOG_TAG, "favorite pressed");
                sortOrder = FAVORITE;
                updateMovies(sortOrder);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mMoviePosterAdapter = new ImageAdapter(
                getActivity(), new ArrayList<Movie>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.main_movie_grid);
        gridView.setAdapter(mMoviePosterAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Movie movie = mMovies.get(position);
                ((Callback) getActivity())
                        .loadItem(movie);

            }

        });

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STORED_KEY)) {
                sortOrder = savedInstanceState.getString(STORED_KEY);
            }

            if (savedInstanceState.containsKey(MOVIES_DATA_KEY)) {
                mMovies = savedInstanceState.getParcelableArrayList(MOVIES_DATA_KEY);
                mMoviePosterAdapter.setData(mMovies);
            } else {
                updateMovies(sortOrder);
            }
        } else {
            updateMovies(sortOrder);
        }

        return rootView;
    }

    private void updateMovies(String choice) {
        //if we want other then favorite movies
        if (!choice.contentEquals(FAVORITE)) {
            if(isNetworkAvailable(getActivity())) {
                new FetchMovies().execute(choice);
            }
        } else {
            if(isNetworkAvailable(getActivity())) {
                new FetchFav(getActivity()).execute();
                ProgressBar mProgressBar = (ProgressBar) getView().findViewById(R.id.progress_bar);
                mProgressBar.setVisibility(View.GONE);
            }
        }
    }



    public class FetchMovies extends AsyncTask<String, Void, List<Movie>> {

        private final String LOG_TAG = "Fetch Movies";

        HttpURLConnection httpURLConnection = null;
        BufferedReader reader = null;
        String jsonResponseString = null;

        @Override
        protected void onPreExecute() {
            Log.d(LOG_TAG,"Fetch movies started");
            super.onPreExecute();
        }

        @Override
        protected List<Movie> doInBackground(String... params) {

            if (params.length == 0){
                Log.d(LOG_TAG, "Died - total Params length is 0");
                return null;
            }

            try {
                String choice = params[0];
                String ApiKey = "?api_key=";

                URL url = new URL(Constants.BASE_URL + choice + ApiKey
                        + BuildConfig.MOVIE_API);

                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                //added new line for pretty printing
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }

                jsonResponseString = buffer.toString();
                //Log.d(LOG_TAG,"Result :" + jsonResponseString);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.d(LOG_TAG, "Error "+ e);
                    }
                }
            }

            try {
                return getMoviesFromJson(jsonResponseString);
            } catch (JSONException e) {
                Log.d(LOG_TAG,"Error " + e);
            }

            //if we failed everywhere this will be returned
            return null;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            //we got movies so let's show them
            //puts movies into adaptor
            if (movies != null) {
                if (mMoviePosterAdapter != null) {
                    mMoviePosterAdapter.setData(movies);
                }
                mMovies = new ArrayList<>();
                mMovies.addAll(movies);
            }
            Log.d(LOG_TAG,"Post execute of Fetch Movies");
        }

        private List<Movie> getMoviesFromJson(String jsonStr) throws JSONException {
            JSONObject movieJson = new JSONObject(jsonStr);
            JSONArray movieArray = movieJson.getJSONArray("results");

            List<Movie> results = new ArrayList<>();

            for(int i = 0; i < movieArray.length(); i++) {
                JSONObject movie = movieArray.getJSONObject(i);
                Movie movieModel = new Movie(movie);
                results.add(movieModel);
            }

            //Log.d(LOG_TAG,results.toString());
            return results;
        }
    }


    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo == null) {
            Toast.makeText(getActivity(), "there's no network connection", Toast.LENGTH_SHORT).show();
        }

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();


    }



    public class FetchFav extends AsyncTask<String, Void, List<Movie>> {

        private Context mContext;



        //constructor
        public FetchFav(Context context) {
            mContext = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Movie> doInBackground(String... params) {
            Cursor cursor = mContext.getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );

            return getFavMoviesFromCursor(cursor);
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            //we got Fav movies so let's show them
            if (movies != null) {
                if (mMoviePosterAdapter != null) {
                    mMoviePosterAdapter.setData(movies);
                }
                mMovies = new ArrayList<>();
                mMovies.addAll(movies);
            }
        }

        private List<Movie> getFavMoviesFromCursor(Cursor cursor) {
            List<Movie> results = new ArrayList<>();
            //if we have data in database for Fav. movies.
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Movie movie = new Movie(cursor);
                    results.add(movie);
                } while (cursor.moveToNext());
                cursor.close();
            }
            return results;
        }
    }


    }






