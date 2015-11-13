package com.example.andrzej.audiocontroller.models;

public class Metadata {
    private String artist;
    private String album;
    private String genre;
    private String coverUrl;
    private int length;
    private double filesize;

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getLength() {
        return length;
    }

    public String gerFormattedLength() {
        if (length < 0)
            return "";

        final int min = length / 60;
        final int sec = length - (min * 60);

        final String strMin = placeZeroIfNeeded(min);
        final String strSec = placeZeroIfNeeded(sec);
        return String.format("%s:%s", strMin, strSec);
    }


    public void setLength(int length) {
        this.length = length;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public double getFilesize() {
        return filesize;
    }

    public void setFilesize(double filesize) {
        this.filesize = filesize;
    }

    private String placeZeroIfNeeded(int number) {
        if (number < 10 && number > 0)
            return "0" + String.valueOf(number);
        else
            return String.valueOf(number);
    }
}
