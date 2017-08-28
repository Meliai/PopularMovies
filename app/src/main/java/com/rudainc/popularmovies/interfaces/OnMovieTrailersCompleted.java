package com.rudainc.popularmovies.interfaces;

import com.rudainc.popularmovies.models.TrailerItem;

import java.util.ArrayList;

public interface OnMovieTrailersCompleted {
    void onMovieTrailersCompleted(ArrayList<TrailerItem> moviesData);

    void onMovieTrailersError(String message);
}
