package com.example.andrzej.audiocontroller.models;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

public class Playlist implements ParentListItem{

    private String name;
    private String coverUrl;
    private int position;
    private List<ExploreItem> tracks;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public List<ExploreItem> getTracks() {
        return tracks;
    }

    public void setTracks(List<ExploreItem> tracks) {
        this.tracks = tracks;
    }

    @Override
    public List<ExploreItem> getChildItemList() {
        return tracks;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
