package com.example.andrzej.audiocontroller.models;


import android.os.Bundle;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Directory {

    private String path;
    private boolean grid;
    private Parcelable savedState;
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


    public List<ExploreItem> getItems() {
        return items;
    }

    public void setItems(List<ExploreItem> items) {
        this.items = items;
    }


    public Parcelable getSavedState() {
        return savedState;
    }

    public void setSavedState(Parcelable savedState) {
        this.savedState = savedState;
    }
}
