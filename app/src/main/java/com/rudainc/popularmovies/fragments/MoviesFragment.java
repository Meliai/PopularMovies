package com.rudainc.popularmovies.fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rudainc.popularmovies.R;
import com.rudainc.popularmovies.activities.MainActivity;
import com.rudainc.popularmovies.activities.MovieDetailsActivity;
import com.rudainc.popularmovies.adapters.MoviesAdapter;
import com.rudainc.popularmovies.custom_views.EndlessRecyclerOnScrollListener;
import com.rudainc.popularmovies.models.MovieItem;
import com.rudainc.popularmovies.network.BaseResponse;
import com.rudainc.popularmovies.network.PmApiWorker;
import com.rudainc.popularmovies.utils.PopularMoviesKeys;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MoviesFragment extends Fragment implements MoviesAdapter.MoviesAdapterOnClickHandler, PopularMoviesKeys {


    @BindView(R.id.rv)
    RecyclerView rvMovies;

    @BindView(R.id.ll_no_data)
    LinearLayout noData;

    @BindView(R.id.tv_no_data)
    TextView noTvData;


    private int mPosition = RecyclerView.NO_POSITION;
    private MoviesAdapter mMoviesAdapter;

    private EndlessRecyclerOnScrollListener mScrollListener;

    private Menu mMenu;
    private String endpoint = POPULAR;
    private ArrayList<MovieItem> movies;

    private int menu_item_checked = -1;
    private int lastFirstVisiblePosition;
    private LinearLayoutManager ll;
    private PmApiWorker mAPiWorker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_movies, container, false);
        ButterKnife.bind(this, v);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            ll = new GridLayoutManager(getActivity(), 2);
            rvMovies.setLayoutManager(ll);
        } else {
            ll = new GridLayoutManager(getActivity(), 3);
            rvMovies.setLayoutManager(ll);
        }

        endpoint = getArguments().getString(FILTER);

        initScrollListener();
        rvMovies.addOnScrollListener(mScrollListener);
        mMoviesAdapter = new MoviesAdapter(getActivity(), this);
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
            movies = savedInstanceState.getParcelableArrayList(MOVIE_DATA);
            mMoviesAdapter.setMoviesData(movies);
            endpoint = savedInstanceState.getString(MOVIE_ENDPOINT);

        } else
            getMoviesList(endpoint, "1");
        return v;
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
                        rvMovies.setVisibility(View.GONE);
                        noTvData.setText(getResources().getString(R.string.smth_went_wrong));
                        noData.setVisibility(View.VISIBLE);
                    }
                });
    }


    private void initScrollListener() {
        mScrollListener = new EndlessRecyclerOnScrollListener(ll) {
            @Override
            public void onLoadMore(int current_page, boolean isFullyLoaded) {
                if (!isFullyLoaded) {
                    getData(current_page);
                }
            }
        };
    }

    private void getData(int page) {
        mScrollListener.setCurrent_page(page);
        getMoviesList(endpoint, String.valueOf(page));
    }

    @Override
    public void onClick(MovieItem movieItem) {
        Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
        intent.putExtra(EXTRA_DATA, movieItem);
        startActivity(intent);
    }



//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_movies, menu);
//        Log.i("MovieFragment", "oncreatemenu");
//        mMenu = menu;
//        if (menu_item_checked != -1) {
//            resetMenuItems();
//            MenuItem menuItem = (MenuItem) menu.findItem(menu_item_checked);
//            menuItem.setChecked(true);
//        }
//        super.onCreateOptionsMenu(menu, inflater);
//    }
//
//    public void resetMenuItems() {
//        for (int i = 0; i < mMenu.size(); i++)
//            mMenu.getItem(i).setChecked(false);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int itemThatWasClickedId = item.getItemId();
//        menu_item_checked = itemThatWasClickedId;
//        if (itemThatWasClickedId == R.id.action_sort_popular) {
//            resetMenuItems();
//            item.setChecked(true);
//            mMoviesAdapter.clearList();
//            rvMovies.scrollToPosition(0);
//            getMoviesList(POPULAR, "1");
//            return true;
//        } else if (itemThatWasClickedId == R.id.action_sort_top) {
//            resetMenuItems();
//            item.setChecked(true);
//            mMoviesAdapter.clearList();
//            rvMovies.scrollToPosition(0);
//            getMoviesList(TOP_RATED, "1");
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MOVIE_ENDPOINT, endpoint);
        outState.putParcelableArrayList(MOVIE_DATA, movies);
        outState.putInt(MENU_ITEM_CHECKED, menu_item_checked);
        if (ll != null)
            lastFirstVisiblePosition = ll.findFirstVisibleItemPosition();
        outState.putInt(SCROLL_POSITION, lastFirstVisiblePosition);

    }

    public static Fragment newInstance(String filter) {
        MoviesFragment myFragment = new MoviesFragment();
        Bundle args = new Bundle();
        args.putString(FILTER, filter);
        myFragment.setArguments(args);
        return myFragment;
    }

    public static Fragment getInstance(MoviesFragment moviesFragment, String tagMoviesPopular) {
        Bundle args = new Bundle();
        args.putString(FILTER, tagMoviesPopular);
        moviesFragment.setArguments(args);
        return moviesFragment;
    }
}
