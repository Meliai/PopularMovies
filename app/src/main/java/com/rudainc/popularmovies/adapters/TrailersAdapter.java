package com.rudainc.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rudainc.popularmovies.R;
import com.rudainc.popularmovies.models.MovieItem;
import com.rudainc.popularmovies.models.TrailerItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersAdapterViewHolder> {

    private final Context context;
    private ArrayList<TrailerItem> mTrailersData;


    private final TrailersAdapterOnClickHandler mClickHandler;

    public interface TrailersAdapterOnClickHandler {
        void onClick(TrailerItem trailerItem);
    }


    public TrailersAdapter(Context context, TrailersAdapterOnClickHandler clickHandler) {
        this.context = context;
        mClickHandler = clickHandler;
    }


    public class TrailersAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

       private TextView mTrailerName;

        public TrailersAdapterViewHolder(View view) {
            super(view);
            mTrailerName = (TextView)view.findViewById(R.id.tv_trailer_name);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            TrailerItem trailerItem = mTrailersData.get(adapterPosition);

            mClickHandler.onClick(trailerItem);
        }
    }

    @Override
    public TrailersAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.item_trailer;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new TrailersAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailersAdapterViewHolder trailersAdapterViewHolder, int position) {
        TrailerItem trailerItem = mTrailersData.get(position);
        trailersAdapterViewHolder.mTrailerName.setText(trailerItem.getName());
    }

    @Override
    public int getItemCount() {
        if (null == mTrailersData) return 0;
        return mTrailersData.size();
    }


    public void setTrailerData(ArrayList<TrailerItem> trailerData) {
        mTrailersData = trailerData;
        notifyDataSetChanged();
    }
}
