package com.rudainc.popularmovies.network.async;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.rudainc.popularmovies.BuildConfig;
import com.rudainc.popularmovies.interfaces.OnMovieReviewsCompleted;
import com.rudainc.popularmovies.models.ReviewItem;
import com.rudainc.popularmovies.network.MoviesDBJsonUtils;
import com.rudainc.popularmovies.network.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

public class GetReviewsAsync extends AsyncTask<Void, Void, ArrayList<ReviewItem>> {

    final private String API_KEY = BuildConfig.API_KEY;

    private OnMovieReviewsCompleted listener;
    private Context context;
    private String movie_id;

    public GetReviewsAsync(Context context, String movie_id, OnMovieReviewsCompleted listener) {
        this.context = context;
        this.movie_id = movie_id;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected ArrayList<ReviewItem> doInBackground(Void... params) {

        try {
            String jsonMoviesResponse = NetworkUtils
                    .getResponseFromHttpUrl(new URL(Uri.parse("http://api.themoviedb.org/3/movie/" + movie_id + "/reviews?api_key=" + API_KEY).toString()));

            return MoviesDBJsonUtils.getReviewsFromJson(context, jsonMoviesResponse);

        } catch (Exception e) {
            listener.onMovieReviewsError(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<ReviewItem> reviewsData) {
        listener.onMovieReviewsCompleted(reviewsData);
    }
}