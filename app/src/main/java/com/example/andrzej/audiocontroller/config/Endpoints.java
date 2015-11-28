package com.example.andrzej.audiocontroller.config;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Endpoints {
    public static final String URL_HTTP = "http://";
    public static String URL_PORT = "";
    public static String IP = "";

    public static final String URL_CHAR_QUESTION = "?";
    public static final String URL_CHAR_AMEPERSAND = "&";

    public static String URL_DATA = IP + "/data";
    public static String URL_COVER = IP + "/file";
    public static String URL_PLAYLISTS = IP + "/all_playlists";
    public static String URL_PLAY = IP + "/track/play";
    public static String URL_PLAYBACK = IP + "/track/playback";
    public static String URL_PAUSE = IP + "/track/pause";
    public static String URL_UNPAUSE = IP + "/track/unpause";
    public static String URL_SMARTPAUSE = IP + "/track/smartpause";
    public static String URL_REWIND = IP + "/track/rewind";
    public static String URL_STOP = IP + "/flush";
    public static String URL_VOLUME = IP + "/volume";
    public static String URL_ALIVE = IP + "/track/alive";

    //Query Params
    public static final String Q_PATH = "path=";
    public static final String Q_METADATA = "meta=";
    public static final String Q_SORT = "sort=";
    public static final String Q_LOCAL = "local=";
    public static final String Q_TERMINATE = "t=";
    public static final String Q_POSITION = "pos=";
    public static final String Q_UNPAUSE = "unpause=";
    public static final String Q_VALUE = "value=";

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

    public static String getAliveUrl() {
        return URL_ALIVE;
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

    //GET
    public static String getVolumeUrl() {
        return URL_VOLUME;
    }

    //POST
    public static String getVolumeUrl(int volume) {
        return URL_VOLUME +
                URL_CHAR_QUESTION +
                Q_VALUE + String.valueOf(volume);
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

    public static void reInit(String newDomain, String port) {
        URL_PORT = ":" + port;
        IP = URL_HTTP + newDomain + URL_PORT;
        URL_DATA = IP + "/data";
        URL_COVER = IP + "/file";
        URL_PLAYLISTS = IP + "/all_playlists";
        URL_PLAY = IP + "/track/play";
        URL_PLAYBACK = IP + "/track/playback";
        URL_PAUSE = IP + "/track/pause";
        URL_UNPAUSE = IP + "/track/unpause";
        URL_SMARTPAUSE = IP + "/track/smartpause";
        URL_REWIND = IP + "/track/rewind";
        URL_STOP = IP + "/flush";
        URL_VOLUME = IP + "/volume";
        URL_ALIVE = IP + "/track/alive";
    }

    public static String getTestUrl(String ip, String port) {
        return URL_HTTP + ip + ":" + port + "/test";
    }
}
