package com.rudainc.popularmovies.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rudainc.popularmovies.R;
import com.rudainc.popularmovies.adapters.TrailersAdapter;
import com.rudainc.popularmovies.interfaces.OnMovieTrailersCompleted;
import com.rudainc.popularmovies.models.MovieItem;
import com.rudainc.popularmovies.models.ReviewItem;
import com.rudainc.popularmovies.models.TrailerItem;
import com.rudainc.popularmovies.network.async.GetTrailerAsync;
import com.rudainc.popularmovies.network.async.MovieListAsync;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.R.id.message;

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

    private MovieItem movieItem;

    @OnClick(R.id.show_reviews)
    void showReviews() {
        Intent intent = new Intent(MovieDetailsActivity.this, ReviewsActivity.class);
        intent.putExtra("data", movieItem);
        startActivity(intent);
    }

    TrailersAdapter mTrailerAdapter;
    private GetTrailerAsync getTrailerAsync;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle(getString(R.string.title_details));

       movieItem = (MovieItem) getIntent().getSerializableExtra("data");

        fillData(movieItem);

        mRecyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));
        mTrailerAdapter = new TrailersAdapter(this, this);
        mRecyclerViewTrailers.setAdapter(mTrailerAdapter);

        if (isOnline(this)) {
            getTrailerAsync = new GetTrailerAsync(this, movieItem.getId(), this);
            getTrailerAsync.execute();
        } else
            showSnackBar(getString(R.string.smth_went_wrong));
    }

    private void fillData(MovieItem movieItem) {
        mTitle.setText(movieItem.getOriginal_title());
        mRate.setText(String.format(getString(R.string.rate), String.valueOf(movieItem.getVote_average())));
        mOverview.setText(movieItem.getOverview());
        mReleaseDate.setText(movieItem.getRelease_date().substring(0, 4));
        Picasso.with(this).load("http://image.tmdb.org/t/p/w342/" + movieItem.getPoster_path()).placeholder(R.mipmap.ic_launcher).into(mPoster);
    }


    @Override
    public void onMovieTrailersCompleted(ArrayList<TrailerItem> trailerData) {
        if (trailerData != null) {
            if (trailerData.isEmpty())
                setNoTrailerUI(getResources().getString(R.string.no_trailers));
            mTrailerAdapter.setTrailerData(trailerData);
        } else {
            showSnackBar(getString(R.string.smth_went_wrong));
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
        showSnackBar(message);
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
