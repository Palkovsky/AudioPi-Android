package com.example.andrzej.audiocontroller.handlers;


import com.example.andrzej.audiocontroller.config.Filters;
import com.example.andrzej.audiocontroller.models.Playlist;

import java.util.ArrayList;
import java.util.List;

public class MediaManager {

    public MediaManager() {}

    public List<Playlist> applyFilter(int filter, List<Playlist> baseDataset){
        List<Playlist> filteredPlaylist = new ArrayList<>();

        if(filter == Filters.ALL)
            return baseDataset;

        for(Playlist playlist : baseDataset){
            if(playlist.getType().equals("artist") && filter == Filters.ARTISTS)
                filteredPlaylist.add(playlist);
            else if(playlist.getType().equals("album") && filter == Filters.ALBUMS)
                filteredPlaylist.add(playlist);
            else if(playlist.getType().equals("genre") && filter == Filters.GENRES)
                filteredPlaylist.add(playlist);
            else if (playlist.getType().equals("local") && filter == Filters.LOCAL_PLAYLISTS)
                filteredPlaylist.add(playlist);
        }
        return filteredPlaylist;
    }

}
