package com.example.jonat.popularmovienanodegree;

import android.os.AsyncTask;
import android.util.Log;

import com.example.jonat.popularmovienanodegree.Model.DatabaseService;
import com.example.jonat.popularmovienanodegree.Model.Trailer;
import com.example.jonat.popularmovienanodegree.Model.Trailers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jonat on 10/5/2016.
 */
public class FetchTrailersTask extends AsyncTask<Integer, Void, List<Trailer>> {

    @SuppressWarnings("unused")
    public static String LOG_TAG = FetchTrailersTask.class.getSimpleName();
    private final Listener mListener;

    /**
     * Interface definition for a callback to be invoked when trailers are loaded.
     */
    interface Listener {
        void onFetchFinished(List<Trailer> trailers);
    }

    public FetchTrailersTask(Listener listener) {
        mListener = listener;
    }

    @Override
    protected List<Trailer> doInBackground(Integer... params) {
        // If there's no movie id, there's nothing to look up.
        if (params.length == 0) {
            return null;
        }
        int movieId = params[0];

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DatabaseService service = retrofit.create(DatabaseService.class);
        Call<Trailers> call = service.findTrailersById(movieId,
                BuildConfig.MOVIE_API);
        try {
            Response<Trailers> response = call.execute();
            Trailers trailers = response.body();
            return trailers.getTrailers();
        } catch (IOException e) {
            Log.e(LOG_TAG, "A problem occurred talking to the movie db ", e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Trailer> trailers) {
        if (trailers != null) {
            mListener.onFetchFinished(trailers);
        } else {
            mListener.onFetchFinished(new ArrayList<Trailer>());
        }
    }
}
