package com.rudainc.popularmovies.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.rudainc.popularmovies.R;
import com.rudainc.popularmovies.models.MovieItem;

public class MovieDetailsActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        MovieItem s = (MovieItem) getIntent().getSerializableExtra("data");
        Log.i("Data",s.toString());
    }
}
