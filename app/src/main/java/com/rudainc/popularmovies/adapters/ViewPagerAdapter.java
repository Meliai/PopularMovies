package com.rudainc.popularmovies.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.rudainc.popularmovies.fragments.MoviesFragment;
import com.rudainc.popularmovies.utils.PopularMoviesKeys;

public class ViewPagerAdapter extends FragmentPagerAdapter implements PopularMoviesKeys {


    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return MoviesFragment.newInstance(POPULAR);

            case 1:
                return MoviesFragment.newInstance(TOP_RATED);


        }
        return null;
    }


    @Override
    public int getCount() {
        return 2;
    }
}
