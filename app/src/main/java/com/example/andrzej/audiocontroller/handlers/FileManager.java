package com.example.andrzej.audiocontroller.handlers;


import android.content.Context;

public class FileManager implements FileRequester.FileRequesterListener {

    private Context context;
    private FileRequester fileRequester;

    //Listener
    private NewCatalogListener newCatalogListener;
    private DeleteFileListener deleteFileListener;

    public FileManager(Context context) {
        this.context = context;
        fileRequester = new FileRequester(this);
    }

    public void newCatalog(String path, String name, NewCatalogListener newCatalogListener) {
        fileRequester.createFolder(path, name);
        this.newCatalogListener = newCatalogListener;
        this.newCatalogListener.onQueryStart();
    }

    public void deleteFile(String path, DeleteFileListener deleteFileListener) {
        fileRequester.deleteFile(path);
        this.deleteFileListener = deleteFileListener;
        this.deleteFileListener.onQueryStart();
    }

    @Override
    public void onNewCatalogCreated() {
        if (newCatalogListener != null)
            newCatalogListener.onQueryFinish();
        newCatalogListener = null;
    }

    @Override
    public void onFileDeleted() {
        this.deleteFileListener.onQueryFinish();
    }

    @Override
    public void onFileUploaded() {

    }

    @Override
    public void onQueryError(int errorCode, int queryType) {
        switch (queryType) {
            case FileRequester.NEW_FOLDER_QUERY:
                if (newCatalogListener != null)
                    newCatalogListener.onQueryError(errorCode);
                break;

            case FileRequester.DELETE_QUERY:
                if (deleteFileListener != null)
                    deleteFileListener.onQueryError(errorCode);
                break;
        }
    }

    public interface NewCatalogListener {
        void onQueryStart();

        void onQueryFinish();

        void onQueryError(int errorCode);
    }

    public interface DeleteFileListener {
        void onQueryStart();

        void onQueryFinish();

        void onQueryError(int errorCode);
    }
}
