package com.rudainc.popularmovies.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rudainc.popularmovies.R;
import com.rudainc.popularmovies.adapters.MoviesAdapter;
import com.rudainc.popularmovies.custom_views.EndlessRecyclerOnScrollListener;
import com.rudainc.popularmovies.database.FavoritesContract;
import com.rudainc.popularmovies.models.MovieItem;
import com.rudainc.popularmovies.network.BaseResponse;
import com.rudainc.popularmovies.network.PmApiWorker;
import com.rudainc.popularmovies.utils.PopularMoviesKeys;
import com.rudainc.popularmovies.utils.ToastListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements MoviesAdapter.MoviesAdapterOnClickHandler {

    private int mPosition = RecyclerView.NO_POSITION;

    @BindView(R.id.my_ads_banner)
    AdView mAdView;

    @BindView(R.id.rv)
    RecyclerView rvMovies;

    private MoviesAdapter mMoviesAdapter;

    private EndlessRecyclerOnScrollListener mScrollListener;

    private Menu mMenu;
    private String endpoint = POPULAR;
    private ArrayList<MovieItem> movies;

    private int menu_item_checked = -1;
    private int lastFirstVisiblePosition;
    private LinearLayoutManager ll;
    private PmApiWorker mAPiWorker;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            ll = new GridLayoutManager(this, 2);
            rvMovies.setLayoutManager(ll);
        } else {
            ll = new GridLayoutManager(this, 3);
            rvMovies.setLayoutManager(ll);
        }

        initScrollListener();
        rvMovies.addOnScrollListener(mScrollListener);
        mMoviesAdapter = new MoviesAdapter(this, this);
        rvMovies.setAdapter(mMoviesAdapter);

        if (savedInstanceState != null) {
            final int pos = savedInstanceState.getInt(SCROLL_POSITION);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    rvMovies.scrollToPosition(pos);
                }
            }, 200);
            menu_item_checked = savedInstanceState.getInt(MENU_ITEM_CHECKED);
            mMoviesAdapter.updateMoviesList(savedInstanceState.<MovieItem>getParcelableArrayList(MOVIE_DATA));
            endpoint = savedInstanceState.getString(MOVIE_ENDPOINT);

        } else
            getMoviesList(endpoint, "1");

        loadAds();
    }


    private void getMoviesList(String endpoint, String page) {
        PmApiWorker.getInstance().getMovies(endpoint, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<BaseResponse>() {
                    @Override
                    public void call(final BaseResponse baseResponse) {
                        mMoviesAdapter.updateMoviesList(baseResponse.getResults());
                        movies = mMoviesAdapter.getMoviesData();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }


    private void initScrollListener() {
        mScrollListener = new EndlessRecyclerOnScrollListener(ll) {
            @Override
            public void onLoadMore(int current_page, boolean isFullyLoaded) {
                Log.i("PAGE", current_page + "" + isFullyLoaded);
                if (!isFullyLoaded) {
                    getData(current_page);
                }
            }
        };
    }

    private void getData(int page) {
        Log.i("PAGE", page + "");
        mScrollListener.setCurrent_page(page);
        getMoviesList(endpoint, String.valueOf(page));
    }

    private void loadAds() {
        mAdView.setAdListener(new ToastListener(this));
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
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
        if (itemThatWasClickedId == R.id.action_sort_popular) {
            resetMenuItems();
            item.setChecked(true);
            mMoviesAdapter.clearList();
            rvMovies.scrollToPosition(0);
            getMoviesList(POPULAR, "1");
            return true;
        } else if (itemThatWasClickedId == R.id.action_sort_top) {
            resetMenuItems();
            item.setChecked(true);
            mMoviesAdapter.clearList();
            rvMovies.scrollToPosition(0);
            getMoviesList(TOP_RATED, "1");
            return true;
        } else if (itemThatWasClickedId == R.id.action_favorites) {
            startActivity(new Intent(MainActivity.this, FavoritesActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void resetMenuItems() {
        for (int i = 0; i < mMenu.size(); i++)
            mMenu.getItem(i).setChecked(false);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MOVIE_ENDPOINT, endpoint);
        outState.putParcelableArrayList(MOVIE_DATA, movies);
        outState.putInt(MENU_ITEM_CHECKED, menu_item_checked);
        if (ll != null)
            lastFirstVisiblePosition = ll.findFirstVisibleItemPosition();
        outState.putInt(SCROLL_POSITION, lastFirstVisiblePosition);

    }

}
