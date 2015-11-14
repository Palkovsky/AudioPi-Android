package com.example.andrzej.audiocontroller.models;

import android.content.Context;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.example.andrzej.audiocontroller.R;

import java.util.List;

public class Playlist implements ParentListItem {

    private String name;
    private String coverUrl;
    private String type;
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
        for (ExploreItem track : tracks)
            track.setPlaylist(this);
        this.tracks = tracks;
    }

    public String getType(Context context) {
        if (type.equals("artist"))
            return context.getString(R.string.artistSimple);
        else if (type.equals("album"))
            return context.getString(R.string.albumSimple);
        else if (type.equals("genre"))
            return context.getString(R.string.genreSimple);
        else
            return null;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public List<ExploreItem> getChildItemList() {
        return tracks;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

    public String getType() {
        return type;
    }
}
