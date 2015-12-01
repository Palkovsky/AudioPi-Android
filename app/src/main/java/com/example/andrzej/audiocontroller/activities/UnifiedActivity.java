package com.example.andrzej.audiocontroller.activities;

import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.example.andrzej.audiocontroller.MyApplication;
import com.example.andrzej.audiocontroller.interfaces.MediaCallback;
import com.example.andrzej.audiocontroller.utils.Converter;
import com.example.andrzej.audiocontroller.utils.PlaybackUtils;
import com.example.andrzej.audiocontroller.utils.SettingsContentObserver;

/*
    Activity that unifies common functionality for all of them
 */
public abstract class UnifiedActivity extends AppCompatActivity implements MediaCallback {
    private SettingsContentObserver mSettingsContentObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Volume buttons observer
        mSettingsContentObserver = new SettingsContentObserver(this, new Handler(), new SettingsContentObserver.VolumeCallback() {
            @Override
            public void onVolumeChange(int volume) {
                MyApplication.volumeManager.setVolume(Converter.androidVolumeToStandard(volume));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.streamManager.registerMediaListener(this);
        getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, mSettingsContentObserver);

        if (PlaybackUtils.useMediaVolumeStream())
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
        else
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getContentResolver().unregisterContentObserver(mSettingsContentObserver);
    }

    @Override
    public void onMediaStart() {
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onMediaRewind(float position) {}

    @Override
    public void onMediaPause() {
        setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
    }

    @Override
    public void onMediaUnpause() {
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onMediaStop() {
        setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
    }

    @Override
    public void onMediaUpdate() {

    }
}
