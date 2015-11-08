package com.example.andrzej.audiocontroller.interfaces;


public interface ExploreListener {
    void onDirectoryUp(String oldPath, String newPath);
    void onDirectoryDown(String oldPath, String newPath);
}
