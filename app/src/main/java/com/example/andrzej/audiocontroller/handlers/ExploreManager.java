package com.example.andrzej.audiocontroller.handlers;

import com.example.andrzej.audiocontroller.interfaces.ExploreListener;
import com.example.andrzej.audiocontroller.models.Directory;
import com.example.andrzej.audiocontroller.models.ExploreItem;

import java.util.List;
import java.util.Stack;


public class ExploreManager {


    private String defaultPath;
    private Stack<Directory> history = new Stack<>();

    private ExploreListener exploreListener;

    public ExploreManager(String defaultPath) {
        history.push(new Directory(defaultPath));
        this.defaultPath = defaultPath;
    }

    public void goTo(String path) {

        String oldPath = history.peek().getPath();
        history.push(new Directory(path));

        if (exploreListener != null)
            exploreListener.onDirectoryDown(oldPath, path);

    }

    public void goUp() {
        Directory oldDir = history.pop();
        if (exploreListener != null)
            exploreListener.onDirectoryUp(oldDir.getPath(), history.peek().getPath());
    }


    public void goToRoot() {
        String oldPath = history.peek().getPath();
        history.clear();
        history.push(new Directory(defaultPath));
        if (exploreListener != null)
            exploreListener.onDirectoryUp(oldPath, defaultPath);
    }

    public boolean canGoUp() {
        return history.size() >= 2;
    }

    public int getDepth() {
        return history.size();
    }

    public String currentPath() {
        return history.peek().getPath();
    }

    public Directory currentDirectory() {
        return history.peek();
    }

    public void setExploreListener(ExploreListener exploreListener) {
        this.exploreListener = exploreListener;
    }
}
