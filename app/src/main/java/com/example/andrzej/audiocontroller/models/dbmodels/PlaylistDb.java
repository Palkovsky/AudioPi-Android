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

    public PlaylistDb() {
    }

    public PlaylistDb(String name) {
        this.name = name;
        this.createdAt = System.currentTimeMillis();
    }

    public PlaylistDb(String name, String coverUrl) {
        this.name = name;
        this.coverUrl = coverUrl;
        this.createdAt = System.currentTimeMillis();
    }

    public List<TrackDb> tracks() {
        return new Select()
                .from(TrackDb.class)
                .where("Playlist = ?", getId())
                .orderBy("position ASC")
                .execute();
    }

    public static List<PlaylistDb> getAll() {
        return new Select()
                .from(PlaylistDb.class)
                .orderBy("createdAt ASC")
                .execute();
    }
}
