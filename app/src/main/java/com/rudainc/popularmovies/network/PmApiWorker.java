package com.rudainc.popularmovies.network;

import android.util.Log;

import com.rudainc.popularmovies.BuildConfig;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

public class PmApiWorker {


    private static final String EDGE_URL = "http://api.themoviedb.org/3/movie/";
    final private String API_KEY = BuildConfig.API_KEY;


    private static PmApiWorker retroWorker;

    private static volatile PmApiService restClient;


    private PmApiWorker() {

        createRestWorker();

    }

    public static PmApiWorker getInstance() {
        if (retroWorker == null) {
            synchronized (PmApiWorker.class) {
                if (retroWorker == null) {
                    retroWorker = new PmApiWorker();
                }
            }
        }
        return retroWorker;
    }

    public OkHttpClient getOkHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(interceptor);
        builder.addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
//                if(Connectivity.isConnected())
                return originalResponse;
            }

        });
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
//                if(Connectivity.isConnected())
                try {
                    Log.i("RESPONSE", originalResponse.header("Auth-Status"));
                    if (Integer.parseInt(originalResponse.header("Auth-Status")) == 0) {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return originalResponse;
            }
        });
        return builder.build();

    }

    private void createRestWorker() {


        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(EDGE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(getOkHttpClient())
                .build();

        restClient = restAdapter.create(PmApiService.class);
    }

    public Observable<BaseResponse> getMovies(String endpoint, String page) {
        Map<String, String> queryMap = new LinkedHashMap<>();
        queryMap.put("api_key", API_KEY);
        queryMap.put("page", page);

        if (endpoint.equals("popular"))
            return restClient.getPopularMoviesList(queryMap);
        else if (endpoint.equals("top_rated"))
            return restClient.getTopRatedMoviesList(queryMap);

        return restClient.getPopularMoviesList(queryMap);
    }
}
