package com.example.andrzej.audiocontroller.utils.network;


import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.andrzej.audiocontroller.MyApplication;
import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.config.Endpoints;
import com.example.andrzej.audiocontroller.models.ExploreItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Downloader {


    public static void downloadFile(final Context context, final ExploreItem item) {

        try {

            DownloadManager mgr = (DownloadManager) context.getSystemService(MyApplication.DOWNLOAD_SERVICE);

            String uRl = Endpoints.getFileUrl(item.getPath());
            String caption = item.getFormattedName();

            Uri downloadUri = Uri.parse(uRl);
            DownloadManager.Request request = new DownloadManager.Request(
                    downloadUri);


            if (caption == null || caption.trim().length() <= 0)
                caption = "";

            String[] extensions = {".mp3", ".flac", ".ogg", ".wav", ".m4a"};
            String filename = null;

            for (String extension : extensions) {
                if (item.getPath().contains(extension)) {

                    int index_end = item.getPath().indexOf(extension) + extension.length();
                    String temp = item.getPath().substring(0, index_end);
                    int index_beg = temp.lastIndexOf("/") + 1;

                    filename = item.getPath().substring(index_beg, index_end);
                    break;
                }
            }

            if (filename != null) {
                request.setAllowedNetworkTypes(
                        DownloadManager.Request.NETWORK_WIFI
                                | DownloadManager.Request.NETWORK_MOBILE)
                        .setAllowedOverRoaming(true).setTitle(context.getString(R.string.download_in_progress))
                        .setDescription(caption)
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, filename);


                mgr.enqueue(request);

                Toast.makeText(context, context.getResources().getString(R.string.download_beg), Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(context, context.getResources().getString(R.string.download_error), Toast.LENGTH_SHORT).show();

        } catch (IllegalStateException e) {
            Toast.makeText(context, context.getResources().getString(R.string.download_error), Toast.LENGTH_SHORT).show();
        }


    }
}
