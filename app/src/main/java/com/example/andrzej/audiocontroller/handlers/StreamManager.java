package com.example.andrzej.audiocontroller.handlers;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.interfaces.MediaCallback;
import com.example.andrzej.audiocontroller.interfaces.StreamListener;
import com.example.andrzej.audiocontroller.models.Playlist;
import com.example.andrzej.audiocontroller.models.Track;
import com.example.andrzej.audiocontroller.services.StreamService;
import com.example.andrzej.audiocontroller.services.ServiceManager;
import com.example.andrzej.audiocontroller.utils.network.Network;

import org.json.JSONException;
import org.json.JSONObject;

public class StreamManager implements StreamListener {

    private Track currentTrack;
    private Playlist currentPlaylist;
    private StreamRequester streamRequester;
    private Context context;
    private ServiceManager serviceManager;
    private MediaCallback mediaCallback;

    public StreamManager(final Context context) {
        this.context = context;
        streamRequester = new StreamRequester();
        streamRequester.registerStreamListener(this);

        serviceManager = new ServiceManager(context, StreamService.class, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(context, "Koniec", Toast.LENGTH_SHORT).show();
                serviceManager.stop();
                handleTrackEnd();
            }
        });
    }

    @Override
    public void onStreamStart(Track track, JSONObject response) {
        currentTrack.setPlaying(true);
        currentTrack.setPaused(false);
        currentTrack.setMilliPosSecs(0);
        try {
            float total = response.getInt("total");
            currentTrack.setMilliTotalSecs(total);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        serviceManager.stop();
        serviceManager.start();

        if(mediaCallback != null)
            mediaCallback.onMediaStart();
    }

    @Override
    public void onStreamStop(JSONObject response) {
        currentPlaylist = null;
        currentTrack = null;
        serviceManager.stop();
        if(mediaCallback != null)
            mediaCallback.onMediaStop();
    }

    @Override
    public void onStreamPause(JSONObject response) {
        currentTrack.setPaused(true);
        serviceManager.stop();
        if(mediaCallback != null)
            mediaCallback.onMediaPause();
    }

    @Override
    public void onStreamUnpause(JSONObject response) {
        currentTrack.setPaused(false);
        serviceManager.start();
        if(mediaCallback != null)
            mediaCallback.onMediaUnpause();
    }

    @Override
    public void onStreamRewind(JSONObject response) {
        try {
            float pos = response.getInt("newPosition");
            currentTrack.setMilliPosSecs(pos);
            if(mediaCallback != null)
                mediaCallback.onMediaRewind(pos);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onQueryError(int type, VolleyError error) {
        if (Network.isNetworkAvailable(context))
            Toast.makeText(context, R.string.server_error, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, R.string.no_internet_error, Toast.LENGTH_SHORT).show();
    }

    private void handleTrackEnd(){
        //This is the place to specify different reproduction methods, like:
        //repeat, shuffle, end etc.
        if(currentPlaylist != null && currentPlaylist.canGoNext())
            nextTrack();
        else {
            currentPlaylist = null;
            currentTrack = null;
        }
        if(mediaCallback != null)
            mediaCallback.onMediaStop();
    }


    public void start(boolean terminate) {
        streamRequester.startStream(currentTrack, terminate);
    }

    public void start(Track track, boolean terminate) {
        streamRequester.startStream(track, terminate);
    }

    public void smartpause() {
        streamRequester.smartpauseStream();
    }

    public void pause() {
        streamRequester.pauseStream();
    }

    public void unpause() {
        streamRequester.unpauseStream();
    }

    public void flush() {
        streamRequester.stopStream();
    }

    public void rewind(int seconds, boolean unpause) {
        streamRequester.rewindStream(seconds, unpause);
    }

    public void nextTrack() {
        if (currentPlaylist != null && currentPlaylist.canGoNext()) {
            currentPlaylist.next();
            currentTrack = currentPlaylist.getTracks().get(currentPlaylist.position());
            start(true);
        }
    }

    public void prevTrack() {
        if (currentPlaylist != null && currentPlaylist.canGoPrev()) {
            currentPlaylist.prev();
            currentTrack = currentPlaylist.getTracks().get(currentPlaylist.position());
            start(true);
        }
    }

    public void setPosition(int position) {
        if (currentPlaylist != null) {
            currentPlaylist.setPosition(position);
            currentTrack = currentPlaylist.getTracks().get(currentPlaylist.position());
            start(true);
        }
    }

    //Getters & Setters
    public Track getCurrentTrack() {
        return currentTrack;
    }

    public void setCurrentTrack(Track currentTrack) {
        this.currentTrack = currentTrack;
        this.currentPlaylist = null;
    }

    public Playlist getCurrentPlaylist() {
        return currentPlaylist;
    }

    public void setCurrentPlaylist(Playlist currentPlaylist) {
        this.currentPlaylist = currentPlaylist;
    }

    public void setCurrentPlaylist(Playlist currentPlaylist, int position) {
        this.currentPlaylist = currentPlaylist;
        this.currentPlaylist.setPosition(position);
        this.currentTrack = currentPlaylist.getTracks().get(position);
    }

    public void registerMediaListener(MediaCallback mediaCallback) {
        this.mediaCallback = mediaCallback;
    }
}
