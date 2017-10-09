package com.rudainc.popularmovies.fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.rudainc.popularmovies.R;
import com.rudainc.popularmovies.activities.MainActivity;
import com.rudainc.popularmovies.activities.MovieDetailsActivity;
import com.rudainc.popularmovies.adapters.MoviesAdapter;
import com.rudainc.popularmovies.database.FavoritesContract;
import com.rudainc.popularmovies.models.MovieItem;
import com.rudainc.popularmovies.utils.PopularMoviesKeys;
import com.rudainc.popularmovies.utils.ToastListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, MoviesAdapter.MoviesAdapterOnClickHandler, PopularMoviesKeys {

    private int mPosition = RecyclerView.NO_POSITION;

    @BindView(R.id.rv)
    RecyclerView rvMovies;

    @BindView(R.id.ll_no_data)
    LinearLayout noData;

    @BindView(R.id.tv_no_data)
    TextView noTvData;

    private MoviesAdapter mMoviesAdapter;

    private LinearLayoutManager ll;
    private InterstitialAd mInterstitialAd;
    private int lastFirstVisiblePosition;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_movies, container, false);
        ButterKnife.bind(this, v);

        getActivity().getSupportLoaderManager().initLoader(ID_LOADER, null, this);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            ll = new GridLayoutManager(getActivity(), 2);
            rvMovies.setLayoutManager(ll);
        } else {
            ll = new GridLayoutManager(getActivity(), 3);
            rvMovies.setLayoutManager(ll);
        }

        if (savedInstanceState != null) {
            final int pos = savedInstanceState.getInt(SCROLL_POSITION);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    rvMovies.scrollToPosition(pos);
                }
            }, 200);
        }

        loadAds();
        return v;
    }


    @Override
    public void onClick(MovieItem movieItem) {
        Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
        intent.putExtra(EXTRA_DATA, movieItem);
        startActivity(intent);
    }

    private void loadAds() {
        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit_id));
        mInterstitialAd.setAdListener(new ToastListener(getActivity()) {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

        });
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (ll != null)
            lastFirstVisiblePosition = ll.findFirstVisibleItemPosition();
        outState.putInt(SCROLL_POSITION, lastFirstVisiblePosition);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {


        switch (loaderId) {

            case ID_LOADER:

                Uri movieQueryUri = FavoritesContract.MovieEntry.CONTENT_URI;
                String sortOrder = FavoritesContract.MovieEntry.COLUMN_MOVIE_ID + " ASC";


                return new CursorLoader(getActivity(),
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
//        mMoviesAdapter.clearList();
        if (!((MainActivity)getActivity()).getAllFavoritesMovies(data).isEmpty()) {
            mMoviesAdapter = new MoviesAdapter(getActivity(), this);
            rvMovies.setAdapter(mMoviesAdapter);
            mMoviesAdapter.setMoviesData(((MainActivity) getActivity()).getAllFavoritesMovies(data));
        }
        else {
            rvMovies.setVisibility(View.GONE);
            noTvData.setText(getResources().getString(R.string.no_favorite));
            noData.setVisibility(View.VISIBLE);
        }
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        rvMovies.smoothScrollToPosition(mPosition);

    }
    @Override
    public void onResume() {
        super.onResume();
        getContext().getContentResolver().notifyChange(FavoritesContract.MovieEntry.CONTENT_URI,null);
    }


    /**
     * Called when a previously created loader is being reset, and thus making its data unavailable.
     * The application should at this point remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviesAdapter.swapCursor(null);
    }
}


