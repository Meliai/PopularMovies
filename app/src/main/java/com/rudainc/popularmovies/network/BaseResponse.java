package com.rudainc.popularmovies.network;

import com.rudainc.popularmovies.models.MovieItem;

import java.util.ArrayList;

public class BaseResponse {

    private String page;
    private String total_pages;
    private ArrayList<MovieItem> results;

    public String getPage() {
        return page;
    }

    public String getTotal_pages() {
        return total_pages;
    }

    public ArrayList<MovieItem> getResults() {
        return results;
    }
}
