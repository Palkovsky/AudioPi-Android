package com.example.andrzej.audiocontroller.interfaces;

import com.example.andrzej.audiocontroller.models.Playlist;
import com.example.andrzej.audiocontroller.models.Track;

public interface MediaCommunicator {
    void onPlaylistStart(Playlist playlist, int position);
    void onTrackStart(Track track);
}
