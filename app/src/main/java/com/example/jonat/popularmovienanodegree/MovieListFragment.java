package com.example.jonat.popularmovienanodegree;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

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
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sortOrder = prefs.getString(getString(R.string.display_preferences_sort_order_key),
                getString(R.string.display_preferences_sort_default_value));

        if(savedInstanceState != null){
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

    @Override
    public void onStart() {
        super.onStart();

        // get sort order to see if it has recently changed
        String prefSortOrder = prefs.getString(getString(R.string.display_preferences_sort_order_key),
                getString(R.string.display_preferences_sort_default_value));

        if(movies.size() > 0 && prefSortOrder.equals(sortOrder)) {
            updatePosterAdapter();
        }else{
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
        FetchMovieTask fetchMoviesTask = new FetchMovieTask(new FetchMovieTask.AsyncResponse() {
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
        for(Movie movie : movies) {
            mMoviePosterAdapter.add(movie.getPoster());
        }
    }

    public class ImageAdapter extends ArrayAdapter<String> {

        private LayoutInflater mLayoutInflater;
        private Context context;
        private int layoutId;
        private int imageViewID;

        public ImageAdapter(Context context, int layoutId, int imageViewID, ArrayList<String> urls) {
            super(context, 0, urls);
            this.mLayoutInflater = LayoutInflater.from(context);
            this.context = context;
            this.layoutId = layoutId;
            this.imageViewID = imageViewID;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            String url;
            if (v == null) {
                v = mLayoutInflater.inflate(layoutId, parent, false);
            }
            ImageView imageView = (ImageView) v.findViewById(imageViewID);
            url = getItem(position);
            Picasso.with(context).load(url).into(imageView);
            return v;
        }
    }

}