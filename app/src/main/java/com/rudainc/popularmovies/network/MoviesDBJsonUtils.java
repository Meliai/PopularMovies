package com.rudainc.popularmovies.network;

import android.content.Context;
import android.util.Log;

import com.rudainc.popularmovies.models.MovieItem;
import com.rudainc.popularmovies.models.MoviesListData;
import com.rudainc.popularmovies.models.ReviewItem;
import com.rudainc.popularmovies.models.TrailerItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class MoviesDBJsonUtils {

    public static ArrayList<ReviewItem> getReviewsFromJson(Context context, String reviewsJsonStr)
            throws JSONException {

        final String OWM_LIST = "results";
        final String OWM_MESSAGE_CODE = "code";

        ArrayList<ReviewItem> parsedMovieReviews = null;

        JSONObject movieJson = new JSONObject(reviewsJsonStr);

        /* Is there an error? */
        if (movieJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = movieJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONArray moviesArray = movieJson.getJSONArray(OWM_LIST);

        parsedMovieReviews = new ArrayList<>();

        for (int i = 0; i < moviesArray.length(); i++) {
            String id;
            String author;
            String content;

            JSONObject movieObject = moviesArray.getJSONObject(i);

            id = movieObject.getString("id");
            author = movieObject.getString("author");
            content = movieObject.getString("content");

            parsedMovieReviews.add(i, new ReviewItem(id, author, content));
        }

        return parsedMovieReviews;
    }

    public static ArrayList<TrailerItem> getTrailersFromJson(Context context, String trailersJsonStr)
            throws JSONException {

        final String OWM_LIST = "results";
        final String OWM_MESSAGE_CODE = "code";

        ArrayList<TrailerItem> parsedMovieTrailers = null;

        JSONObject movieJson = new JSONObject(trailersJsonStr);

        /* Is there an error? */
        if (movieJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = movieJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONArray moviesArray = movieJson.getJSONArray(OWM_LIST);

        parsedMovieTrailers = new ArrayList<>();

        for (int i = 0; i < moviesArray.length(); i++) {
            String id;
            String key;
            String name;

            JSONObject movieObject = moviesArray.getJSONObject(i);

            id = movieObject.getString("id");
            key = movieObject.getString("key");
            name = movieObject.getString("name");

            parsedMovieTrailers.add(i, new TrailerItem(id, key, name));
        }

        return parsedMovieTrailers;
    }


}
