package com.example.jonat.popularmovienanodegree;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;


import com.example.jonat.popularmovienanodegree.Model.Movie;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieListFragment.Callback{
    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    @Bind(R.id.toolbar1)
    Toolbar mToolbar;
    private FragmentManager fragmentManager = getFragmentManager();
    private boolean mTwoPane;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mToolbar.setTitle(R.string.title_movie_list);
        setSupportActionBar(mToolbar);


        if (findViewById(R.id.movie_detail_container) != null) {

            mTwoPane = true;

            if (savedInstanceState == null) {
                fragmentManager.beginTransaction()
                        .add(R.id.movie_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

    }


    @Override
    public void loadItem(Movie movie) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable("movies_details", movie);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            fragmentManager.beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra("movies_details", movie);
            startActivity(intent);
        }
    }
}
