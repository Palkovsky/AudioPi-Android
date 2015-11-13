package com.example.andrzej.audiocontroller.models;


import com.example.andrzej.audiocontroller.config.Endpoints;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class ExploreItem {
    private String path;
    private String name;
    private String type;
    private Metadata metadata = new Metadata();
    private boolean directory;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getFormattedName() {
        return FilenameUtils.removeExtension(name);
    }

    public void setName(String name) {
        this.name = name;

        int i = name.lastIndexOf('.');
        if (i > 0)
            setType(name.substring(i + 1));
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public void setJSONMetadata(JSONObject json) {
        try {
            metadata.setArtist(json.getString("artist"));
            metadata.setGenre(json.getString("genre"));
            metadata.setAlbum(json.getString("album"));
            metadata.setLength(json.getInt("length"));
            metadata.setFilesize(json.getDouble("filesize"));
            String coverUrl = json.getString("cover");
            if (coverUrl != null && !coverUrl.equals("null"))
                metadata.setCoverUrl(Endpoints.getFileUrl(coverUrl));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
