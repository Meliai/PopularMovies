package com.rudainc.popularmovies.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.rudainc.popularmovies.R;
import com.rudainc.popularmovies.adapters.MoviesAdapter;
import com.rudainc.popularmovies.models.MovieItem;
import com.rudainc.popularmovies.network.MoviesDBJsonUtils;
import com.rudainc.popularmovies.network.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends BaseActivity implements MoviesAdapter.MoviesAdapterOnClickHandler {

    private RecyclerView rvMovies;
    private MoviesAdapter mMoviesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvMovies = (RecyclerView) findViewById(R.id.rv_movies);

        rvMovies.setLayoutManager(new GridLayoutManager(this, 2));
        mMoviesAdapter = new MoviesAdapter(this, this);
        rvMovies.setAdapter(mMoviesAdapter);

        new MoviesPosterTask().execute("popular");
    }

    @Override
    public void onClick(MovieItem movieItem) {
        Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
        intent.putExtra("data", movieItem);
        startActivity(intent);
    }

    public class MoviesPosterTask extends AsyncTask<String, Void, ArrayList<MovieItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected ArrayList<MovieItem> doInBackground(String... params) {
            String url_endpoint = params[0];

            try {
                String jsonMoviesResponse = NetworkUtils
                        .getResponseFromHttpUrl(new URL(Uri.parse("http://api.themoviedb.org/3/movie/"+url_endpoint+"?api_key=1ccf9bd7d6bd3dff076ac0c2c5114610").toString()));

                return MoviesDBJsonUtils.getMoviesFromJson(MainActivity.this, jsonMoviesResponse);

            } catch (Exception e) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.action_sort_popular) {
            new MoviesPosterTask().execute("popular");
            return true;
        }else if (itemThatWasClickedId == R.id.action_sort_top) {
            new MoviesPosterTask().execute("top_rated");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
