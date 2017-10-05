package com.rudainc.popularmovies.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rudainc.popularmovies.database.FavoritesContract.MovieEntry;

public class FavoritesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favorites_movies.db";
    private static final int DATABASE_VERSION = 1;

    public FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RATE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_DATE + " TEXT NOT NULL " +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}