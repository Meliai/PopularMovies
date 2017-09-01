package com.rudainc.popularmovies.models;

import java.util.ArrayList;

public class MoviesListData {
    private ArrayList<MovieItem> mList;

    private int page;

    private int total_pages;

    public MoviesListData(ArrayList<MovieItem> mList, int page, int total_pages) {
        this.mList = mList;
        this.page = page;
        this.total_pages = total_pages;
    }


    public ArrayList<MovieItem> getmList() {
        return mList;
    }

    public int getPage() {
        return page;
    }

    public int getTotal_pages() {
        return total_pages;
    }
}
