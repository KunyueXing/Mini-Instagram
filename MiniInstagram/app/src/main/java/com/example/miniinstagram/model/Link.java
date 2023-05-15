package com.example.miniinstagram.model;

public class Link {
    private String Url;
    private String title;

    public Link(String url, String title) {
        Url = url;
        this.title = title;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
