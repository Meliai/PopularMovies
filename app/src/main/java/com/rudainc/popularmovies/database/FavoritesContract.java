package com.rudainc.popularmovies.database;

import android.net.Uri;
import android.provider.BaseColumns;

public class FavoritesContract {

    public static final class MovieEntry implements BaseColumns {

        public static final String CONTENT_AUTHORITY = "com.rudainc.popularmovies";

        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

        /*
         * Possible paths that can be appended to BASE_CONTENT_URI to form valid URI's that Sunshine
         * can handle. For instance,
         *
         *     content://com.example.android.sunshine/weather/
         *     [           BASE_CONTENT_URI         ][ PATH_WEATHER ]
         *
         * is a valid path for looking at weather data.
         *
         *      content://com.example.android.sunshine/givemeroot/
         *
         * will fail, as the ContentProvider hasn't been given any information on what to do with
         * "givemeroot". At least, let's hope not. Don't be that dev, reader. Don't be that dev.
         */
        public static final String PATH_FAVORITES = "favorites";


        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITES)
                .build();

        public static final String TABLE_NAME = "favorite_list";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "original_title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RATE = "vote_average";
        public static final String COLUMN_DATE = "release_date";

    }
}
