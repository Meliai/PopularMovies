package com.rudainc.popularmovies.models;

public class ReviewItem {
    private String id;
    private String author;
    private String content;

    public ReviewItem(String id, String author, String content) {
        this.id = id;
        this.author = author;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
