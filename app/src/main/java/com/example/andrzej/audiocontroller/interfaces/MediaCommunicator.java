package com.example.andrzej.audiocontroller.interfaces;

import com.example.andrzej.audiocontroller.models.ExploreItem;
import com.example.andrzej.audiocontroller.models.Playlist;

public interface MediaCommunicator {
    void onPlaylistStart(Playlist playlist, int position);
    void onTrackStart(ExploreItem track);
}
