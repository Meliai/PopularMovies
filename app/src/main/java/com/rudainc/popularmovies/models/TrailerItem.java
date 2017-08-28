package com.rudainc.popularmovies.models;

public class TrailerItem {

    private String id;
    private String key;
    private String name;

    public TrailerItem(String id, String key, String name) {
        this.id = id;
        this.key = key;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }
}
