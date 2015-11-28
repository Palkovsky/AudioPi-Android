package com.example.andrzej.audiocontroller.utils;


import com.example.andrzej.audiocontroller.MyApplication;
import com.example.andrzej.audiocontroller.models.Track;

public class PlaybackUtils {
    public static boolean useMediaVolumeStream(){
        Track track = MyApplication.streamManager.getCurrentTrack();
        return (track != null && !track.isPaused() && track.isPlaying());
    }
}
