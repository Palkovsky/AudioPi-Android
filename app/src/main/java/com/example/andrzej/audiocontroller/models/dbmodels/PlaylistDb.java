package com.example.andrzej.audiocontroller.models.dbmodels;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "Playlists")
public class PlaylistDb extends Model {

    @Column
    public String name;
    @Column
    public String coverUrl;
    @Column
    public long createdAt; //In millis, for sorting purposes

    public List<TrackDb> tracks() {
        return getMany(TrackDb.class, "Playlist");
    }

    public static List<PlaylistDb> getAll() {
        return new Select()
                .from(PlaylistDb.class)
                .orderBy("createdAt ASC")
                .execute();
    }
}
