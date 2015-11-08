package com.example.andrzej.audiocontroller.config;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Endpoints {
    public static final String DOMAIN = "http://192.168.1.103:5000";

    public static final String URL_CHAR_QUESTION = "?";
    public static final String URL_CHAR_AMEPERSAND = "&";

    public static final String URL_DATA = DOMAIN + "/data";
    public static final String URL_COVER = DOMAIN + "/cover";

    //Query Params
    public static final String Q_PATH = "path=";
    public static final String Q_METADATA = "meta=";

    //Config
    public static final String CHARSET = "UTF-8";

    public static String getDataUrl(String path, boolean withMetadata) {
        String encodedPath = encodeString(path);
        return URL_DATA +
                URL_CHAR_QUESTION +
                Q_PATH + encodedPath +
                URL_CHAR_AMEPERSAND +
                Q_METADATA + String.valueOf(withMetadata);
    }

    public static String getCoverUrl(String localPath) {
        String encodedPath = encodeString(localPath);
        return URL_COVER +
                URL_CHAR_QUESTION +
                Q_PATH + encodedPath;
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