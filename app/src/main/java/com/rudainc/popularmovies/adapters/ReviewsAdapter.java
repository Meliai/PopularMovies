package com.rudainc.popularmovies.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rudainc.popularmovies.R;
import com.rudainc.popularmovies.models.ReviewItem;

import java.util.ArrayList;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder> {

    private final Context context;
    private ArrayList<ReviewItem> mReviewsData;

    public ReviewsAdapter(Context context) {
        this.context = context;

    }


    public class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder {

        private TextView mReviewsAuthor;
        private TextView mReviewsContent;
        private CircleImageView mUserImage;
        private TextView mUserInitials;

        public ReviewsAdapterViewHolder(View view) {
            super(view);
            mReviewsAuthor = (TextView) view.findViewById(R.id.tv_reviews_author);
            mReviewsContent = (TextView) view.findViewById(R.id.tv_reviews_content);
            mUserImage = (CircleImageView)view.findViewById(R.id.iv_user_image);
            mUserInitials = (TextView) view.findViewById(R.id.tv_username_initials);
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
        reviewsAdapterViewHolder.mUserInitials.setText(reviewItem.getAuthor().charAt(0)+"");

        int[] colors = context.getResources().getIntArray(R.array.review);

        reviewsAdapterViewHolder.mUserImage.setBorderColor(ContextCompat.getColor(context,R.color.colorAccent));
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
