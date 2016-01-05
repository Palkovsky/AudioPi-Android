package com.example.andrzej.audiocontroller.handlers;


import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.Log;

import com.example.andrzej.audiocontroller.services.MediaPlayerService;
import com.example.andrzej.audiocontroller.services.ServiceManager;

public class MediaSessionManager {

    public static final int MSG_SEND = 1;
    public static final int MSG_RECEIVE = 2;
    public static final int MSH_PAUSED = 3;
    public static final int MSG_UNPAUSED = 4;

    public static final int MSG_PLAY = 10;
    public static final int MSG_PAUSE = 11;
    public static final int MSG_NEXT = 12;
    public static final int MSG_PREV = 13;
    public static final int MSG_FLUSH = 14;
    public static final int MSG_NOTIFICATION = 32;

    private ServiceManager serviceManager;
    private Context context;

    private StreamManager.MediaSessionCallback mediaSessionListener;

    public MediaSessionManager(Context context, final StreamManager.MediaSessionCallback mediaSessionCallback) {
        this.context = context;
        this.mediaSessionListener = mediaSessionCallback;
        this.serviceManager = new ServiceManager(context, MediaPlayerService.class, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case MSG_PLAY:
                        mediaSessionListener.onPlay();
                        break;
                    case MSG_PAUSE:
                        mediaSessionListener.onPause();
                        break;
                    case MSG_NEXT:
                        mediaSessionListener.onNext();
                        break;
                    case MSG_PREV:
                        mediaSessionListener.onPrev();
                        break;
                    case MSG_FLUSH:
                        mediaSessionListener.onFlush();
                        break;
                }
            }
        });
    }

    public void start() {
        if (!serviceManager.isRunning()) {
            Intent intent = new Intent(context, MediaPlayerService.class);
            intent.setAction(MediaPlayerService.ACTION_PLAY);
            serviceManager.start(intent);
            Log.e("andrzej", "started service");
        }
    }

    public void stop(boolean flush) {
        serviceManager.stop();
        if (flush) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(1);
        }
        Log.e("andrzej", "stopped service");
    }



}
