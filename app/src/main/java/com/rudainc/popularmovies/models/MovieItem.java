package com.rudainc.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieItem implements Parcelable {
    private String id;
    private String original_title;
    private String poster_path;
    private String overview;
    private String vote_average;
    private String release_date;
    private String is_favorite;
    private String is_pinned;

    public MovieItem(String id, String original_title, String poster_path, String overview, String vote_average, String release_date, String is_favorite, String is_pinned) {
        this.id = id;
        this.original_title = original_title;
        this.poster_path = poster_path;
        this.overview = overview;
        this.vote_average = vote_average;
        this.release_date = release_date;
        this.is_favorite = is_favorite;
        this.is_pinned = is_pinned;
    }

    protected MovieItem(Parcel in) {
        id = in.readString();
        original_title = in.readString();
        poster_path = in.readString();
        overview = in.readString();
        vote_average = in.readString();
        release_date = in.readString();
        is_favorite = in.readString();
        is_pinned = in.readString();
    }

    public String getId() {
        return id;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public String getVote_average() {
        return vote_average;
    }

    public String getRelease_date() {
        return release_date;
    }

    public String is_favorite() {
        return is_favorite;
    }

    public String is_pinned() {
        return is_pinned;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(original_title);
        dest.writeString(poster_path);
        dest.writeString(overview);
        dest.writeString(vote_average);
        dest.writeString(release_date);
        dest.writeString(is_favorite);
        dest.writeString(is_pinned);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MovieItem> CREATOR = new Creator<MovieItem>() {
        @Override
        public MovieItem createFromParcel(Parcel in) {
            return new MovieItem(in);
        }

        @Override
        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };
}
