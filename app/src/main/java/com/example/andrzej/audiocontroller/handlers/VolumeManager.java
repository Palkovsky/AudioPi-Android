package com.example.andrzej.audiocontroller.handlers;


import android.content.Context;
import android.widget.Toast;

import com.example.andrzej.audiocontroller.MyApplication;
import com.example.andrzej.audiocontroller.R;

public class VolumeManager implements VolumeRequester.VolumeRequestCallback {

    private Context context;
    private VolumeRequester volumeRequester;
    private int currentVolume = 100;

    public VolumeManager(Context context) {
        this.context = context;
        volumeRequester = new VolumeRequester(this);
    }

    @Override
    public void onVolumeChange(int volume) {
        currentVolume = volume;
    }

    @Override
    public void onQueryError() {
        Toast.makeText(context, R.string.volume_change_error, Toast.LENGTH_SHORT).show();
    }

    public void setVolume(int volume) {
        volumeRequester.setVolume(volume);
    }

    public int getVolume() {
        return currentVolume;
    }
}
