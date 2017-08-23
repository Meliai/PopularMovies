package com.rudainc.popularmovies.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.rudainc.popularmovies.R;
import com.rudainc.popularmovies.adapters.MoviesAdapter;
import com.rudainc.popularmovies.models.MovieItem;
import com.rudainc.popularmovies.network.MoviesDBJsonUtils;
import com.rudainc.popularmovies.network.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

import static com.rudainc.popularmovies.R.id.action_sort_popular;

public class MainActivity extends BaseActivity implements MoviesAdapter.MoviesAdapterOnClickHandler {

    private static final String MOVIE_DATA = "movie_data";
    private static final String MENU_ITEM_CHECKED = "menu_item_checked";
    private RecyclerView rvMovies;
    private MoviesAdapter mMoviesAdapter;

    // Put your API key here! =)
    final private String API_KEY = "YOUR KEY";

    private Menu mMenu;
    private String endpoint;
    private int menu_item_checked = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvMovies = (RecyclerView) findViewById(R.id.rv_movies);

        rvMovies.setLayoutManager(new GridLayoutManager(this, 2));
        mMoviesAdapter = new MoviesAdapter(this, this);
        rvMovies.setAdapter(mMoviesAdapter);
        if (savedInstanceState != null) {
            callAsync(savedInstanceState.getString(MOVIE_DATA));
            menu_item_checked = savedInstanceState.getInt(MENU_ITEM_CHECKED);
        } else
            callAsync("popular");
    }

    @Override
    public void onClick(MovieItem movieItem) {
        Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
        intent.putExtra("data", movieItem);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        mMenu = menu;
        if (menu_item_checked == -1){
            return true;
        }else{
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
            callAsync("popular");
            return true;
        } else if (itemThatWasClickedId == R.id.action_sort_top) {
            item.setChecked(true);
            callAsync("top_rated");
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

        if (isOnline(this))
            new MoviesPosterTask().execute(endpoint);
        else
            showSnackBar(getString(R.string.no_connection));
    }

    private class MoviesPosterTask extends AsyncTask<String, Void, ArrayList<MovieItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected ArrayList<MovieItem> doInBackground(String... params) {
            String url_endpoint = params[0];

            try {
                String jsonMoviesResponse = NetworkUtils
                        .getResponseFromHttpUrl(new URL(Uri.parse("http://api.themoviedb.org/3/movie/" + url_endpoint + "?api_key=" + API_KEY).toString()));

                return MoviesDBJsonUtils.getMoviesFromJson(MainActivity.this, jsonMoviesResponse);

            } catch (Exception e) {
                showSnackBar(e.getMessage());
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<MovieItem> moviesData) {
            if (moviesData != null) {
                mMoviesAdapter.setMoviesData(moviesData);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MOVIE_DATA, endpoint);
        outState.putInt(MENU_ITEM_CHECKED, menu_item_checked);
    }


}
