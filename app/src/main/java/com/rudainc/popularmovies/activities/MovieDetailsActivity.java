package com.rudainc.popularmovies.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;


import com.rudainc.popularmovies.R;
import com.rudainc.popularmovies.models.MovieItem;
import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends BaseActivity {

    private TextView mTitle;
    private ImageView mPoster;
    private TextView mRate;
    private TextView mOverview;
    private TextView mReleaseDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        getSupportActionBar().setTitle(getString(R.string.title_details));

        mTitle = (TextView) findViewById(R.id.tv_origin_title);
        mRate = (TextView) findViewById(R.id.rate);
        mOverview = (TextView) findViewById(R.id.overview);
        mPoster = (ImageView) findViewById(R.id.poster);
        mReleaseDate = (TextView) findViewById(R.id.release);

        MovieItem movieItem = (MovieItem) getIntent().getSerializableExtra("data");

        fillData(movieItem);

    }

    private void fillData(MovieItem movieItem) {
        mTitle.setText(movieItem.getOriginal_title());
        mRate.setText(String.format(getString(R.string.rate),String.valueOf(movieItem.getVote_average())));
        mOverview.setText(movieItem.getOverview());
        mReleaseDate.setText(movieItem.getRelease_date().substring(0, 4));
        Picasso.with(this).load("http://image.tmdb.org/t/p/w342/" + movieItem.getPoster_path()).placeholder(R.mipmap.ic_launcher).into(mPoster);
    }
}
