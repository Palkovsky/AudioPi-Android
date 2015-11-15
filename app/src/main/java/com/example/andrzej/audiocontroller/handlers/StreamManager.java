package com.example.andrzej.audiocontroller.handlers;


import android.content.Context;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.interfaces.StreamListener;
import com.example.andrzej.audiocontroller.models.Playlist;
import com.example.andrzej.audiocontroller.models.Track;
import com.example.andrzej.audiocontroller.utils.network.Network;

import org.json.JSONObject;

public class StreamManager implements StreamListener {

    private Track currentTrack;
    private Playlist currentPlaylist;
    private StreamRequester streamRequester;
    private Context context;

    public StreamManager(Context context) {
        this.context = context;
        streamRequester = new StreamRequester();
        streamRequester.registerStreamListener(this);
    }

    @Override
    public void onStreamStart(Track track, JSONObject response) {}

    @Override
    public void onStreamStop(JSONObject response) {

    }

    @Override
    public void onStreamPause(JSONObject response) {

    }

    @Override
    public void onStreamUnpause(JSONObject response) {

    }

    @Override
    public void onStreamRewind(JSONObject response) {

    }

    @Override
    public void onQueryError(int type, VolleyError error) {
        if(Network.isNetworkAvailable(context))
            Toast.makeText(context, R.string.server_error, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, R.string.no_internet_error, Toast.LENGTH_SHORT).show();
    }


    public void start(boolean terminate){
        streamRequester.startStream(currentTrack, terminate);
    }

    public void start(Track track, boolean terminate){
        streamRequester.startStream(track, terminate);
    }

    public void smartpause(){
        streamRequester.smartpauseStream();
    }

    public void pause(){
        streamRequester.pauseStream();
    }

    public void unpause(){
        streamRequester.unpauseStream();
    }

    public void flush(){
        streamRequester.stopStream();
    }

    public void rewind(int seconds, boolean unpause){
        streamRequester.rewindStream(seconds, unpause);
    }

    public void nextTrack(){
        if(currentPlaylist != null && currentPlaylist.canGoNext()){
            currentPlaylist.next();
            currentTrack = currentPlaylist.getTracks().get(currentPlaylist.position());
            start(true);
        }
    }

    public void prevTrack(){
        if(currentPlaylist != null && currentPlaylist.canGoPrev()){
            currentPlaylist.prev();
            currentTrack = currentPlaylist.getTracks().get(currentPlaylist.position());
            start(true);
        }
    }

    public void setPosition(int position){
        if(currentPlaylist != null){
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
    }

    public Playlist getCurrentPlaylist() {
        return currentPlaylist;
    }

    public void setCurrentPlaylist(Playlist currentPlaylist) {
        this.currentPlaylist = currentPlaylist;
    }

    public void setCurrentPlaylist(Playlist currentPlaylist, int position) {
        this.currentPlaylist = currentPlaylist;
        this.currentTrack = currentPlaylist.getTracks().get(position);
    }
}
