package com.rudainc.popularmovies.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rudainc.popularmovies.R;
import com.rudainc.popularmovies.activities.MainActivity;
import com.rudainc.popularmovies.adapters.MoviesAdapter;

import butterknife.ButterKnife;

/**
 * Created on 04.10.2017.
 */

public class InfoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_info, container, false);
        ButterKnife.bind(this, v);
        ((MainActivity)getActivity()).setToolbarText(getString(R.string.title_info));

        return v;
    }


}
