package com.example.andrzej.audiocontroller.models.dbmodels;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Tracks")
public class TrackDb extends Model {
    @Column
    public String name;
    @Column
    public String path;
    @Column
    public String artist;
    @Column
    public String album;
    @Column
    public String genre;
    @Column
    public String coverUrl;
    @Column
    public int length;
    @Column
    public double filesize;
    @Column(name = "Playlist", index = true)
    public PlaylistDb playlist;
}
