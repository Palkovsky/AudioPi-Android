package com.example.andrzej.audiocontroller.handlers;



import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.andrzej.audiocontroller.config.Endpoints;
import com.example.andrzej.audiocontroller.utils.network.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;
public class VolumeRequester {

    public static final String TAG = "VOLUME_REQUESTER";

    private RequestQueue requestQueue;
    private VolumeRequestCallback volumeRequestCallback;

    public VolumeRequester(VolumeRequestCallback volumeRequestCallback) {
        this.volumeRequestCallback = volumeRequestCallback;
        requestQueue = VolleySingleton.getsInstance().getRequestQueue();
    }

    public void setVolume(final int volume) {
        String queryUrl = Endpoints.getVolumeUrl(volume);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, queryUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    volumeRequestCallback.onVolumeChange(response.getJSONObject("config").getInt("volume"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    volumeRequestCallback.onQueryError();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volumeRequestCallback.onQueryError();
            }
        });

        request.setTag(TAG);
        requestQueue.add(request);
    }

    public void getVolume() {

    }

    public interface VolumeRequestCallback {
        void onVolumeChange(int volume);

        void onQueryError();
    }
}
