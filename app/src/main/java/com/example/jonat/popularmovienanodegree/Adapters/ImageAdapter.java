package com.example.jonat.popularmovienanodegree.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jonat.popularmovienanodegree.Model.Movie;
import com.example.jonat.popularmovienanodegree.R;
import com.example.jonat.popularmovienanodegree.Utility;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by jonat on 8/10/2016.
 */public class ImageAdapter extends BaseAdapter {

    private final Context mContext;
    private final LayoutInflater mInflater;

    private final Movie mMovie = new Movie();

    private List<Movie> mMovieObjects;

    public interface open{
        void onItemSelected(Movie movie);
    }

    //constructor
    public ImageAdapter(Context context, List<Movie> MovieObjects){

        mContext = context;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMovieObjects = MovieObjects;
    }



    //Getter method
    public Context getmContext(){
        return mContext;
    }

    public void add(Movie MovieObject){
        synchronized (mMovie){
            mMovieObjects.add(MovieObject);
        }
        notifyDataSetChanged();
    }

    public void remove(){
        synchronized (mMovie){
            mMovieObjects.clear();
        }
        notifyDataSetChanged();
    }

    public void setData(List<Movie> data){
        remove();
        for (Movie movie : data){
            add(movie);
        }
    }

    @Override
    public int getCount() {
        return mMovieObjects.size();
    }

    @Override
    public Movie getItem(int position) {
        return mMovieObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        MovieViewHolder viewHolder;

        if (view == null){
            view = mInflater.inflate(R.layout.list_item_poster, parent, false);
            viewHolder = new MovieViewHolder(view);
            view.setTag(viewHolder);

        }


        final Movie movie = getItem(position);


        String poster_url = Utility.buildPosterUrl(movie.getPoster());

        //Log.d("GridAdaptor",poster_url);
        viewHolder = (MovieViewHolder) view.getTag();

        //load poster image into image view
        Picasso.with(getmContext()).load(poster_url).into(viewHolder.imageView);
        //Log.d("GridAdaptor","Loading Poster Image IN adaptor");
        //set movie title text into view
        viewHolder.titleView.setText(movie.getTitle());
        //Log.d("GridAdaptor","Setting text in Grid Adaptor");

        return view;
    }

    public static class MovieViewHolder {
        public final ImageView imageView;
        public final TextView titleView;

        public MovieViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.grid_item_image);
            titleView = (TextView) view.findViewById(R.id.grid_item_title);
        }
    }
}
