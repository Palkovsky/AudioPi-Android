package com.example.andrzej.audiocontroller.interfaces;


public interface MediaCallback {
    void onMediaStart();
    void onMediaRewind(float position);
    void onMediaPause();
    void onMediaUnpause();
    void onMediaStop();
}
