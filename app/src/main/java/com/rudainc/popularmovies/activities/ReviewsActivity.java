package com.rudainc.popularmovies.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.rudainc.popularmovies.R;
import com.rudainc.popularmovies.adapters.ReviewsAdapter;
import com.rudainc.popularmovies.interfaces.OnMovieReviewsCompleted;
import com.rudainc.popularmovies.models.MovieItem;
import com.rudainc.popularmovies.models.ReviewItem;
import com.rudainc.popularmovies.network.async.GetReviewsAsync;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewsActivity extends BaseActivity implements OnMovieReviewsCompleted{

    @BindView(R.id.rv)
    RecyclerView rvReviews;

    @BindView(R.id.tv_no_data)
    TextView mNoData;

    private ReviewsAdapter mReviewsAdapter;

    private GetReviewsAsync getReviewsAsync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle(getString(R.string.title_reviews));

        MovieItem movieItem = (MovieItem) getIntent().getSerializableExtra("data");

        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        mReviewsAdapter = new ReviewsAdapter(this);
        rvReviews.setAdapter(mReviewsAdapter);

        if (isOnline(this)) {
            getReviewsAsync = new GetReviewsAsync(this, movieItem.getId(), this);
            getReviewsAsync.execute();
        } else
            showSnackBar(getString(R.string.smth_went_wrong));
    }

    @Override
    public void onMovieReviewsCompleted(ArrayList<ReviewItem> reviewData) {
        if (reviewData != null) {
            if (reviewData.isEmpty())
                setNoReviewsUI(getResources().getString(R.string.no_reviews));
            mReviewsAdapter.setReviewsData(reviewData);
        } else {
            showSnackBar(getString(R.string.smth_went_wrong));
            setNoReviewsUI(getResources().getString(R.string.cant_upload_data));
        }
    }

    private void setNoReviewsUI(String text) {
        mNoData.setVisibility(View.VISIBLE);
        mNoData.setText(text);
        rvReviews.setVisibility(View.GONE);
    }

    @Override
    public void onMovieReviewsError(String message) {
        showSnackBar(message);
        setNoReviewsUI(getResources().getString(R.string.cant_upload_data));
    }
}
