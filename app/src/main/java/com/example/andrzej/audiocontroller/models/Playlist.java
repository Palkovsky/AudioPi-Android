package com.example.andrzej.audiocontroller.models;


import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

public class Playlist implements ParentListItem {

    private String name;
    private String coverUrl;
    private String type;
    private int position;
    private List<Track> tracks;
    private long dbId;

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

    public int position() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        for (ExploreItem track : tracks)
            track.setPlaylist(this);
        this.tracks = tracks;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public List<Track> getChildItemList() {
        return tracks;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

    public String getType() {
        return type;
    }

    public boolean canGoNext() {
        return tracks.size() > 0 && position < tracks.size() - 1;
    }

    public boolean canGoPrev() {
        return tracks.size() > 0 && position > 0;
    }

    public void next() {
            tracks.get(position).setPlaying(false);
            position++;
            tracks.get(position).setPlaying(true);

    }

    public void prev() {
        if(position < tracks.size()) {
            tracks.get(position).setPlaying(false);
            position--;
            tracks.get(position).setPlaying(true);
        }
    }

    public int getPosition() {
        return position;
    }

    public boolean isLocal() {
        return type.equals("local");
    }

    public long getDbId() {
        return dbId;
    }

    public void setDbId(long dbId) {
        this.dbId = dbId;
    }
}
