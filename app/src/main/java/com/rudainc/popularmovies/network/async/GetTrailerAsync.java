package com.rudainc.popularmovies.network.async;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.rudainc.popularmovies.BuildConfig;
import com.rudainc.popularmovies.interfaces.OnMovieTrailersCompleted;
import com.rudainc.popularmovies.models.TrailerItem;
import com.rudainc.popularmovies.network.MoviesDBJsonUtils;
import com.rudainc.popularmovies.network.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

public class GetTrailerAsync extends AsyncTask<Void, Void, ArrayList<TrailerItem>> {

    final private String API_KEY = BuildConfig.API_KEY;

    private OnMovieTrailersCompleted listener;
    private Context context;
    private String movie_id;

    public GetTrailerAsync(Context context, String movie_id, OnMovieTrailersCompleted listener) {
        this.context = context;
        this.movie_id = movie_id;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected ArrayList<TrailerItem> doInBackground(Void... params) {

        try {
            String jsonMoviesResponse = NetworkUtils
                    .getResponseFromHttpUrl(new URL(Uri.parse("http://api.themoviedb.org/3/movie/" + movie_id + "/videos?api_key=" + API_KEY).toString()));

            return MoviesDBJsonUtils.getTrailersFromJson(context, jsonMoviesResponse);

        } catch (Exception e) {
            listener.onMovieTrailersError(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<TrailerItem> moviesData) {
        listener.onMovieTrailersCompleted(moviesData);

    }
}