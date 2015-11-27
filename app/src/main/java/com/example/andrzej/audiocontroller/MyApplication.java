package com.example.andrzej.audiocontroller;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.activeandroid.ActiveAndroid;
import com.example.andrzej.audiocontroller.config.Endpoints;
import com.example.andrzej.audiocontroller.config.PrefKeys;
import com.example.andrzej.audiocontroller.handlers.StreamManager;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;


public class MyApplication extends Application {

    private static Context context;
    public static StreamManager streamManager;
    private SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
        CustomActivityOnCrash.install(this);
        MyApplication.context = getApplicationContext();
        streamManager = new StreamManager(context);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Endpoints.reInit(prefs.getString(PrefKeys.KEY_IP, ""), prefs.getString(PrefKeys.KEY_PORT, ""));
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
