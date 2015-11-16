package com.example.andrzej.audiocontroller.models;


public class Track extends ExploreItem{

    private boolean playing;
    private boolean paused;
    private float posMiliSecs;
    private float totalMiliSecs;

    public Track() {
        setDirectory(false);
    }

    public Track(Playlist playlist) {
        super(playlist);
        setDirectory(false);
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
}
