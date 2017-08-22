package com.rudainc.popularmovies;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class SquareFrameLayoutHeight extends AppCompatImageView {
    public SquareFrameLayoutHeight(@NonNull Context context) {
        super(context);
    }

    public SquareFrameLayoutHeight(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareFrameLayoutHeight(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec*2);
    }
}
