package com.rudainc.popularmovies.activities;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.Space;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.rudainc.popularmovies.R;
import com.rudainc.popularmovies.adapters.ViewPagerAdapter;
import com.rudainc.popularmovies.custom_views.NavigationItem;
import com.rudainc.popularmovies.custom_views.NavigationTabBar;
import com.rudainc.popularmovies.custom_views.ZoomOutPageTransformer;
import com.rudainc.popularmovies.fragments.FavoritesFragment;
import com.rudainc.popularmovies.fragments.InfoFragment;
import com.rudainc.popularmovies.fragments.MoviesFragment;
import com.rudainc.popularmovies.utils.PopularMoviesKeys;
import com.rudainc.popularmovies.utils.ToastListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, PopularMoviesKeys {

    @BindView(R.id.my_ads_banner)
    AdView mAdView;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.appbar)
    AppBarLayout appBarLayout;

    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;

    @OnClick(R.id.search)
    void search() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    @BindView(R.id.vp_space)
    Space mSpace;
    @BindView(R.id.ll_movies)
    LinearLayout mMovies;
    @BindView(R.id.tabbar)
    NavigationTabBar mNavigationTabBar;

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private InterstitialAd mInterstitialAd;
    private PopupMenu popup;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        setToolbarText(getString(R.string.title_movies));
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);
        initViewPager();
        initTopBar();

        if (savedInstanceState == null) {
            changeFragment(MoviesFragment.newInstance(POPULAR), TAG_MOVIES_POPULAR);
        }

        loadAds();
        loadInAds();

    }

    public void setToolbarText(String title) {
        mToolbarTitle.setText(title);
    }

    private void loadAds() {
        mAdView.setAdListener(new ToastListener(this));
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void showViewPager(boolean show) {
        mSpace.setVisibility(show? View.VISIBLE:View.GONE);
        mViewPager.setVisibility(show ? View.VISIBLE : View.GONE);
        mNavigationTabBar.setVisibility(show ? View.VISIBLE : View.GONE);
        findViewById(R.id.fragment_container).setVisibility(!show ? View.VISIBLE : View.GONE);
        if (!show)
            appBarLayout.setExpanded(true, true);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_movies:
                showViewPager(true);
                setToolbarText(getString(R.string.title_movies));
                break;
            case R.id.nav_favorites:
                showViewPager(false);
                setToolbarText(getString(R.string.favorite));
                changeFragment(new FavoritesFragment(), TAG_FAVORITE);
                break;
            case R.id.nav_ads:
                Answers.getInstance().logCustom(new CustomEvent(getString(R.string.ce_open_ads)));
                mInterstitialAd.show();
                break;
            case R.id.nav_info:
                showViewPager(false);
                setToolbarText(getString(R.string.title_info));
                changeFragment(new InfoFragment(), TAG_INFO);
                break;
            default:
                changeFragment(new MoviesFragment(), TAG_MOVIES_POPULAR);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changeFragment(Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, tag).commit();
    }

    private void initViewPager() {
        mViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
    }

    private void initTopBar() {
        mNavigationTabBar.setList(getNavigationItems());
        mNavigationTabBar.setViewPager(mViewPager);
    }

    private List<NavigationItem> getNavigationItems() {
        List<NavigationItem> list = new ArrayList<>();
        TypedArray icons = getResources().obtainTypedArray(R.array.bottom_icons);
        TypedArray iconsActive = getResources().obtainTypedArray(R.array.bottom_icons_active);
        String[] titles = getResources().getStringArray(R.array.navigation_titles);
        for (int i = 0; i < icons.length(); i++)
            list.add(new NavigationItem(icons.getResourceId(i, -1), iconsActive.getResourceId(i, -1), titles[i]));
        icons.recycle();
        iconsActive.recycle();
        return list;
    }

    private void loadInAds() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.inter_ads_menu));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();

            }

        });
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }

}
