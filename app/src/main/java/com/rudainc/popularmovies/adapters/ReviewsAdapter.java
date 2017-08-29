package com.rudainc.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rudainc.popularmovies.R;
import com.rudainc.popularmovies.models.ReviewItem;

import java.util.ArrayList;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder> {

    private final Context context;
    private ArrayList<ReviewItem> mReviewsData;


    public ReviewsAdapter(Context context) {
        this.context = context;

    }


    public class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder {

        private TextView mReviewsAuthor;
        private TextView mReviewsContent;

        public ReviewsAdapterViewHolder(View view) {
            super(view);
            mReviewsAuthor = (TextView) view.findViewById(R.id.tv_reviews_author);
            mReviewsContent = (TextView) view.findViewById(R.id.tv_reviews_content);
        }
    }

    @Override
    public ReviewsAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.item_review;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ReviewsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsAdapterViewHolder reviewsAdapterViewHolder, int position) {
        ReviewItem reviewItem = mReviewsData.get(position);
        reviewsAdapterViewHolder.mReviewsAuthor.setText(reviewItem.getAuthor());
        reviewsAdapterViewHolder.mReviewsContent.setText(reviewItem.getContent());
    }

    @Override
    public int getItemCount() {
        if (null == mReviewsData) return 0;
        return mReviewsData.size();
    }


    public void setReviewsData(ArrayList<ReviewItem> reviewsData) {
        mReviewsData = reviewsData;
        notifyDataSetChanged();
    }
}
