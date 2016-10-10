package com.example.jonat.popularmovienanodegree.Model;

import com.example.jonat.popularmovienanodegree.Model.Reviews;
import com.example.jonat.popularmovienanodegree.Model.Trailers;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by jonat on 10/5/2016.
 */
public interface DatabaseService {
    @GET("3/movie/{id}/videos")
    Call<Trailers> findTrailersById(@Path("id") int movieId, @Query("api_key") String apiKey);

    @GET("3/movie/{id}/reviews")
    Call<Reviews> findReviewsById(@Path("id") int movieId, @Query("api_key") String apiKey);

}
