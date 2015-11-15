package com.example.andrzej.audiocontroller.config;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Endpoints {
    public static final String DOMAIN = "http://192.168.1.101:5000";

    public static final String URL_CHAR_QUESTION = "?";
    public static final String URL_CHAR_AMEPERSAND = "&";

    public static final String URL_DATA = DOMAIN + "/data";
    public static final String URL_COVER = DOMAIN + "/file";
    public static final String URL_PLAYLISTS = DOMAIN + "/all_playlists";
    public static final String URL_PLAY = DOMAIN + "/track/play";
    public static final String URL_PLAYBACK = DOMAIN + "/track/playback";
    public static final String URL_PAUSE = DOMAIN + "/track/pause";
    public static final String URL_UNPAUSE = DOMAIN + "/track/unpause";
    public static final String URL_SMARTPAUSE = DOMAIN + "/track/smartpause";
    public static final String URL_REWIND = DOMAIN + "/track/rewind";
    public static final String URL_STOP = DOMAIN + "/flush";
    public static final String URL_VOLUME = DOMAIN + "/volume";

    //Query Params
    public static final String Q_PATH = "path=";
    public static final String Q_METADATA = "meta=";
    public static final String Q_SORT = "sort=";
    public static final String Q_LOCAL = "local=";
    public static final String Q_TERMINATE = "t=";
    public static final String Q_POSITION = "pos=";
    public static final String Q_UNPAUSE = "unpause=";

    //Config
    public static final String CHARSET = "UTF-8";

    public static String getPlayUrl(String path, boolean terminate) {
        String encodedPath = encodeString(path);
        return URL_PLAY +
                URL_CHAR_QUESTION +
                Q_PATH + encodedPath +
                URL_CHAR_AMEPERSAND +
                Q_TERMINATE + String.valueOf(terminate);
    }

    public static String getRewindUrl(int seconds, boolean unpause) {
        return URL_REWIND +
                URL_CHAR_QUESTION +
                Q_POSITION + String.valueOf(seconds) +
                URL_CHAR_AMEPERSAND +
                Q_UNPAUSE + String.valueOf(unpause);
    }

    public static String getPlaybackUrl() {
        return URL_PLAYBACK;
    }

    public static String getPauseUrl() {
        return URL_PAUSE;
    }

    public static String getUnpauseUrl() {
        return URL_UNPAUSE;
    }

    public static String getSmartpauseUrl() {
        return URL_SMARTPAUSE;
    }

    public static String getDataUrl(String path, boolean withMetadata, int sort) {
        String encodedPath = encodeString(path);
        return URL_DATA +
                URL_CHAR_QUESTION +
                Q_PATH + encodedPath +
                URL_CHAR_AMEPERSAND +
                Q_METADATA + String.valueOf(withMetadata) +
                URL_CHAR_AMEPERSAND +
                Q_SORT + String.valueOf(sort);
    }

    public static String getFileUrl(String localPath) {
        String encodedPath = encodeString(localPath);
        return URL_COVER +
                URL_CHAR_QUESTION +
                Q_PATH + encodedPath;
    }

    public static String getPlaylistsUrl(String localPath, boolean local, int sort) {
        String encodedPath = encodeString(localPath);
        return URL_PLAYLISTS +
                URL_CHAR_QUESTION +
                Q_PATH + encodedPath +
                URL_CHAR_AMEPERSAND +
                Q_LOCAL + String.valueOf(local) +
                URL_CHAR_AMEPERSAND +
                Q_SORT + String.valueOf(sort);
    }

    public static String getFlushUrl() {
        return URL_STOP;
    }

    public static String getVolumeUrl() {
        return URL_VOLUME;
    }

    public static String encodeString(String path) {
        String encodedPath = path;
        try {
            encodedPath = URLEncoder.encode(path, CHARSET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodedPath;
    }
}
