package com.rudainc.popularmovies.custom_views;

import android.support.annotation.DrawableRes;

public class NavigationItem {

    @DrawableRes
    public Integer icon;

    @DrawableRes
    public Integer activeIcon;

    public String title;

    public NavigationItem(Integer icon, Integer activeIcon, String title) {
        this.icon = icon;
        this.activeIcon = activeIcon;
        this.title = title;
    }
}