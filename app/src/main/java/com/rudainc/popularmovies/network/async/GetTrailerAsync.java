package com.rudainc.popularmovies.network.async;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.rudainc.popularmovies.interfaces.OnMovieTrailersCompleted;
import com.rudainc.popularmovies.interfaces.OnMoviesUploadCompleted;
import com.rudainc.popularmovies.models.MovieItem;
import com.rudainc.popularmovies.models.ReviewItem;
import com.rudainc.popularmovies.models.TrailerItem;
import com.rudainc.popularmovies.network.MoviesDBJsonUtils;
import com.rudainc.popularmovies.network.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

public class GetTrailerAsync extends AsyncTask<Void, Void, ArrayList<TrailerItem>> {

    // Put your API key here! =)
    final private String API_KEY = "1ccf9bd7d6bd3dff076ac0c2c5114610";

    private OnMovieTrailersCompleted listener;
    private Context context;
    private int movie_id;

    public GetTrailerAsync(Context context, int movie_id, OnMovieTrailersCompleted listener) {
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