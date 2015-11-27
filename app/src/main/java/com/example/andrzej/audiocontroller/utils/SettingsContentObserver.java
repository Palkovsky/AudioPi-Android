package com.example.andrzej.audiocontroller.utils;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class SettingsContentObserver extends ContentObserver {

    int previousVolume;
    Context context;
    VolumeCallback volumeCallback;

    public SettingsContentObserver(Context c, Handler handler, VolumeCallback volumeCallback) {
        super(handler);
        context = c;
        this.volumeCallback = volumeCallback;

        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        previousVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);

        int delta = previousVolume - currentVolume;

        volumeCallback.onVolumeChange(currentVolume);
        previousVolume = currentVolume;

    }

    public interface VolumeCallback {
        void onVolumeChange(int volume);
    }
}