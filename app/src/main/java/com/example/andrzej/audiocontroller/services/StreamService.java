package com.example.andrzej.audiocontroller.services;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.andrzej.audiocontroller.config.Endpoints;
import com.example.andrzej.audiocontroller.utils.network.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

public class StreamService extends AbstractService {

    private static final String REQUEST_TAG = "REQUEST_ALIVE_TAG";
    public static final int MSG_VALUE = 1;
    public static final int MSG_POS_UPDATE = 2;
    public static final int SERVER_ERROR = 3;
    private static final int REFRESH_INTERVAL = 1000;

    final Handler handler = new Handler();
    Runnable r;

    @Override
    public void onStartService() {

        r = new Runnable() {
            @Override
            public void run() {
                try {
                    final RequestQueue requestQueue = VolleySingleton.getsInstance().getRequestQueue();
                    final String queryUrl = Endpoints.getPlaybackUrl();

                    requestQueue.cancelAll(REQUEST_TAG);
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, queryUrl, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                int code = response.getInt("code");
                                if (code == 1015) {
                                    send(Message.obtain(null, MSG_VALUE, -1, 0));
                                } else {
                                    int curTime = response.getJSONObject("playback").getJSONObject("position").getInt("millis");
                                    send(Message.obtain(null, MSG_POS_UPDATE, curTime, 0));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            send(Message.obtain(null, SERVER_ERROR, -1, 0));
                        }
                    });

                    request.setTag(REQUEST_TAG);
                    requestQueue.add(request);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    handler.postDelayed(this, REFRESH_INTERVAL);
                }
            }
        };
        handler.postDelayed(r, REFRESH_INTERVAL);
    }

    @Override
    public void onStopService() {
        if (r != null) {
            handler.removeCallbacks(r);
        }
    }

    @Override
    public void onReceiveMessage(Message msg) {

    }
}
