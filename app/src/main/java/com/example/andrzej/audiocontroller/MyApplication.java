package com.example.andrzej.audiocontroller;

import android.app.Application;
import android.content.Context;

import com.example.andrzej.audiocontroller.handlers.StreamManager;


public class MyApplication extends Application {

    private static Context context;
    public static StreamManager streamManager;

    @Override
    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
        streamManager = new StreamManager(context);
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
