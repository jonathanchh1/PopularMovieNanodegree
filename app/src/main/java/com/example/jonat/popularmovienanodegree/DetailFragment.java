package com.example.jonat.popularmovienanodegree;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jonat.popularmovienanodegree.Adapters.ReviewListAdapter;
import com.example.jonat.popularmovienanodegree.Adapters.TrailerListAdapter;
import com.example.jonat.popularmovienanodegree.Data.MovieContract;
import com.example.jonat.popularmovienanodegree.Model.Movie;
import com.example.jonat.popularmovienanodegree.Model.Review;
import com.example.jonat.popularmovienanodegree.Model.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jonat on 8/10/2016.
 */
public class DetailFragment extends Fragment implements FetchTrailersTask.Listener, FetchReviewsTask.Listener,
TrailerListAdapter.Callbacks, ReviewListAdapter.Callbacks {
    @SuppressWarnings("unused")

    public static final String LOG_TAG = DetailFragment.class.getSimpleName();
    Movie movie;
    static final String DETAIL_MOVIE = "DETAIL_MOVIE";

    @Bind(R.id.movie_title)
    TextView mMovieTitleView;
    @Bind(R.id.movie_overview)
    TextView mMovieOverviewView;
    @Bind(R.id.movie_release_date)
    TextView mMovieReleaseDateView;
    @Bind(R.id.movie_vote_average)
    TextView mMovieRatingView;
    @Bind(R.id.movie_poster)
    ImageView mMoviePosterView;
    private TrailerListAdapter mTrailerListAdapter;
    private ReviewListAdapter mReviewListAdapter;
    public static final String EXTRA_TRAILERS = "EXTRA_TRAILERS";
    public static final String EXTRA_REVIEWS = "EXTRA_REVIEWS";

    private LayoutInflater mLayoutInflater;

    @Bind(R.id.trailer_list)
    RecyclerView mRecyclerViewForTrailers;
    @Bind(R.id.review_list)
    RecyclerView mRecyclerViewForReviews;
    private Toast mToast;
    @Bind(R.id.button_watch_trailer)
    Button mButtonWatchTrailer;
    Trailer trailer;
    private ShareActionProvider mShareActionProvider;
    private View rootView;

    @Bind({R.id.rating_first_star, R.id.rating_second_star, R.id.rating_third_star,
            R.id.rating_fourth_star, R.id.rating_fifth_star})

    List<ImageView> ratingStarViews;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        Activity activity = getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout)
                activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null && activity instanceof DetailActivity) {
            appBarLayout.setTitle(movie.getTitle());
        }

        ImageView movieBackdrop = ((ImageView) activity.findViewById(R.id.movie_backdrop));
        if (movieBackdrop != null) {
            String poster_url = Utility.buildBackdropUrl(movie.getBackdrop());
            Picasso.with(getActivity()).load(poster_url).into(movieBackdrop);

        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Trailer> trailers = mTrailerListAdapter.getTrailers();
        if (trailers != null && !trailers.isEmpty()) {
            outState.putParcelableArrayList(EXTRA_TRAILERS, trailers);
        }

        ArrayList<Review> reviews = mReviewListAdapter.getReviews();
        if (reviews != null && !reviews.isEmpty()) {
            outState.putParcelableArrayList(EXTRA_REVIEWS, reviews);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mLayoutInflater = inflater;

        Bundle arguments = getArguments();
        Intent intent = getActivity().getIntent();

        if (arguments != null || intent != null && intent.hasExtra("movies_details")) {

            rootView = mLayoutInflater.inflate(R.layout.detail_fragment, container, false);
            if (arguments != null) {
                movie = getArguments().getParcelable("movies_details");
            } else {
                movie = intent.getParcelableExtra("movies_details");
            }

            ButterKnife.bind(this, rootView);


            DisplayInfo(rootView);
            updateRatingBar(rootView);


            //For horizontal list of trailers
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                    LinearLayoutManager.HORIZONTAL, false);
            mRecyclerViewForTrailers.setLayoutManager(layoutManager);
            mTrailerListAdapter = new TrailerListAdapter(new ArrayList<Trailer>(), this);
            mRecyclerViewForTrailers.setAdapter(mTrailerListAdapter);
            mRecyclerViewForTrailers.setNestedScrollingEnabled(false);

            LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(),
                    LinearLayoutManager.VERTICAL, false);

            mRecyclerViewForReviews.setLayoutManager(layoutManager1);

            //For vertical list of reviews
            mReviewListAdapter = new ReviewListAdapter(new ArrayList<Review>(), this);
            mRecyclerViewForReviews.setAdapter(mReviewListAdapter);


            // Fetch trailers only if savedInstanceState == null
            if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_TRAILERS)) {
                List<Trailer> trailers = savedInstanceState.getParcelableArrayList(EXTRA_TRAILERS);
                mTrailerListAdapter.add(trailers);
                mButtonWatchTrailer.setEnabled(true);
            } else {
                fetchTrailers(rootView);
            }

            // Fetch reviews only if savedInstanceState == null
            if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_REVIEWS)) {
                List<Review> reviews = savedInstanceState.getParcelableArrayList(EXTRA_REVIEWS);
                mReviewListAdapter.add(reviews);
            } else {
                fetchReviews(rootView);
            }

        }
        return rootView;

    }



    private void updateRatingBar(View view) {
        if (movie.getVoteAverage() != null && !movie.getVoteAverage().isEmpty()) {
            String userRatingStr = getResources().getString(R.string.vote_average,
                    movie.getVoteAverage());
            mMovieRatingView.setText(userRatingStr);

            float userRating = Float.valueOf(movie.getVoteAverage()) / 2;
            int integerPart = (int) userRating;

            // Fill stars
            for (int i = 0; i < integerPart; i++) {
                ratingStarViews.get(i).setImageResource(R.drawable.ic_star_black_24dp);
            }

            // Fill half star
            if (Math.round(userRating) > integerPart) {
                ratingStarViews.get(integerPart).setImageResource(
                        R.drawable.ic_star_half_black_24dp);
            }

        } else {
            mMovieRatingView.setVisibility(View.GONE);
        }
    }



    private void DisplayInfo(View v) {

        if (movie != null) {
            String poster_url = Utility.buildPosterUrl(movie.getPoster());
            //load poster with picasso
            Picasso.with(getActivity()).load(poster_url).into(mMoviePosterView);


            mMovieTitleView.setText(movie.getTitle());
            mMovieOverviewView.setText(movie.getOverview());
            mMovieReleaseDateView.setText(movie.getReleaseDate());

            mButtonWatchTrailer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mTrailerListAdapter.getItemCount() > 0){
                        watch(mTrailerListAdapter.getTrailers().get(0), 0);
                    }
                }
            });

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (movie != null) {
            inflater.inflate(R.menu.main_detail, menu);
            Log.d(LOG_TAG, "detail Menu created");

            final MenuItem action_fav = menu.findItem(R.id.favorite_icon);
            MenuItem action_share = menu.findItem(R.id.action_share);
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(action_share);

            //set  icon on toolbar for favored movies
            new AsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... params) {
                    return Utility.isFavored(getActivity(), movie.getId());
                }

                @Override
                protected void onPostExecute(Integer isFavored) {
                    action_fav.setIcon(isFavored == 1 ?
                            R.drawable.ic_favorite_heart_icon :
                            R.drawable.ic_favorite_hallow_name);
                }
            }.execute();
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.favorite_icon:
                if (movie != null) {
                    // check if movie is favored or not
                    new AsyncTask<Void, Void, Integer>() {

                        @Override
                        protected Integer doInBackground(Void... params) {
                            return Utility.isFavored(getActivity(), movie.getId());
                        }

                        @Override
                        protected void onPostExecute(Integer isFavored) {
                            // if it is in favorites
                            if (isFavored == 1) {
                                // delete from favorites
                                new AsyncTask<Void, Void, Integer>() {
                                    @Override
                                    protected Integer doInBackground(Void... params) {
                                        return getActivity().getContentResolver().delete(
                                                MovieContract.MovieEntry.CONTENT_URI,
                                                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                                                new String[]{Integer.toString(movie.getId())}
                                        );
                                    }

                                    @Override
                                    protected void onPostExecute(Integer rowsDeleted) {
                                        item.setIcon(R.drawable.ic_favorite_hallow_name);
                                        if (mToast != null) {
                                            mToast.cancel();
                                        }
                                        mToast = Toast.makeText(getActivity(), getString(R.string.removed_from_favorites), Toast.LENGTH_SHORT);
                                        mToast.show();
                                    }
                                }.execute();
                            }
                            // if it is not in favorites
                            else {
                                // add to favorites
                                new AsyncTask<Void, Void, Uri>() {
                                    @Override
                                    protected Uri doInBackground(Void... params) {
                                        ContentValues values = new ContentValues();

                                        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
                                        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
                                        values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
                                        values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPoster());
                                        values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
                                        values.put(MovieContract.MovieEntry.COLUMN_PLOT, movie.getOverview());
                                        values.put(MovieContract.MovieEntry.COLUMN_BACKDROP, movie.getBackdrop());

                                        return getActivity().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);
                                    }

                                    @Override
                                    protected void onPostExecute(Uri returnUri) {
                                        item.setIcon(R.drawable.ic_favorite_heart_icon);
                                        if (mToast != null) {
                                            mToast.cancel();
                                        }
                                        mToast = Toast.makeText(getActivity(),
                                                getString(R.string.added_to_favorites), Toast.LENGTH_SHORT);
                                        mToast.show();
                                    }
                                }.execute();
                            }
                        }
                    }.execute();
                }
                return true;

            case R.id.action_share:
                //share movie trailer
                updateShareActionProvider(trailer);


            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void watch(Trailer trailer, int position) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailer.getTrailerUrl())));

    }

    @Override
    public void onFetchFinished(List<Trailer> trailers) {
        mTrailerListAdapter.add(trailers);
        mButtonWatchTrailer.setEnabled(!trailers.isEmpty());

        if(mTrailerListAdapter.getItemCount() > 0){
            Trailer trailer = mTrailerListAdapter.getTrailers().get(0);
            updateShareActionProvider(trailer);
        }
    }

    @Override
    public void read(Review review, int position) {
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(review.getUrl())));
    }

    @Override
    public void onReviewsFetchFinished(List<Review> reviews) {
        mReviewListAdapter.add(reviews);
    }

    private void fetchReviews(View view){
        FetchReviewsTask reviewtask = new FetchReviewsTask(this);
        reviewtask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, movie.getId());
    }

    private void fetchTrailers(View view){
        FetchTrailersTask trailertask = new FetchTrailersTask(this);
        trailertask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, movie.getId());
    }

    private void updateShareActionProvider(Trailer trailer) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, movie.getTitle());
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, trailer.getName() + " : "
                + trailer.getTrailerUrl());
        mShareActionProvider.setShareIntent(sharingIntent);
    }
}






