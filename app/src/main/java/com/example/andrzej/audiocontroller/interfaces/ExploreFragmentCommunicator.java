package com.example.andrzej.audiocontroller.interfaces;


import org.json.JSONObject;

public interface ExploreFragmentCommunicator {
    void onQueryStart(String url);
    void onQuerySuccess(String url, JSONObject response);
    void onQueryError(String url, int code);
}
