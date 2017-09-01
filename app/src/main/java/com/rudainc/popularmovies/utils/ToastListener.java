package com.rudainc.popularmovies.utils;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;

public class ToastListener extends AdListener {
    Context context;
    private String mError;

    public ToastListener(Context context) {
        this.context = context;
    }

    @Override
    public void onAdFailedToLoad(int i) {
        super.onAdFailedToLoad(i);
        mError = "";
        switch (i) {
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                mError = "Internal Error";
                break;
            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                mError = "Invalid Request";
                break;
            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                mError = "Network Error";
                break;
        }
        Log.i("ADS", "onAdFailedToLoad - "+mError);
    }

}
