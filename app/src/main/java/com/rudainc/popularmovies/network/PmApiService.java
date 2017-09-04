package com.rudainc.popularmovies.network;

import java.util.Map;

import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface PmApiService {


    @POST("popular")
    Observable<BaseResponse> getPopularMoviesList(@QueryMap Map<String, String> queryMap);

    @POST("top_rated")
    Observable<BaseResponse> getTopRatedMoviesList(@QueryMap Map<String, String> queryMap);


}
