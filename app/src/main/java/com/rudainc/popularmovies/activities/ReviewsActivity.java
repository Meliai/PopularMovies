package com.rudainc.popularmovies.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.rudainc.popularmovies.R;
import com.rudainc.popularmovies.adapters.ReviewsAdapter;
import com.rudainc.popularmovies.interfaces.OnMovieReviewsCompleted;
import com.rudainc.popularmovies.models.MovieItem;
import com.rudainc.popularmovies.models.ReviewItem;
import com.rudainc.popularmovies.network.async.GetReviewsAsync;
import com.rudainc.popularmovies.utils.PopularMoviesKeys;
import com.rudainc.popularmovies.utils.ToastListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ReviewsActivity extends BaseActivity implements OnMovieReviewsCompleted, PopularMoviesKeys {

    @BindView(R.id.rv)
    RecyclerView rvReviews;

    @BindView(R.id.tv_no_data)
    TextView mNoData;

    @BindView(R.id.ads)
    ImageView ivAds;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;

    @OnClick(R.id.ads)
    void showAds(){
        mInterstitialAd.show();
    }

    private ReviewsAdapter mReviewsAdapter;

    private GetReviewsAsync getReviewsAsync;

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        ButterKnife.bind(this);
        ivAds.setVisibility(View.VISIBLE);

        MovieItem movieItem = (MovieItem) getIntent().getParcelableExtra(EXTRA_DATA);
        mToolbarTitle.setText(getString(R.string.title_reviews));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        mReviewsAdapter = new ReviewsAdapter(this);
        rvReviews.setAdapter(mReviewsAdapter);

        if (isOnline(this)) {
            getReviewsAsync = new GetReviewsAsync(this, movieItem.getId(), this);
            getReviewsAsync.execute();
        } else
            showSnackBar(getString(R.string.smth_went_wrong), true);

        loadAds();
    }

    @Override
    public void onMovieReviewsCompleted(ArrayList<ReviewItem> reviewData) {
        if (reviewData != null) {
            if (reviewData.isEmpty())
                setNoReviewsUI(getResources().getString(R.string.no_reviews));
            mReviewsAdapter.setReviewsData(reviewData);
        } else {
            showSnackBar(getString(R.string.smth_went_wrong), true);
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
        showSnackBar(message, true);
        setNoReviewsUI(getResources().getString(R.string.cant_upload_data));
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
}
