package com.rudainc.popularmovies.network.async;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.rudainc.popularmovies.BuildConfig;
import com.rudainc.popularmovies.interfaces.OnMoviesUploadCompleted;
import com.rudainc.popularmovies.models.MovieItem;
import com.rudainc.popularmovies.network.MoviesDBJsonUtils;
import com.rudainc.popularmovies.network.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

public class MovieListAsync extends AsyncTask<Void, Void, ArrayList<MovieItem>> {


    final private String API_KEY = BuildConfig.API_KEY;

    private OnMoviesUploadCompleted listener;
    private Context context;
    private String url_endpoint;

    public MovieListAsync(Context context, String url_endpoint, OnMoviesUploadCompleted listener) {
        this.context = context;
        this.url_endpoint = url_endpoint;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected ArrayList<MovieItem> doInBackground(Void... params) {

        try {
            String jsonMoviesResponse = NetworkUtils
                    .getResponseFromHttpUrl(new URL(Uri.parse("http://api.themoviedb.org/3/movie/" + url_endpoint + "?api_key=" + API_KEY).toString()));

            return MoviesDBJsonUtils.getMoviesFromJson(context, jsonMoviesResponse);

        } catch (Exception e) {
            listener.onMoviesUploadError(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<MovieItem> moviesData) {
        listener.onMoviesUploadCompleted(moviesData);
    }
}