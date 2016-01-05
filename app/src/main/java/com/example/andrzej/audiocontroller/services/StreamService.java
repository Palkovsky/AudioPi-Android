package com.example.andrzej.audiocontroller.services;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.andrzej.audiocontroller.MyApplication;
import com.example.andrzej.audiocontroller.config.Endpoints;
import com.example.andrzej.audiocontroller.utils.network.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

public class StreamService extends AbstractService {

    private static final String REQUEST_TAG = "REQUEST_ALIVE_TAG";
    public static final int MSG_VALUE = 1;
    public static final int MSG_POS_UPDATE = 2;
    public static final int SERVER_ERROR = 3;
    public static final int MSG_OTHER_TRACK = 4;
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

                        //This is used, cuz sometimes /track/playback don't respond properly
                        //this assures that playback is actually dead, cuz it forces it to wait 1 sec more
                        //and in that time everyting on server should be fine
                        private int retryCount = 0;

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (MyApplication.streamManager != null && MyApplication.streamManager.getCurrentTrack() != null) {
                                    int code = response.getInt("code");
                                    Log.e("andrzej", "code: " + code);
                                    if (code == 1015) {
                                        retryCount++;
                                        Log.e("andrzej", "POS: " + MyApplication.streamManager.getCurrentTrack().getMilliPosSecs());
                                        if ((retryCount > 7 && MyApplication.streamManager.getCurrentTrack().sinceLastPause() <= 3000)
                                                || ( MyApplication.streamManager.getCurrentTrack().sinceLastPause() > 3000 &&
                                                MyApplication.streamManager.getCurrentTrack().getMilliPosSecs() > 3000)) {
                                            send(Message.obtain(null, MSG_VALUE, -1, 0));
                                            retryCount = 0;
                                        }
                                    } else {
                                        retryCount = 0;
                                        int curTime = response.getJSONObject("playback").getJSONObject("position").getInt("millis");
                                        send(Message.obtain(null, MSG_POS_UPDATE, curTime, 0));
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Disabled because, it was giving some problems. Probably better to keep it this way
                            //send(Message.obtain(null, SERVER_ERROR, -1, 0));
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
