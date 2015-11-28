package com.example.andrzej.audiocontroller;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;

import com.activeandroid.ActiveAndroid;
import com.example.andrzej.audiocontroller.config.Endpoints;
import com.example.andrzej.audiocontroller.config.PrefKeys;
import com.example.andrzej.audiocontroller.handlers.StreamManager;
import com.example.andrzej.audiocontroller.handlers.VolumeManager;
import com.example.andrzej.audiocontroller.utils.Converter;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;


public class MyApplication extends Application {

    private static Context context;
    public static StreamManager streamManager;
    public static VolumeManager volumeManager;
    private SharedPreferences prefs;
    private BroadcastReceiver volumeReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
        CustomActivityOnCrash.install(this);
        MyApplication.context = getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Endpoints.reInit(prefs.getString(PrefKeys.KEY_IP, ""), prefs.getString(PrefKeys.KEY_PORT, ""));
        streamManager = new StreamManager(context);
        volumeManager = new VolumeManager(context);
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        volumeManager.setVolume(Converter.androidVolumeToStandard(audio.getStreamVolume(AudioManager.STREAM_MUSIC)));
    }



    public static Context getAppContext() {
        return MyApplication.context;
    }
}
