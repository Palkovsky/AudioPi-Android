package com.example.andrzej.audiocontroller.utils;


import com.example.andrzej.audiocontroller.models.ExploreItem;
import com.example.andrzej.audiocontroller.models.Metadata;
import com.example.andrzej.audiocontroller.models.Playlist;
import com.example.andrzej.audiocontroller.models.Track;
import com.example.andrzej.audiocontroller.models.dbmodels.PlaylistDb;
import com.example.andrzej.audiocontroller.models.dbmodels.TrackDb;

import java.util.ArrayList;
import java.util.List;

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

    public static Playlist dbToStandard(PlaylistDb playlistDb) {
        Playlist playlist = new Playlist();
        playlist.setName(playlistDb.name);
        playlist.setCoverUrl(playlistDb.coverUrl);
        playlist.setDbId(playlistDb.getId());
        playlist.setType("local");

        List<Track> tracks = new ArrayList<>();
        playlist.setTracks(tracks);

        for (TrackDb trackDb : playlistDb.tracks()) {
            Track track = new Track();
            Metadata metadata = new Metadata();
            metadata.setCoverUrl(trackDb.coverUrl);
            metadata.setArtist(trackDb.artist);
            metadata.setAlbum(trackDb.album);
            metadata.setLength(trackDb.length);
            metadata.setGenre(trackDb.genre);
            metadata.setFilesize(trackDb.filesize);

            track.setMetadata(metadata);
            track.setPath(trackDb.path);
            track.setName(trackDb.name);
            track.setType("local");
            track.setPlaylist(playlist);
            track.setDbId(trackDb.getId());
            track.setPlaylistPosition(trackDb.position);

            playlist.getTracks().add(track);
        }

        return playlist;
    }

    public static PlaylistDb standardToDb(Playlist playlist) {
        PlaylistDb playlistDb = new PlaylistDb();
        playlistDb.name = playlist.getName();
        playlistDb.coverUrl = playlist.getCoverUrl();

        for (Track track : playlist.getTracks()) {
            TrackDb trackDb = standardToDb(track);
            trackDb.playlist = playlistDb;
            trackDb.save();
        }

        return playlistDb;
    }

    public static TrackDb standardToDb(Track track) {
        TrackDb trackDb = new TrackDb();
        Metadata metadata = track.getMetadata();

        trackDb.name = track.getName();
        trackDb.album = metadata.getAlbum();
        trackDb.artist = metadata.getArtist();
        trackDb.coverUrl = metadata.getCoverUrl();
        trackDb.filesize = metadata.getFilesize();
        trackDb.genre = metadata.getGenre();
        trackDb.path = track.getPath();
        trackDb.length = metadata.getLength();
        trackDb.position = track.getPlaylistPosition();

        return trackDb;
    }

    public static Track dbToStandard(TrackDb trackDb) {
        Track track = new Track();
        Metadata metadata = new Metadata();

        metadata.setAlbum(trackDb.album);
        metadata.setArtist(trackDb.artist);
        metadata.setFilesize(trackDb.filesize);
        metadata.setCoverUrl(trackDb.coverUrl);
        metadata.setGenre(trackDb.genre);
        metadata.setLength(trackDb.length);
        track.setDbId(trackDb.getId());
        track.setMetadata(metadata);
        track.setName(trackDb.name);
        track.setPath(trackDb.path);
        track.setPlaylistPosition(trackDb.position);

        return track;
    }

    public static List<Track> dbToStandard(List<TrackDb> trackDbs) {
        List<Track> tracks = new ArrayList<>();

        for (TrackDb trackDb : trackDbs)
            tracks.add(dbToStandard(trackDb));

        return tracks;
    }

    public static int androidVolumeToStandard(int vol) {
        switch (vol) {
            default:
            case 15:
                return 100;
            case 14:
                return 92;
            case 13:
                return 84;
            case 12:
                return 76;
            case 11:
                return 68;
            case 10:
                return 61;
            case 9:
                return 54;
            case 8:
                return 46;
            case 7:
                return 39;
            case 6:
                return 33;
            case 5:
                return 27;
            case 4:
                return 19;
            case 3:
                return 11;
            case 2:
                return 8;
            case 1:
                return 5;
            case 0:
                return 0;
        }
    }
}
