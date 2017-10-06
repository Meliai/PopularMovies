package com.rudainc.popularmovies.activities;

import android.animation.Animator;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rudainc.popularmovies.R;
import com.rudainc.popularmovies.custom_views.EndlessRecyclerOnScrollListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import io.codetail.widget.RevealFrameLayout;

public class SearchActivity extends BaseActivity {

    @BindView(R.id.search_bar)
    LinearLayout mSearchBar;

    @BindView(R.id.et_search)
    EditText mSearch;

    @BindView(R.id.clear)
    ImageView ivClearEditText;

    @BindView(R.id.search_activity)
    RevealFrameLayout revealFrameLayout;

    @BindView(R.id.rv_search_results)
    RecyclerView mRecyclerViewSearch;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.layout_no_search)
    View mLayoutNoSearch;


    private EndlessRecyclerOnScrollListener mScrollListener;


    @OnClick(R.id.back)
    void back() {
        onBackPressed();
    }

    @OnClick(R.id.clear)
    void clearEditText() {
        mSearch.setText("");
        toggleClearSearch(true);
        mSearch.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mSearch, InputMethodManager.SHOW_IMPLICIT);

    }


    @OnEditorAction(R.id.et_search)
    boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            mSearch.clearFocus();
            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(mSearch.getWindowToken(), 0);
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        initRv();
        postAnimation();
        mSearch.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mSearch, InputMethodManager.SHOW_IMPLICIT);
//        initSearchTextListener();
    }


    private void initScrollListener() {
//        mScrollListener = new EndlessRecyclerOnScrollListener(mLinearLayoutManager) {
//            @Override
//            public void onLoadMore(int current_page, boolean isFullyLoaded) {
//                if (!isFullyLoaded) {
//                    if (mPresenter.getCurrentQuery() != null)
//                        getData(mPresenter.getCurrentQuery(), current_page);
//                }
//            }
//        };
    }


    private void toggleClearSearch(boolean clear) {
        ivClearEditText.setVisibility(clear ? View.INVISIBLE : View.VISIBLE);
        if (clear) {
//            toggleErrorView(null, "");
            mScrollListener.setLoading(false);
            toggleNoSearchLayout(false);
            mScrollListener.setCurrent_page(0);
            mSwipeRefreshLayout.setRefreshing(false);
//            mAdapter.updateVideoItemList(new ArrayList<>());
//            mPresenter.cancelSearchRequest();
        }
    }


    private void toggleNoSearchLayout(boolean show) {
        mLayoutNoSearch.setVisibility(show ? View.VISIBLE : View.GONE);
    }


    private void initRv() {
        initScrollListener();
        mSwipeRefreshLayout.setEnabled(false);
//        mRecyclerViewSearch.setLayoutManager(mLinearLayoutManager);
//        mRecyclerViewSearch.setAdapter(mAdapter);
        mRecyclerViewSearch.addOnScrollListener(mScrollListener);
//        mAdapter.setOnActionVideoListener(this);
//        mAdapter.setOnSelectListener(this);
//        mAdapter.setOnRetryPaginationListener(this);
    }

    private void postAnimation() {
    new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
            mSearchBar.setVisibility(View.VISIBLE);
                    animateEditText();
        }
    },600);
    }

    private void animateEditText() {
        // get the center for the clipping circle
        int cx = mSearchBar.getRight();
        int cy = (mSearchBar.getTop() + mSearchBar.getBottom()) / 2;

        // get the final radius for the clipping circle
        int dx = Math.max(cx, mSearchBar.getWidth() - cx);
        int dy = Math.max(cy, mSearchBar.getHeight() - cy);
        float finalRadius = (float) Math.hypot(dx, dy);

        // Android native animator
        Animator animator =
                ViewAnimationUtils.createCircularReveal(mSearchBar, cx, cy, 0, finalRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(500);
        animator.start();
    }


    private void getData(String query, int page) {

        mSwipeRefreshLayout.setRefreshing(true);
//        if (page != 1)
//            new Handler().post(() -> mAdapter.addPaginationFooter(null, ""));
//        else
//            mScrollListener.setFullyLoaded(false);
//        mScrollListener.setCurrent_page(page);
    }


}
