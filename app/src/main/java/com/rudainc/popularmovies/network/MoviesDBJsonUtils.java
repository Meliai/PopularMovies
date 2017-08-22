package com.rudainc.popularmovies.network;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.rudainc.popularmovies.models.MovieItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class MoviesDBJsonUtils {

    public static ArrayList<MovieItem> getMoviesFromJson(Context context, String moviesJsonStr)
            throws JSONException {


        final String OWM_LIST = "results";
        final String OWM_MESSAGE_CODE = "code";


        ArrayList<MovieItem> parsedMovieData = null;

        JSONObject movieJson = new JSONObject(moviesJsonStr);

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

        parsedMovieData = new ArrayList<>();

        Log.i("List", moviesArray.length() +"");

        for (int i = 0; i < moviesArray.length(); i++) {

            String poster_path;
            String original_title;
           String overview;
            double vote_average;
            String release_date;

            JSONObject movieObject = moviesArray.getJSONObject(i);

            original_title = movieObject.getString("original_title");
            poster_path = movieObject.getString("poster_path");
            overview = movieObject.getString("overview");
            release_date = movieObject.getString("release_date");
            vote_average = movieObject.getDouble("vote_average");


            parsedMovieData.add(i, new MovieItem(original_title,poster_path,overview,vote_average,release_date));


        }

        return parsedMovieData;
    }


}
