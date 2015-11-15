package com.example.andrzej.audiocontroller.utils;


import com.example.andrzej.audiocontroller.models.ExploreItem;
import com.example.andrzej.audiocontroller.models.Track;

public class Converter {
    public static Track exploreItemToTrack(ExploreItem item){
        Track track = new Track();
        track.setPath(item.getPath());
        track.setPlaylist(item.getPlaylist());
        track.setPath(item.getPath());
        track.setMetadata(item.getMetadata());
        track.setType(item.getType());
        track.setName(item.getName());
        return track;
    }
}
