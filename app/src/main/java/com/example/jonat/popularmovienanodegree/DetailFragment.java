package com.example.jonat.popularmovienanodegree;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by jonat on 8/10/2016.
 */
public class DetailFragment extends Fragment{
    private final String LOG_TAG = DetailFragment.class.getSimpleName();
    Movie movie;

    public DetailFragment(){
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.detail_fragment, container, false);

        Intent intent = getActivity().getIntent();
        if(intent != null && intent.hasExtra("movies_details")){
            movie = (Movie)intent.getParcelableExtra("movies_details");
            DisplayInfo(rootView);

        }

        return rootView;
    }

    private void DisplayInfo(View v) {

        TextView title = (TextView) v.findViewById(R.id.title_textview);
        ImageView poster = (ImageView) v.findViewById(R.id.image_poster);
        TextView releaseDate = (TextView) v.findViewById(R.id.release_date);
        TextView ratings = (TextView) v.findViewById(R.id.ratings_view);
        TextView overview = (TextView) v.findViewById(R.id.synopsis);

        title.setText(movie.getTitle());
        Picasso.with(getActivity()).load(movie.getPoster()).into(poster);
        releaseDate.setText(movie.getReleaseDate());
        ratings.setText(movie.getVoteAverage() + "/10");
        overview.setText(movie.getOverview());

    }

}
