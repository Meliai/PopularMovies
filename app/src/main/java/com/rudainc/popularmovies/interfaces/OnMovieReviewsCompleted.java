package com.rudainc.popularmovies.interfaces;

import com.rudainc.popularmovies.models.ReviewItem;

import java.util.ArrayList;

public interface OnMovieReviewsCompleted {
    void onMovieReviewsCompleted(ArrayList<ReviewItem> moviesData);

    void onMovieReviewsError(String message);
}
