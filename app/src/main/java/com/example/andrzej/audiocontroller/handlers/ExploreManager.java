package com.example.andrzej.audiocontroller.handlers;

import com.example.andrzej.audiocontroller.interfaces.ExploreListener;

import java.util.Stack;


public class ExploreManager {

    private String defaultPath;
    private Stack<String> history = new Stack<>();

    private ExploreListener exploreListener;

    public ExploreManager(String defaultPath) {
        history.push(defaultPath);
        this.defaultPath = defaultPath;
    }

    public void goTo(String path) {

        String oldPath = history.peek();
        history.push(path);

        if (exploreListener != null)
            exploreListener.onDirectoryDown(oldPath, path);

    }

    public void goUp() {
        String oldPath = history.pop();
        if (exploreListener != null)
            exploreListener.onDirectoryUp(oldPath, history.peek());
    }



    public void goToRoot() {
        history.clear();
        history.push(defaultPath);
        if (exploreListener != null)
            exploreListener.onDirectoryUp(history.peek(), defaultPath);
    }

    public boolean canGoUp() {
        return history.size() >= 2;
    }

    public String currentPath() {
        return history.peek();
    }

    public void setExploreListener(ExploreListener exploreListener) {
        this.exploreListener = exploreListener;
    }
}
