/*
Copyright 2014 Yahoo Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.rudainc.popularmovies.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;


/**
 * Util class for converting between dp, px and other magical pixel units
 */
public class PixelUtil {



    public static int dpToPx(Context context, int dp) {

        return Math.round(dp * getPixelScaleFactor(context));
    }

    public static int pxToDp(Context context, int px) {
        return Math.round(px / getPixelScaleFactor(context));
    }

    private static float getPixelScaleFactor(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float density = (float)(displayMetrics.densityDpi);
//        Log.i("REAL_DPI",Math.sqrt(Math.pow((double) displayMetrics.xdpi,2d) + Math.pow((double) displayMetrics.ydpi,2d))+" "+displayMetrics.xdpi + " "+displayMetrics.ydpi);
        return (float)(density / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static int getRawScreenWidth(Context context){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRealMetrics(displayMetrics);
        return displayMetrics.widthPixels;

    }

    public static int getScreenWidth(Context context){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;

    }

    public static int getRawScreenHeight(Context context){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRealMetrics(displayMetrics);
        return displayMetrics.heightPixels;

    }

    public static int getScreenHeight(Context context){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;

    }


    public static int getNavigationBarHeight(Context context)
    {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT?
                (getRawScreenHeight(context) - getScreenHeight(context)): (getRawScreenWidth(context) - getScreenWidth(context));
    }


    public static int getStatusBarHeight(Context context){
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }



    public static int getActionBarHeight(Context context){
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            return  TypedValue.complexToDimensionPixelSize(tv.data,context.getResources().getDisplayMetrics());
        }
        return 0;
    }



}
