package com.rudainc.popularmovies.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.rudainc.popularmovies.R;
import com.rudainc.popularmovies.adapters.MoviesAdapter;
import com.rudainc.popularmovies.interfaces.OnMoviesUploadCompleted;
import com.rudainc.popularmovies.models.MovieItem;
import com.rudainc.popularmovies.network.async.MovieListAsync;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.rudainc.popularmovies.R.id.action_sort_popular;

public class MainActivity extends BaseActivity implements MoviesAdapter.MoviesAdapterOnClickHandler, OnMoviesUploadCompleted {

    private static final String MOVIE_DATA = "movie_data";
    private static final String MENU_ITEM_CHECKED = "menu_item_checked";
    private static final String EXTRA_DATA = "data";

    private static final String FAVORITES = "favorites";
    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";

    @BindView(R.id.rv)
    RecyclerView rvMovies;

    private MoviesAdapter mMoviesAdapter;
    private Menu mMenu;
    private String endpoint;

    private int menu_item_checked = -1;
    private MovieListAsync movieListAsync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            rvMovies.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            rvMovies.setLayoutManager(new GridLayoutManager(this, 3));
        }

        mMoviesAdapter = new MoviesAdapter(this, this);
        rvMovies.setAdapter(mMoviesAdapter);
        if (savedInstanceState != null) {
            menu_item_checked = savedInstanceState.getInt(MENU_ITEM_CHECKED);
            if (savedInstanceState.getString(MOVIE_DATA).equals(FAVORITES)) {
                mMoviesAdapter.setMoviesData(getAllFavoritesMovies());
                endpoint = FAVORITES;
            }else
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
            mMoviesAdapter.setMoviesData(getAllFavoritesMovies());
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
    }
}
