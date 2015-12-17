package com.example.andrzej.audiocontroller.utils.explorer;


import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.Arrays;

public class FileItem {

    public static final int DIRECTORY = 0;
    public static final int TEXT = 1;
    public static final int AUDIO = 2;
    public static final int VIDEO = 3;
    public static final int GRAPHIC = 4;
    public static final int ARCHIVE = 5;

    private static final String[] AUDIO_EXT = {"mp3", "flac", "m4a", "ogg", "wav"};
    private static final String[] VIDEO_EXT = {"mp4", "3gp", "wmv", "avi"};
    private static final String[] TEXT_EXT = {"doc", "docx", "py", "java", "rb", "html", "txt"};
    private static final String[] GRAPHIC_EXT = {"jpg", "png", "gif", "bnp"};
    private static final String[] ARCHIVE_EXT = {"rar", "zip", "gz", "tar"};

    private String name;
    private String extension;
    private boolean selected = false;
    private int type;
    private File file;

    public FileItem(String name, File file) {
        this.name = name;
        this.file = file;
        this.extension = FilenameUtils.getExtension(file.getName());
        findType();
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
        this.extension = FilenameUtils.getExtension(file.getName());
        findType();
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getType() {
        return type;
    }

    private void findType() {
        if (file.isDirectory())
            type = DIRECTORY;
        else {
            if(contains(getExtension(), TEXT_EXT))
                type = TEXT;
            else if(contains(getExtension(), VIDEO_EXT))
                type = VIDEO;
            else if(contains(getExtension(), AUDIO_EXT))
                type = AUDIO;
            else if(contains(getExtension(), GRAPHIC_EXT))
                type = GRAPHIC;
            else if(contains(getExtension(), ARCHIVE_EXT))
                type = ARCHIVE;
            else
                type = TEXT;
        }
    }

    private boolean contains(String ext, String[] extensions){
        return Arrays.asList(extensions).contains(ext);
    }

    public String getExtension() {
        return extension;
    }
}
