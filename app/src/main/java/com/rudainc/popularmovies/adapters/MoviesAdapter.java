package com.rudainc.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.rudainc.popularmovies.R;
import com.rudainc.popularmovies.models.MovieItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    private final Context context;
    private ArrayList<MovieItem> mMoviesData;


    private final MoviesAdapterOnClickHandler mClickHandler;

    public interface MoviesAdapterOnClickHandler {
        void onClick(MovieItem movieItem);
    }


    public MoviesAdapter(Context context,MoviesAdapterOnClickHandler clickHandler) {
        this.context = context;
        mClickHandler = clickHandler;
    }


    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mPoster;

        public MoviesAdapterViewHolder(View view) {
            super(view);
            mPoster = (ImageView) view.findViewById(R.id.iv_movie_poster);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            MovieItem movieItem = mMoviesData.get(adapterPosition);
            mClickHandler.onClick(movieItem);
        }
    }

    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movies_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder moviesAdapterViewHolder, int position) {
        MovieItem movieItem = mMoviesData.get(position);
        Picasso.with(context).load("http://image.tmdb.org/t/p/w185/"+movieItem.getPoster_path()).placeholder(R.mipmap.ic_launcher).into(moviesAdapterViewHolder.mPoster);
    }

    @Override
    public int getItemCount() {
        if (null == mMoviesData) return 0;
        return mMoviesData.size();
    }


    public void setMoviesData(ArrayList<MovieItem> moviesData) {
        mMoviesData = moviesData;
        notifyDataSetChanged();
    }
}
