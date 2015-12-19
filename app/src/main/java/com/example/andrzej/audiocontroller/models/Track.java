package com.example.andrzej.audiocontroller.models;


public class Track extends ExploreItem {
    private boolean playing;
    private boolean paused;
    private boolean offline = true;
    private float posMiliSecs;
    private float totalMiliSecs;
    private long dbId;
    private long pauseStartTime;
    private int playlistPosition;

    public Track() {
        setDirectory(false);
    }

    public Track(Playlist playlist) {
        super(playlist);
        setDirectory(false);
        pauseStartTime = System.currentTimeMillis();
    }

    public long sinceLastPause() {
        return System.currentTimeMillis() - pauseStartTime;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        if (!paused)
            pauseStartTime = System.currentTimeMillis();
        this.paused = paused;
    }

    public float getMilliPosSecs() {
        return posMiliSecs;
    }

    public void setMilliPosSecs(float posMiliSecs) {
        this.posMiliSecs = posMiliSecs;
    }

    public float getMilliTotalSecs() {
        return totalMiliSecs;
    }

    public void setMilliTotalSecs(float totalMiliSecs) {
        this.totalMiliSecs = totalMiliSecs;
    }

    public long getDbId() {
        return dbId;
    }

    public void setDbId(long dbId) {
        this.dbId = dbId;
    }

    public boolean isOffline() {
        return offline;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    public int getPlaylistPosition() {
        return playlistPosition;
    }

    public void setPlaylistPosition(int playlistPosition) {
        this.playlistPosition = playlistPosition;
    }

    public void setPauseStartTime(long pauseStartTime) {
        this.pauseStartTime = pauseStartTime;
    }
}
