package com.rudainc.popularmovies.activities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.rudainc.popularmovies.R;
import com.rudainc.popularmovies.database.FavoritesContract;
import com.rudainc.popularmovies.database.FavoritesDbHelper;
import com.rudainc.popularmovies.models.MovieItem;

import java.util.ArrayList;

public abstract class BaseActivity extends AppCompatActivity {

    private View mCustomSnackBarView;

    private SQLiteDatabase mDb;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FavoritesDbHelper dbHelper = new FavoritesDbHelper(this);
        mDb = dbHelper.getWritableDatabase();
    }

    public ArrayList<MovieItem> getAllFavoritesMovies(Cursor cursor) {
//        Cursor cursor = mDb.query(
//                FavoritesContract.MovieEntry.TABLE_NAME,
//                null,
//                null,
//                null,
//                null,
//                null,
//                FavoritesContract.MovieEntry.COLUMN_MOVIE_ID
//        );
        ArrayList<MovieItem> mArrayList = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

            mArrayList.add(new MovieItem(   cursor.getString(cursor.getColumnIndex(FavoritesContract.MovieEntry.COLUMN_MOVIE_ID)),
                                            cursor.getString(cursor.getColumnIndex(FavoritesContract.MovieEntry.COLUMN_TITLE)),
                                            cursor.getString(cursor.getColumnIndex(FavoritesContract.MovieEntry.COLUMN_POSTER_PATH)),
                                            cursor.getString(cursor.getColumnIndex(FavoritesContract.MovieEntry.COLUMN_OVERVIEW)),
                                            cursor.getString(cursor.getColumnIndex(FavoritesContract.MovieEntry.COLUMN_RATE)),
                                            cursor.getString(cursor.getColumnIndex(FavoritesContract.MovieEntry.COLUMN_DATE))));
        }
        return mArrayList;
    }




    public void addMovie(MovieItem movieItem) {
        ContentValues cv = new ContentValues();
        cv.put(FavoritesContract.MovieEntry.COLUMN_MOVIE_ID, movieItem.getId());
        cv.put(FavoritesContract.MovieEntry.COLUMN_TITLE, movieItem.getOriginal_title());
        cv.put(FavoritesContract.MovieEntry.COLUMN_POSTER_PATH, movieItem.getPoster_path());
        cv.put(FavoritesContract.MovieEntry.COLUMN_RATE, movieItem.getVote_average());
        cv.put(FavoritesContract.MovieEntry.COLUMN_OVERVIEW, movieItem.getOverview());
        cv.put(FavoritesContract.MovieEntry.COLUMN_DATE, movieItem.getRelease_date());
        mDb.insert(FavoritesContract.MovieEntry.TABLE_NAME, null, cv);
    }


    public void removeMovie(String id) {
        mDb.delete(FavoritesContract.MovieEntry.TABLE_NAME, FavoritesContract.MovieEntry.COLUMN_MOVIE_ID + "=" + id, null);
    }

    public boolean checkIsDataAlreadyInDBorNot(String movie_id) {
        String Query = "Select * from " + FavoritesContract.MovieEntry.TABLE_NAME + " where " + FavoritesContract.MovieEntry.COLUMN_MOVIE_ID + " = " + movie_id;
        Cursor cursor = mDb.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    private Snackbar initSnackBar(String message) {
        View snackBarParent = null;
        if (mCustomSnackBarView != null) {
            snackBarParent = mCustomSnackBarView;
        } else if (findViewById(android.R.id.content) != null) {
            snackBarParent = findViewById(android.R.id.content);
        }
        if (snackBarParent != null) {
            Snackbar snackbar = Snackbar.make(snackBarParent, message, Snackbar.LENGTH_SHORT).setActionTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            return snackbar;
        }
        return null;
    }

    public void showSnackBar(String message, boolean isError) {
        Snackbar snackbar = initSnackBar(message);
        snackbar.getView().setBackgroundColor(isError ? ContextCompat.getColor(this, R.color.colorRed) : ContextCompat.getColor(this, R.color.colorGreen));

        if (snackbar != null)
            snackbar.show();

    }

    public boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

}
