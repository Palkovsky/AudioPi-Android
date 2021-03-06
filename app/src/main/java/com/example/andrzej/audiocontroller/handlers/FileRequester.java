package com.example.andrzej.audiocontroller.handlers;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.andrzej.audiocontroller.config.Codes;
import com.example.andrzej.audiocontroller.config.Defaults;
import com.example.andrzej.audiocontroller.config.Endpoints;
import com.example.andrzej.audiocontroller.utils.network.VolleySingleton;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class FileRequester {

    public static final String TAG = "FILE_REQUESTER";

    //Query Types
    public static final int UPLOAD_QUERY = 1;
    public static final int NEW_FOLDER_QUERY = 2;
    public static final int DELETE_QUERY = 3;

    private RequestQueue requestQueue;
    private FileRequesterListener fileRequesterListener;




    public FileRequester(FileRequesterListener fileRequesterListener) {
        this.fileRequesterListener = fileRequesterListener;
        requestQueue = VolleySingleton.getsInstance().getRequestQueue();
    }

    public void createFolder(String path, String name) {
        String queryUrl = Endpoints.getNewCatalogUrl(path, name);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, queryUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int code = response.getInt("code");
                    if (code != Codes.SUCCESFULL)
                        fileRequesterListener.onQueryError(code, NEW_FOLDER_QUERY);
                    else
                        fileRequesterListener.onNewCatalogCreated();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                fileRequesterListener.onQueryError(Codes.SUCCESFULL, NEW_FOLDER_QUERY);
            }
        });

        request.setTag(TAG);
        requestQueue.add(request);
    }

    public void deleteFile(String path){
        String queryUrl = Endpoints.getDeleteFileUrl(path);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, queryUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int code = response.getInt("code");
                    if (code != Codes.SUCCESFULL)
                        fileRequesterListener.onQueryError(code, DELETE_QUERY);
                    else
                        fileRequesterListener.onFileDeleted();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                fileRequesterListener.onQueryError(Codes.SUCCESFULL, DELETE_QUERY);
            }
        });

        request.setTag(TAG);
        request.setRetryPolicy(new DefaultRetryPolicy(
                Defaults.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(request);
    }

    public void uploadFile(String path, File file){

    }

    public interface FileRequesterListener {
        void onNewCatalogCreated();

        void onFileDeleted();

        void onFileUploaded();

        void onQueryError(int errorCode, int queryType);
    }
}
