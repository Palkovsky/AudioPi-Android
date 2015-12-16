package com.example.andrzej.audiocontroller.handlers;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.andrzej.audiocontroller.config.Endpoints;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;

public class FileManager implements FileRequester.FileRequesterListener {

    private Context context;
    private FileRequester fileRequester;

    //Listener
    private NewCatalogListener newCatalogListener;
    private DeleteFileListener deleteFileListener;
    private UploadListener uploadListener;

    private final OkHttpClient client = new OkHttpClient();


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

    public void uploadFile(String path, File file, UploadListener uploadListener) {
        this.uploadListener = uploadListener;
        if(file.isFile()) {
            this.uploadListener.onStart();
            FileUploader fileUploader = new FileUploader(path, file);
            fileUploader.execute();
        }
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

    private class  FileUploader extends AsyncTask<Void, Void, Void>{

        private String path;
        private File file;
        private Response response;

        public FileUploader(String path, File file) {
            this.path = path;
            this.file = file;
        }

        @Override
        protected Void doInBackground(Void... params) {
            RequestBody fileBody = RequestBody.create(MediaType.parse("audio/mpeg3"), file);
            RequestBody formBody = new MultipartBuilder()
                    .type(MultipartBuilder.FORM)
                    .addFormDataPart("path", path)
                    .addFormDataPart("file", file.getName(), fileBody)
                    .build();
            Request request = new Request.Builder()
                    .url(Endpoints.getUrlUpload())
                    .post(formBody)
                    .build();


            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (response != null && !response.isSuccessful()) {
                if (uploadListener != null)
                    uploadListener.onError(response.code());
            } else {
                if (uploadListener != null)
                    uploadListener.onFinish();
            }
            uploadListener = null;
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

    public interface UploadListener {
        void onStart();

        void onFinish();

        void onError(int errorCode);
    }
}
