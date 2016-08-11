package com.example.jonat.popularmovienanodegree;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.widget.Toast.*;

/**
 * Created by jonat on 8/9/2016.
 */
public class MovieListFragment extends Fragment {

    private final String LOG_TAG = MovieListFragment.class.getSimpleName();
    private final String STORED_MOVIES = "stored_movies";
    private SharedPreferences prefs;
    private ImageAdapter mMoviePosterAdapter;
    String sortOrder;
    List<Movie> movies = new ArrayList<Movie>();

    public MovieListFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        checkNetwork();
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sortOrder = prefs.getString(getString(R.string.display_preferences_sort_order_key),
                getString(R.string.display_preferences_sort_default_value));

        if (savedInstanceState != null) {
            ArrayList<Movie> storedMovies = new ArrayList<Movie>();
            storedMovies = savedInstanceState.<Movie>getParcelableArrayList(STORED_MOVIES);
            movies.clear();
            movies.addAll(storedMovies);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMoviePosterAdapter = new ImageAdapter(
                getActivity(),
                R.layout.list_item_poster,
                R.id.list_item_poster_imageview,
                new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.main_movie_grid);
        gridView.setAdapter(mMoviePosterAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Movie details = movies.get(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("movies_details", details);
                startActivity(intent);
            }

        });

        return rootView;
    }

    public boolean checkNetwork() {
        ConnectivityManager cm = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();
        makeText(getActivity(), "no connection found", LENGTH_LONG).show();
        return network !=null && network.isConnectedOrConnecting();
    }

    private String moviesUri() {
        Uri builtUri;

        if (sortOrder.equals(getString(R.string.pref_popular_value))) {
            builtUri = Uri.parse(Constants.POPULAR_URL);
        } else if (sortOrder.equals(getString(R.string.pref_top_rated_value))) {
            builtUri = Uri.parse(Constants.RATED_URL);
        } else {
            builtUri = Uri.parse(Constants.POPULAR_URL);
        }

        return String.valueOf(builtUri);
    }

    @Override
    public void onStart() {
        super.onStart();

        // get sort order to see if it has recently changed
        String prefSortOrder = prefs.getString(getString(R.string.display_preferences_sort_order_key),
                getString(R.string.display_preferences_sort_default_value));

        if (movies.size() > 0 && prefSortOrder.equals(sortOrder)) {
            updatePosterAdapter();
        } else {
            sortOrder = prefSortOrder;
            getMovies();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Movie> storedMovies = new ArrayList<Movie>();
        storedMovies.addAll(movies);
        outState.putParcelableArrayList(STORED_MOVIES, storedMovies);
    }

    private void getMovies() {
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask(new AsyncResponse() {
            @Override
            public void onTaskCompleted(List<Movie> results) {
                movies.clear();
                movies.addAll(results);
                updatePosterAdapter();
            }
        });
        fetchMoviesTask.execute(sortOrder);
    }

    // updates the ArrayAdapter of poster images
    private void updatePosterAdapter() {
        mMoviePosterAdapter.clear();
        for (Movie movie : movies) {
            mMoviePosterAdapter.add(movie.getPoster());
        }
    }

    class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
        public AsyncResponse delegate;
        private final String MOVIE_BASE_PATH = "http://image.tmdb.org/t/p/";
        private final String MOVIE_BASE_SIZE = "w185";

        public FetchMoviesTask(AsyncResponse delegate) {
            this.delegate = delegate;

        }

        @Override
        protected List<Movie> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }


            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieJsonStr = moviesUri();

            try {
                final String KEY = "api_key";
                final String SORT_BY = "sort_by";
                String SortBy = params[0];



                Uri builtUri = Uri.parse(moviesUri()).buildUpon()
                        .appendQueryParameter(KEY, BuildConfig.MOVIE_API)
                        .appendQueryParameter(SORT_BY, SortBy)
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.i(LOG_TAG, "mean info");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                 if(movieJsonStr != null)
                    movieJsonStr = buffer.toString();
                   Log.e(LOG_TAG, movieJsonStr);



            } catch (ProtocolException e) {
                e.printStackTrace();
                Log.i(LOG_TAG, "recording");
                return null;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "error streaming");
                        e.printStackTrace();
                    }
                }

            }
            try {

                return getMovieData(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "streaming messages");
                e.printStackTrace();
            }


            return null;
        }

        private List<Movie> getMovieData(String movieJsonStr) throws JSONException {
            //item to extract
            final String MOVIE_DATA = "results";
            final String ORIGINAL_TITLE = "original_title";
            final String POSTER_PATH = "poster_path";
            final String OVERVIEW = "overview";
            final String VOTE_AVERAGE = "vote_average";
            final String RELEASE_DATE = "release_date";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(MOVIE_DATA);
            int movieLength = movieArray.length();
            List<Movie> movies = new ArrayList<Movie>();

            for (int i = 0; i < movieLength; ++i) {

                JSONObject movie = movieArray.getJSONObject(i);
                String title = movie.getString(ORIGINAL_TITLE);
                String poster = MOVIE_BASE_PATH + MOVIE_BASE_SIZE + movie.getString(POSTER_PATH);
                String overview = movie.getString(OVERVIEW);
                String voteAverage = movie.getString(VOTE_AVERAGE);
                String release = getYear(movie.getString(RELEASE_DATE));

                movies.add(new Movie(title, poster, overview, voteAverage, release));
            }

            return movies;
        }

        private String getYear(String date) {
            final SimpleDateFormat mdate = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Calendar mcal = Calendar.getInstance();
            try {
                mcal.setTime(mdate.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return Integer.toString(mcal.get(Calendar.YEAR));
        }

        @Override
        protected void onPostExecute(List<Movie> results) {
            if (results != null) {
                delegate.onTaskCompleted(results);
                Log.i(LOG_TAG, "entry:" + results);
            }

        }
    }

        public interface AsyncResponse {
            void onTaskCompleted(List<Movie> results);
        }

    }


