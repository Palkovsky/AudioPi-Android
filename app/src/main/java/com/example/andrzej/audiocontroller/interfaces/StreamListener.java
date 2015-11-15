package com.example.andrzej.audiocontroller.interfaces;


import com.android.volley.VolleyError;
import com.example.andrzej.audiocontroller.models.Track;

import org.json.JSONObject;

public interface StreamListener {
    void onStreamStart(Track track, JSONObject response);
    void onStreamStop(JSONObject response);
    void onStreamPause(JSONObject response);
    void onStreamUnpause(JSONObject response);
    void onStreamRewind(JSONObject response);
    void onQueryError(int type, VolleyError error);
}
