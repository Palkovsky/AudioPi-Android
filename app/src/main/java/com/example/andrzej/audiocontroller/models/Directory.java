package com.example.andrzej.audiocontroller.models;


import java.util.ArrayList;
import java.util.List;

public class Directory {

    private String path;
    private int lastScrollPosition;
    private List<ExploreItem> items;

    public Directory(String path) {
        this.path = path;
        items = new ArrayList<>();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getLastScrollPosition() {
        return lastScrollPosition;
    }

    public void setLastScrollPosition(int lastScrollPosition) {
        this.lastScrollPosition = lastScrollPosition;
    }

    public List<ExploreItem> getItems() {
        return items;
    }

    public void setItems(List<ExploreItem> items) {
        this.items = items;
    }
}
