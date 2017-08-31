package com.rudainc.popularmovies.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.rudainc.popularmovies.R;
import com.rudainc.popularmovies.adapters.MoviesAdapter;
import com.rudainc.popularmovies.database.FavoritesContract;
import com.rudainc.popularmovies.interfaces.OnMoviesUploadCompleted;
import com.rudainc.popularmovies.models.MovieItem;
import com.rudainc.popularmovies.network.async.MovieListAsync;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.rudainc.popularmovies.R.id.action_sort_popular;
import static com.rudainc.popularmovies.R.id.rv;

public class MainActivity extends BaseActivity implements MoviesAdapter.MoviesAdapterOnClickHandler, OnMoviesUploadCompleted, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String MOVIE_DATA = "movie_data";
    private static final String MENU_ITEM_CHECKED = "menu_item_checked";
    private static final String SCROLL_POSITION = "scroll_position";
    private static final String EXTRA_DATA = "data";

    private static final String FAVORITES = "favorites";
    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";

    private static final int ID_LOADER = 44;
    private int mPosition = RecyclerView.NO_POSITION;

    @BindView(rv)
    RecyclerView rvMovies;

    private MoviesAdapter mMoviesAdapter;
    private Menu mMenu;
    private String endpoint;

    private int menu_item_checked = -1;
    private MovieListAsync movieListAsync;
    private int lastFirstVisiblePosition;
    private LinearLayoutManager ll;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getSupportLoaderManager().initLoader(ID_LOADER, null, this);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            ll = new GridLayoutManager(this, 2);
            rvMovies.setLayoutManager(ll);
        } else {
            ll = new GridLayoutManager(this, 3);
            rvMovies.setLayoutManager(ll);
        }

        mMoviesAdapter = new MoviesAdapter(this, this);
        rvMovies.setAdapter(mMoviesAdapter);
        if (savedInstanceState != null) {
            Log.i("ROTATE", "oncreate position" + savedInstanceState.getInt(SCROLL_POSITION));
            final int pos = savedInstanceState.getInt(SCROLL_POSITION);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    rvMovies.scrollToPosition(pos);
                }
            }, 200);
            menu_item_checked = savedInstanceState.getInt(MENU_ITEM_CHECKED);
            if (savedInstanceState.getString(MOVIE_DATA).equals(FAVORITES)) {
                getContentResolver().notifyChange(FavoritesContract.MovieEntry.CONTENT_URI, null);
                endpoint = FAVORITES;
            } else
                callAsync(savedInstanceState.getString(MOVIE_DATA));

        } else
            callAsync(POPULAR);


    }

    @Override
    public void onClick(MovieItem movieItem) {
        Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
        intent.putExtra(EXTRA_DATA, movieItem);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        mMenu = menu;
        if (menu_item_checked == -1) {
            return true;
        } else {
            resetMenuItems();
            MenuItem menuItem = (MenuItem) menu.findItem(menu_item_checked);
            menuItem.setChecked(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        menu_item_checked = itemThatWasClickedId;
        resetMenuItems();
        if (itemThatWasClickedId == action_sort_popular) {
            item.setChecked(true);
            callAsync(POPULAR);
            return true;
        } else if (itemThatWasClickedId == R.id.action_sort_top) {
            item.setChecked(true);
            callAsync(TOP_RATED);
            return true;
        } else if (itemThatWasClickedId == R.id.action_favorites) {
            endpoint = FAVORITES;
            item.setChecked(true);
            getContentResolver().notifyChange(FavoritesContract.MovieEntry.CONTENT_URI, null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void resetMenuItems() {
        for (int i = 0; i < mMenu.size(); i++)
            mMenu.getItem(i).setChecked(false);
    }

    private void callAsync(String url_endpoint) {
        endpoint = url_endpoint;

        if (isOnline(this)) {
            movieListAsync = new MovieListAsync(this, url_endpoint, this);
            movieListAsync.execute();
        } else
            showSnackBar(getString(R.string.smth_went_wrong), true);
    }

    @Override
    public void onMoviesUploadCompleted(ArrayList<MovieItem> moviesData) {
        if (moviesData != null) {
            mMoviesAdapter.setMoviesData(moviesData);
        } else
            showSnackBar(getString(R.string.smth_went_wrong), true);
    }

    @Override
    public void onMoviesUploadError(String message) {
        showSnackBar(message, true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MOVIE_DATA, endpoint);
        outState.putInt(MENU_ITEM_CHECKED, menu_item_checked);
        if (ll != null)
            lastFirstVisiblePosition = ll.findFirstVisibleItemPosition();
        Log.i("ROTATE", "position" + lastFirstVisiblePosition);
        outState.putInt(SCROLL_POSITION, lastFirstVisiblePosition);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {


        switch (loaderId) {

            case ID_LOADER:
                /* URI for all rows of weather data in our weather table */
                Uri movieQueryUri = FavoritesContract.MovieEntry.CONTENT_URI;
                /* Sort order: Ascending by date */
                String sortOrder = FavoritesContract.MovieEntry.COLUMN_MOVIE_ID + " ASC";
                /*
                 * A SELECTION in SQL declares which rows you'd like to return. In our case, we
                 * want all weather data from today onwards that is stored in our weather table.
                 * We created a handy method to do that in our WeatherEntry class.
                 */


                return new CursorLoader(this,
                        movieQueryUri,
                        null,
                        null,
                        null,
                        sortOrder);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mMoviesAdapter.setMoviesData(getAllFavoritesMovies(data));
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        rvMovies.smoothScrollToPosition(mPosition);

    }

    /**
     * Called when a previously created loader is being reset, and thus making its data unavailable.
     * The application should at this point remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        /*
         * Since this Loader's data is now invalid, we need to clear the Adapter that is
         * displaying the data.
         */
        mMoviesAdapter.swapCursor(null);
    }

}
