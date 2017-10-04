package com.rudainc.popularmovies.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.rudainc.popularmovies.R;
import com.rudainc.popularmovies.fragments.FavoritesFragment;
import com.rudainc.popularmovies.fragments.InfoFragment;
import com.rudainc.popularmovies.fragments.MoviesFragment;
import com.rudainc.popularmovies.utils.ToastListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.my_ads_banner)
    AdView mAdView;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.getMenu().getItem(0).setChecked(true);
        if (savedInstanceState == null)
            changeFragment(new MoviesFragment());

        navigationView.setNavigationItemSelectedListener(this);
        loadAds();
        loadInAds();
    }

    public void setToolbarText(String title) {
        toolbar.setTitle(title);
//        getSupportActionBar().setTitle(title);
    }

//    public void showMenu() {
//        toolbar.showOverflowMenu();
//    }


    private void loadAds() {
        mAdView.setAdListener(new ToastListener(this));
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(this);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.i("MainActivity","tap");
        if (id == R.id.nav_movies) {
            Log.i("MainActivity","movies");
            changeFragment(new MoviesFragment());
        } else if (id == R.id.nav_favorites) {
            Log.i("MainActivity","favorites");
            changeFragment(new FavoritesFragment());
        } else if (id == R.id.nav_ads) {
               Answers.getInstance().logCustom(new CustomEvent("Open Ads via NavMenu"));
            Log.i("MainActivity","ads");
            mInterstitialAd.show();
        } else if (id == R.id.nav_info) {
            Log.i("MainActivity","info");
            changeFragment(new InfoFragment());
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    private void changeFragment(Fragment fragment) {
        if (getSupportFragmentManager().findFragmentByTag(fragment.getClass().getName()) == null)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, fragment.getClass().getName()).commit();
        else
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    getSupportFragmentManager().findFragmentByTag(fragment.getClass().getName()),
                    fragment.getClass().getName()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item); // important line
    }


}
