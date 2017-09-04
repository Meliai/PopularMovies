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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rudainc.popularmovies.R;
import com.rudainc.popularmovies.adapters.MoviesAdapter;
import com.rudainc.popularmovies.custom_views.EndlessRecyclerOnScrollListener;
import com.rudainc.popularmovies.database.FavoritesContract;
import com.rudainc.popularmovies.models.MovieItem;
import com.rudainc.popularmovies.network.BaseResponse;
import com.rudainc.popularmovies.network.PmApiWorker;
import com.rudainc.popularmovies.utils.ToastListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class FavoritesActivity extends BaseActivity implements  LoaderManager.LoaderCallbacks<Cursor>{

    private static final String SCROLL_POSITION = "scroll_position";
    private static final String EXTRA_DATA = "data";

    private static final String FAVORITES = "favorites";
    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";

    private static final int ID_LOADER = 44;
    private int mPosition = RecyclerView.NO_POSITION;

    @BindView(R.id.my_ads_banner)
    AdView mAdView;

    @BindView(R.id.rv)
    RecyclerView rvMovies;

    private MoviesAdapter mMoviesAdapter;

    private EndlessRecyclerOnScrollListener mScrollListener;

    private Menu mMenu;
    private String endpoint = POPULAR;

    private int menu_item_checked = -1;
    private int lastFirstVisiblePosition;
    private LinearLayoutManager ll;
    private PmApiWorker mAPiWorker;

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

        initScrollListener();
        rvMovies.addOnScrollListener(mScrollListener);
//        mMoviesAdapter = new MoviesAdapter(this, this);
        rvMovies.setAdapter(mMoviesAdapter);

        if (savedInstanceState != null) {
            final int pos = savedInstanceState.getInt(SCROLL_POSITION);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    rvMovies.scrollToPosition(pos);
                }
            }, 200);
//            menu_item_checked = savedInstanceState.getInt(MENU_ITEM_CHECKED);
//            if (savedInstanceState.getString(MOVIE_DATA).equals(FAVORITES)) {
//                getContentResolver().notifyChange(FavoritesContract.MovieEntry.CONTENT_URI, null);
//                endpoint = FAVORITES;
//            } else
//                getMoviesList(savedInstanceState.getString(MOVIE_DATA), "1");

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
//        if (page != 1)
//            new Handler().post(() -> mMoviesAdapter.addPaginationFooter(null, ""));
        Log.i("PAGE", page + "");
        mScrollListener.setCurrent_page(page);
        getMoviesList(endpoint, String.valueOf(page));
    }

    private void loadAds() {
        mAdView.setAdListener(new ToastListener(this));
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


    }

//    @Override
//    public void onClick(MovieItem movieItem) {
//        Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
//        intent.putExtra(EXTRA_DATA, movieItem);
//        startActivity(intent);
//    }

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
        if (itemThatWasClickedId == R.id.action_sort_popular) {
            item.setChecked(true);
            mMoviesAdapter.clearList();
            rvMovies.scrollToPosition(0);
            getMoviesList(POPULAR, "1");
            return true;
        } else if (itemThatWasClickedId == R.id.action_sort_top) {
            item.setChecked(true);
            mMoviesAdapter.clearList();
            rvMovies.scrollToPosition(0);
            getMoviesList(TOP_RATED, "1");
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


//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putString(MOVIE_DATA, endpoint);
//        outState.putInt(MENU_ITEM_CHECKED, menu_item_checked);
//        if (ll != null)
//            lastFirstVisiblePosition = ll.findFirstVisibleItemPosition();
//        outState.putInt(SCROLL_POSITION, lastFirstVisiblePosition);
//
//    }

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
        mMoviesAdapter.clearList();
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


