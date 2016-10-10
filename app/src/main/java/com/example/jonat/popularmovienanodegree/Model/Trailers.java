package com.example.jonat.popularmovienanodegree.Model;

import com.example.jonat.popularmovienanodegree.Model.Trailer;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonat on 10/5/2016.
 */
public class Trailers {
        @SerializedName("results")
        private List<Trailer> trailers = new ArrayList<>();

    //trailer getter
        public List<Trailer> getTrailers() {
            return trailers;
        }
    }

