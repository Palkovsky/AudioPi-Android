package com.example.andrzej.audiocontroller.utils;


import com.example.andrzej.audiocontroller.models.ExploreItem;
import com.example.andrzej.audiocontroller.models.Track;

public class Converter {
    public static Track exploreItemToTrack(ExploreItem item) {
        Track track = new Track();
        track.setPath(item.getPath());
        track.setPlaylist(item.getPlaylist());
        track.setPath(item.getPath());
        track.setMetadata(item.getMetadata());
        track.setType(item.getType());
        track.setName(item.getName());
        return track;
    }

    public static int millisToSeconds(int millis) {
        return Math.round(millis / 1000);
    }

    public static String secsToFormattedTime(int time) {
        int mins = (int) (time / 60);
        int secs = time - (mins * 60);

        if (mins < 0 || secs < 0)
            return "00:00";


        String strSecs = String.valueOf(secs);
        String strMins = String.valueOf(mins);

        if (secs < 10)
            strSecs = "0" + String.valueOf(secs);
        if (mins < 10)
            strMins = "0" + String.valueOf(mins);

        return strMins + ":" + strSecs;
    }
}
