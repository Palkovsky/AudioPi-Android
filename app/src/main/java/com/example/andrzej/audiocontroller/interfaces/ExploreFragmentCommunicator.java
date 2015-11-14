package com.example.andrzej.audiocontroller.interfaces;


import org.json.JSONObject;

public interface ExploreFragmentCommunicator {
    void onQueryStart(String url, String path);
    void onQuerySuccess(String url, String path, JSONObject response);
    void onQueryError(String url, int code);
}
