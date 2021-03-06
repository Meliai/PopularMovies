package com.rudainc.popularmovies.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ShareEvent;
import com.rudainc.popularmovies.R;
import com.rudainc.popularmovies.adapters.TrailersAdapter;
import com.rudainc.popularmovies.interfaces.OnMovieTrailersCompleted;
import com.rudainc.popularmovies.models.MovieItem;
import com.rudainc.popularmovies.models.TrailerItem;
import com.rudainc.popularmovies.network.async.GetTrailerAsync;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MovieDetailsActivity extends BaseActivity implements TrailersAdapter.TrailersAdapterOnClickHandler, OnMovieTrailersCompleted {

    @BindView(R.id.tv_origin_title)
    TextView mTitle;

    @BindView(R.id.poster)
    ImageView mPoster;

    @BindView(R.id.rate)
    TextView mRate;

    @BindView(R.id.overview)
    TextView mOverview;

    @BindView(R.id.release_date)
    TextView mReleaseDate;

    @BindView(R.id.rv_trailers)
    RecyclerView mRecyclerViewTrailers;

    @BindView(R.id.tv_no_trailers)
    TextView mNoTrailers;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;

    @BindView(R.id.toolbar_favorite)
    ImageView mToolbarFavorite;

    @BindView(R.id.toolbar_pin)
    ImageView mToolbarPin;

    TrailersAdapter mTrailerAdapter;
    private GetTrailerAsync getTrailerAsync;
    private MovieItem movieItem;

    private static final String EXTRA_DATA = "data";
    private MovieItem myMovie;

    @OnClick(R.id.show_reviews)
    void showReviews() {
        Intent intent = new Intent(MovieDetailsActivity.this, ReviewsActivity.class);
        intent.putExtra(EXTRA_DATA, movieItem);
        startActivity(intent);
    }


    @OnClick(R.id.toolbar_favorite)
    void favorite() {
        if (!checkIsDataAlreadyInDBorNot(String.valueOf(movieItem.getId()))) {
            addMovie(movieItem, TRUE, FALSE);
            mToolbarFavorite.setImageResource(R.drawable.ic_favorite_active);
            showSnackBar(getResources().getString(R.string.favorite_added), false);
        } else {
            myMovie = getMovie(String.valueOf(movieItem.getId()));
            if (myMovie.is_favorite().equals(FALSE)) {
                updateMovie(movieItem, TRUE, myMovie.is_pinned());
                mToolbarFavorite.setImageResource(R.drawable.ic_favorite_active);
                showSnackBar(getResources().getString(R.string.favorite_added), false);
            } else {
                updateMovie(movieItem, FALSE, myMovie.is_pinned());
                mToolbarFavorite.setImageResource(R.drawable.ic_favorite_inactive);
                showSnackBar(getResources().getString(R.string.favorite_removed), false);
            }
        }
    }

    @OnClick(R.id.toolbar_pin)
    void pin() {
        if (!checkIsDataAlreadyInDBorNot(String.valueOf(movieItem.getId()))) {
            addMovie(movieItem, FALSE, TRUE);
            mToolbarPin.setImageResource(R.drawable.ic_pin_yellow);
            showSnackBar(getResources().getString(R.string.pin_added), false);
        } else {
            myMovie = getMovie(String.valueOf(movieItem.getId()));
            if (myMovie.is_pinned().equals(FALSE)) {
                updateMovie(movieItem, myMovie.is_favorite(), TRUE);
                mToolbarPin.setImageResource(R.drawable.ic_pin_yellow);
                showSnackBar(getResources().getString(R.string.pin_added), false);
            } else {
                updateMovie(movieItem, myMovie.is_favorite(), FALSE);
                mToolbarPin.setImageResource(R.drawable.ic_pin_black);
                showSnackBar(getResources().getString(R.string.pin_removed), false);
            }
        }
    }

    @OnClick(R.id.fab_share)
    void share() {
        Answers.getInstance().logShare(new ShareEvent());
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String image = "http://image.tmdb.org/t/p/w500/" + movieItem.getPoster_path();
        String app_link = "https://play.google.com/store/apps/details?id=com.rudainc.popularmovies&hl=en";
        intent.putExtra(Intent.EXTRA_TEXT, "What do you think about \"" + movieItem.getOriginal_title() + "\"?" + "\nFound this movie in the app\n" + app_link);
        startActivity(Intent.createChooser(intent, "Share with"));
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        mToolbarFavorite.setVisibility(View.VISIBLE);
        mToolbarPin.setVisibility(View.VISIBLE);
        mToolbarTitle.setText(getString(R.string.title_details));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        movieItem = (MovieItem) getIntent().getParcelableExtra(EXTRA_DATA);

        fillData(movieItem);

        mRecyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));
        mTrailerAdapter = new TrailersAdapter(this, this);
        mRecyclerViewTrailers.setAdapter(mTrailerAdapter);

        if (isOnline(this)) {
            getTrailerAsync = new GetTrailerAsync(this, movieItem.getId(), this);
            getTrailerAsync.execute();
        } else
            showSnackBar(getString(R.string.smth_went_wrong), true);
    }

    private void fillData(MovieItem movieItem) {
        if (checkIsDataAlreadyInDBorNot(String.valueOf(movieItem.getId()))) {
            myMovie = getMovie(String.valueOf(movieItem.getId()));
            mToolbarFavorite.setImageResource(Boolean.parseBoolean(myMovie.is_favorite()) ? R.drawable.ic_favorite_active : R.drawable.ic_favorite_inactive);
            mToolbarPin.setImageResource(Boolean.parseBoolean(myMovie.is_pinned()) ? R.drawable.ic_pin_yellow : R.drawable.ic_pin_black);
        }
        mTitle.setText(movieItem.getOriginal_title());
        mRate.setText(String.format(getString(R.string.rate), String.valueOf(movieItem.getVote_average())));
        mOverview.setText(movieItem.getOverview());
        mReleaseDate.setText(movieItem.getRelease_date().substring(0, 4));
        Picasso.with(this).load("http://image.tmdb.org/t/p/w342/" + movieItem.getPoster_path()).placeholder(R.mipmap.ic_place_holder).error(R.mipmap.ic_place_holder).into(mPoster);
    }


    @Override
    public void onMovieTrailersCompleted(ArrayList<TrailerItem> trailerData) {
        if (trailerData != null) {
            if (trailerData.isEmpty())
                setNoTrailerUI(getResources().getString(R.string.no_trailers));
            mTrailerAdapter.setTrailerData(trailerData);
        } else {
            showSnackBar(getString(R.string.smth_went_wrong), true);
            setNoTrailerUI(getResources().getString(R.string.cant_upload_data));
        }
    }

    private void setNoTrailerUI(String text) {
        mNoTrailers.setVisibility(View.VISIBLE);
        mNoTrailers.setText(text);
        mRecyclerViewTrailers.setVisibility(View.GONE);
    }

    @Override
    public void onMovieTrailersError(String message) {
        showSnackBar(message, true);
        setNoTrailerUI(getResources().getString(R.string.cant_upload_data));
    }

    @Override
    public void onClick(TrailerItem trailerItem) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerItem.getKey()));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + trailerItem.getKey()));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }
}
