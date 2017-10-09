package com.rudainc.popularmovies.database;

import android.net.Uri;
import android.provider.BaseColumns;

public class FavoritesContract {

    public static final class MovieEntry implements BaseColumns {

        public static final String CONTENT_AUTHORITY = "com.rudainc.popularmovies";

        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

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
        public static final String IS_FAVORITE = "is_favorite";
        public static final String IS_PINNED = "is_pinned";

    }
}
