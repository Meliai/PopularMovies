package com.rudainc.popularmovies.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.rudainc.popularmovies.R;
import com.rudainc.popularmovies.adapters.MoviesAdapter;
import com.rudainc.popularmovies.custom_views.EndlessRecyclerOnScrollListener;
import com.rudainc.popularmovies.database.FavoritesContract;
import com.rudainc.popularmovies.models.MovieItem;
import com.rudainc.popularmovies.network.BaseResponse;
import com.rudainc.popularmovies.network.PmApiWorker;
import com.rudainc.popularmovies.utils.PopularMoviesKeys;
import com.rudainc.popularmovies.utils.ToastListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.rudainc.popularmovies.R.id.action_ads;

public class FavoritesActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>, MoviesAdapter.MoviesAdapterOnClickHandler {


    private int mPosition = RecyclerView.NO_POSITION;

    @BindView(R.id.my_ads_banner)
    AdView mAdView;

    @BindView(R.id.rv)
    RecyclerView rvMovies;

    @BindView(R.id.tv_no_data)
    TextView noData;

    private MoviesAdapter mMoviesAdapter;

    private LinearLayoutManager ll;
    private InterstitialAd mInterstitialAd;
    private int lastFirstVisiblePosition;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle(getString(R.string.title_favorite));
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
            final int pos = savedInstanceState.getInt(SCROLL_POSITION);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    rvMovies.scrollToPosition(pos);
                }
            }, 200);
        }

        loadAdsBanner();
        loadAds();
    }

    private void loadAdsBanner() {
        mAdView.setAdListener(new ToastListener(this));
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


    }

    @Override
    public void onClick(MovieItem movieItem) {
        Intent intent = new Intent(FavoritesActivity.this, MovieDetailsActivity.class);
        intent.putExtra(EXTRA_DATA, movieItem);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reviews, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();

        if (itemThatWasClickedId == action_ads) {
            mInterstitialAd.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadAds() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit_id));
        mInterstitialAd.setAdListener(new ToastListener(this) {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();


            }

        });
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (ll != null)
            lastFirstVisiblePosition = ll.findFirstVisibleItemPosition();
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
        mMoviesAdapter.clearList();
        if (!getAllFavoritesMovies(data).isEmpty())
            mMoviesAdapter.setMoviesData(getAllFavoritesMovies(data));
        else {
            rvMovies.setVisibility(View.GONE);
            noData.setText(getResources().getString(R.string.no_favorite));
            noData.setVisibility(View.VISIBLE);
//            noData.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
        }
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


