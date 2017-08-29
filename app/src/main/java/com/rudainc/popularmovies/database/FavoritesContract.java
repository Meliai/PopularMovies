package com.rudainc.popularmovies.database;

import android.provider.BaseColumns;

public class FavoritesContract {

    public static final class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorite_list";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "original_title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RATE = "vote_average";
        public static final String COLUMN_DATE = "release_date";

    }


}
