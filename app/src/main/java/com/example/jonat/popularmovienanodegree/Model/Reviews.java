package com.example.jonat.popularmovienanodegree.Model;

import com.example.jonat.popularmovienanodegree.Model.Review;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonat on 10/5/2016.
 */
public class Reviews {
    @SerializedName("results")
    private List<Review> reviews = new ArrayList<>();

    public List<Review> getReviews(){
        return reviews;
    }
}
