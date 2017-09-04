package com.rudainc.popularmovies.custom_views;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public abstract class EndlessRecyclerOnScrollListener extends
        RecyclerView.OnScrollListener {
    public static String TAG = EndlessRecyclerOnScrollListener.class
            .getSimpleName();



    private int previousTotal = 0;


    private boolean loading = false;



    private boolean isFullyLoaded = false;
    private int visibleThreshold = 1;
    private int firstVisibleItem, visibleItemCount, totalItemCount;



    private int current_page = 1;

    private RecyclerView.LayoutManager layoutManager;

    public EndlessRecyclerOnScrollListener(
            RecyclerView.LayoutManager linearLayoutManager) {
        this.layoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
//        visibleItemCount = recyclerView.getChildCount();
//        totalItemCount = layoutManager.getItemCount();
//        firstVisibleItem = layoutManager instanceof LinearLayoutManager ? ((GridLayoutManager)layoutManager).findFirstVisibleItemPosition():((GridLayoutManager)layoutManager).findFirstVisibleItemPosition();
//
//        Log.i("WHAT????", loading +"   "+ totalItemCount +"   "+ (totalItemCount - visibleItemCount) +"  " + firstVisibleItem + visibleThreshold);
//
//        if (!loading && totalItemCount > 0
//                && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
//            // End has been reached
//
//            // Do something
//            current_page++;
//            onLoadMore(current_page, isFullyLoaded);
//
//            loading = true;
//        }

        visibleItemCount = layoutManager.getChildCount();
        totalItemCount = layoutManager.getItemCount();
        int pastVisiblesItems = ((GridLayoutManager)layoutManager).findFirstVisibleItemPosition();

        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
            //bottom of recyclerview
            current_page++;
            onLoadMore(current_page, isFullyLoaded);
        }
    }

    public void setPreviousTotal(int previousTotal) {
        this.previousTotal = previousTotal;
    }

    public int getCurrent_page() {
        return current_page;
    }

    public boolean isFullyLoaded() {
        return isFullyLoaded;
    }

    public void setFullyLoaded(boolean fullyLoaded) {
        isFullyLoaded = fullyLoaded;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public abstract void onLoadMore(int current_page, boolean isFullyLoaded);

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

}
