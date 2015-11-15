package com.example.andrzej.audiocontroller.models;


public class Track extends ExploreItem{
    public Track() {
        setDirectory(false);
    }

    public Track(Playlist playlist) {
        super(playlist);
        setDirectory(false);
    }
}
