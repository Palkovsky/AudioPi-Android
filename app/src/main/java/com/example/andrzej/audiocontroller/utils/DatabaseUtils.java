package com.example.andrzej.audiocontroller.utils;


import com.example.andrzej.audiocontroller.models.dbmodels.PlaylistDb;
import com.example.andrzej.audiocontroller.models.dbmodels.TrackDb;

import java.util.List;

public class DatabaseUtils {

    //This method handles positions after deleting record
    public static void handlePositions(long playlistId, int removedPosition){
        List<TrackDb> tracks = PlaylistDb.load(PlaylistDb.class, playlistId).tracks();

        for(TrackDb trackDb : tracks){
            if(trackDb.position > removedPosition){
                trackDb.position--;
                trackDb.save();
            }
        }
    }
}
