package com.rudainc.popularmovies.interfaces;

import com.rudainc.popularmovies.models.MovieItem;

import java.util.ArrayList;

public interface OnMoviesUploadCompleted {
    void onMoviesUploadCompleted(ArrayList<MovieItem> moviesData);

    void onMoviesUploadError(String message);
}
