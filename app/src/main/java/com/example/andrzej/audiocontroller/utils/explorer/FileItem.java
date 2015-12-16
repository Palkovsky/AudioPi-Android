package com.example.andrzej.audiocontroller.utils.explorer;


import java.io.File;

public class FileItem {
    private String name;
    private File file;

    public FileItem() {
    }

    public FileItem(String name, File file) {
        this.name = name;
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
