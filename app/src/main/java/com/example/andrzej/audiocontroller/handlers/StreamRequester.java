package com.example.andrzej.audiocontroller.handlers;


import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.andrzej.audiocontroller.config.Endpoints;
import com.example.andrzej.audiocontroller.interfaces.StreamListener;
import com.example.andrzej.audiocontroller.models.Track;
import com.example.andrzej.audiocontroller.utils.network.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

public class StreamRequester {

    public static final String TAG = "STREAM_MANAGER";

    //Error types
    public static final int PLAY_ERROR = 0x01;
    public static final int SMARTPAUSE_ERROR = 0x02;
    public static final int REWIND_ERROR = 0x03;
    public static final int FLUSH_ERROR = 0x04;
    public static final int PAUSE_ERROR = 0x05;
    public static final int UNPAUSE_ERROR = 0x06;

    private RequestQueue requestQueue;
    private StreamListener streamListener;

    public StreamRequester() {
        requestQueue = VolleySingleton.getsInstance().getRequestQueue();
    }

    public void startStream(final Track item, boolean terminate) {
        String queryUrl = Endpoints.getPlayUrl(item.getPath(), terminate);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, queryUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (streamListener != null)
                    streamListener.onStreamStart(item, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (streamListener != null)
                    streamListener.onQueryError(PLAY_ERROR, error);
            }
        });

        request.setTag(TAG + "_START");
        requestQueue.add(request);
    }

    public void stopStream() {
        String queryUrl = Endpoints.getFlushUrl();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, queryUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (streamListener != null)
                    streamListener.onStreamStop(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (streamListener != null)
                    streamListener.onQueryError(FLUSH_ERROR, error);
            }
        });

        request.setTag(TAG + "_FLUSH");
        requestQueue.add(request);
    }

    public void smartpauseStream() {
        String queryUrl = Endpoints.getSmartpauseUrl();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, queryUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject state = response.getJSONObject("state");
                    boolean paused = state.getBoolean("paused");

                    if (paused) {
                        if (streamListener != null)
                            streamListener.onStreamPause(response);
                    } else {
                        if (streamListener != null)
                            streamListener.onStreamUnpause(response);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    if (streamListener != null)
                        streamListener.onQueryError(SMARTPAUSE_ERROR, null);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (streamListener != null)
                    streamListener.onQueryError(SMARTPAUSE_ERROR, error);
            }
        });

        request.setTag(TAG + "_SMARTPAUSE");
        requestQueue.add(request);
    }

    public void rewindStream(int seconds, boolean unpause) {
        String queryUrl = Endpoints.getRewindUrl(seconds, unpause);

        Log.e(null, queryUrl);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, queryUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (streamListener != null)
                    streamListener.onStreamRewind(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (streamListener != null)
                    streamListener.onQueryError(REWIND_ERROR, error);
            }
        });

        request.setTag(TAG + "_REWIND");
        requestQueue.add(request);
    }

    public void pauseStream() {
        String queryUrl = Endpoints.getPauseUrl();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, queryUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (streamListener != null)
                    streamListener.onStreamPause(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (streamListener != null)
                    streamListener.onQueryError(PAUSE_ERROR, error);
            }
        });

        request.setTag(TAG + "_PAUSE");
        requestQueue.add(request);
    }

    public void unpauseStream() {
        String queryUrl = Endpoints.getUnpauseUrl();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, queryUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (streamListener != null)
                    streamListener.onStreamUnpause(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (streamListener != null)
                    streamListener.onQueryError(UNPAUSE_ERROR, error);
            }
        });

        request.setTag(TAG + "_UNPAUSE");
        requestQueue.add(request);
    }


    //Getters & Setters
    public void registerStreamListener(StreamListener streamListener) {
        this.streamListener = streamListener;
    }
}
