package com.rudainc.popularmovies.custom_views;


import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.rudainc.popularmovies.R;
import com.rudainc.popularmovies.utils.PixelUtil;

import java.util.ArrayList;
import java.util.List;


public class NavigationTabBar extends FrameLayout {


    public static final int ICON_SIZE_DP = 24;
    OnSelectChild onSelectChild;
    private List<NavigationItem> list;
    private List<Spring> childSprings;
    private ViewPager viewPager;


    private int currentItem = 0;

    private View underline;

    private LinearLayout containerChildTab;


    public NavigationTabBar(Context context) {
        this(context, null, 0);

    }

    public NavigationTabBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public NavigationTabBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public List<NavigationItem> getList() {
        return list;
    }

    public void setList(List<NavigationItem> list) {
        this.list = list;
        initChildContainer();
        addUnderline();
        initSpring();
    }

    private void initChildContainer() {
        containerChildTab = new LinearLayout(getContext());
        containerChildTab.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        addView(containerChildTab, layoutParams);
        addChilds();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i("NAV_BAR_HEIGHT", PixelUtil.getNavigationBarHeight(getContext())+"");
        underline.getLayoutParams().width = PixelUtil.getScreenWidth(getContext()) / list.size();
        underline.setTranslationX((PixelUtil.getScreenWidth(getContext()) / list.size())*viewPager.getCurrentItem());
        underline.requestLayout();

    }

    private void addChilds() {
        for (NavigationItem item : list) {
            View view = initChild(item);
            containerChildTab.addView(view);
        }
    }

    private void addUnderline() {
        underline = new View(getContext());
        underline.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        LayoutParams layoutParams = new LayoutParams(PixelUtil.getScreenWidth(getContext()) / list.size(), PixelUtil.dpToPx(getContext(), 3));
        layoutParams.gravity = Gravity.BOTTOM;
        addView(underline, layoutParams);
    }

    private LinearLayout initChild(final NavigationItem item) {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);
        linearLayout.addView(initChildImageView(item.icon));
        linearLayout.addView(initChildTextView(item.title));
        linearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = containerChildTab.indexOfChild(view);
                if (onSelectChild != null) {
                    onSelectChild.select(index, item);
                }
                viewPager.setCurrentItem(index);
            }
        });
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(0, PixelUtil.dpToPx(getContext(), ICON_SIZE_DP)*2, 1));
        return linearLayout;
    }

    private ImageView initChildImageView(Integer resId) {
        ImageView imageView = new ImageView(getContext());
        imageView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtil.dpToPx(getContext(), ICON_SIZE_DP)));
        imageView.setImageResource(resId);
        return imageView;
    }

    private TextView initChildTextView(String title) {
        TextView textView = new TextView(getContext());
        textView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(ContextCompat.getColor(getContext(),R.color.colorWhite));
        textView.setText(title);
        textView.setTextSize(12);
        textView.setVisibility(GONE);
        return textView;
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
        initVpListener(viewPager);
        if (currentItem == 0)
            itemChanged(currentItem);
    }


    private void initVpListener(ViewPager viewPager) {
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                itemChanged(position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                underline.setTranslationX((position + positionOffset) * underline.getWidth());
            }
        });
    }

    /**
     * change item in specific position
     *
     * @param position of item in layout
     */
    private void itemChanged(int position) {
        LinearLayout child = (LinearLayout) containerChildTab.getChildAt(currentItem);
        ((ImageView) child.getChildAt(0)).setImageResource(list.get(currentItem).icon);
        (child.getChildAt(1)).setVisibility(GONE);
        childSprings.get(currentItem).setEndValue(0);

        LinearLayout childPosition = (LinearLayout) containerChildTab.getChildAt(position);
        ((ImageView) childPosition.getChildAt(0)).setImageResource(list.get(position).activeIcon);
        (childPosition.getChildAt(1)).setVisibility(VISIBLE);
        childSprings.get(position).setEndValue(1);
        currentItem = position;
    }

    public void setOnSelectChild(OnSelectChild onSelectChild) {
        this.onSelectChild = onSelectChild;
    }

    private void initSpring() {
        childSprings = new ArrayList<>();
        SpringSystem springSystem = SpringSystem.create();

        for (int i = 0; i < containerChildTab.getChildCount(); i++) {
            Spring spring = springSystem.createSpring();
            spring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(50, 3));
            final int finalI = i;
            spring.addListener(new SimpleSpringListener() {
                @Override
                public void onSpringUpdate(Spring spring) {
                    super.onSpringUpdate(spring);
                    float value = (float) spring.getCurrentValue();
                    float scale = 1f + (value * 0.25f);
                    containerChildTab.getChildAt(finalI).setScaleX(scale);
                    containerChildTab.getChildAt(finalI).setScaleY(scale);
                }
            });
            childSprings.add(spring);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), (int) (containerChildTab.getChildAt(0).getMeasuredHeight() * 1.3));
    }


    public interface OnSelectChild {
        void select(int index, NavigationItem bottomNavigationItem);
    }
}