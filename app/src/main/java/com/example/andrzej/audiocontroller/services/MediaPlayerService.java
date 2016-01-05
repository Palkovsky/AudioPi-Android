package com.example.andrzej.audiocontroller.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.andrzej.audiocontroller.MyApplication;
import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.handlers.MediaSessionManager;
import com.example.andrzej.audiocontroller.models.Playlist;
import com.example.andrzej.audiocontroller.models.Track;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class MediaPlayerService extends AbstractService implements AudioManager.OnAudioFocusChangeListener {


    private AudioManager m_objMediaSessionManager;
    private MediaSessionCompat m_objMediaSession;
    private MediaControllerCompat m_objMediaController;

    private Playlist playlist;
    private Target target;

    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_REWIND = "action_rewind";
    public static final String ACTION_FAST_FORWARD = "action_fast_foward";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_STOP = "action_stop";
    public static final String LOG_TAG = "MediaService";


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStartService() {
        initMediaSessions();
    }

    @Override
    public void onStopService() {

    }

    @Override
    public void onReceiveMessage(Message msg) {
        switch (msg.what) {
            case MediaSessionManager.MSG_SEND:
                playlist = (Playlist) msg.obj;
                initMediaSessions();
                break;
            case MediaSessionManager.MSG_RECEIVE:
                Intent intent = (Intent) msg.obj;
                handleIntent(intent);
                break;
            case MediaSessionManager.MSH_PAUSED:
                Log.e("andrzej", "paused");
                buildNotification(generateAction(android.R.drawable.ic_media_play, "Play", ACTION_PLAY));
                break;
            case MediaSessionManager.MSG_UNPAUSED:
                Log.e("andrzej", "unpaused");
                buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE));
                break;
        }
    }

    private void handleIntent(Intent intent) {
        if (intent == null || intent.getAction() == null)
            return;

        String action = intent.getAction();

        if (action.equalsIgnoreCase(ACTION_PLAY)) {
            m_objMediaController.getTransportControls().play();
        } else if (action.equalsIgnoreCase(ACTION_PAUSE)) {
            m_objMediaController.getTransportControls().pause();
        } else if (action.equalsIgnoreCase(ACTION_FAST_FORWARD)) {
            m_objMediaController.getTransportControls().fastForward();
        } else if (action.equalsIgnoreCase(ACTION_REWIND)) {
            m_objMediaController.getTransportControls().rewind();
        } else if (action.equalsIgnoreCase(ACTION_PREVIOUS)) {
            m_objMediaController.getTransportControls().skipToPrevious();
        } else if (action.equalsIgnoreCase(ACTION_NEXT)) {
            m_objMediaController.getTransportControls().skipToNext();
        } else if (action.equalsIgnoreCase(ACTION_STOP)) {
            m_objMediaController.getTransportControls().stop();
        }
    }

    private void buildNotification(final android.support.v4.app.NotificationCompat.Action action) {


        if (playlist != null) {
            final Track currentTrack = playlist.currentTrack();

            if (target != null)
                Picasso.with(getBaseContext()).cancelRequest(target);

            target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    buildNotification(currentTrack, action, bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            };

            String queryUrl = currentTrack.getMetadata().getCoverUrl();
            if (queryUrl != null && !queryUrl.trim().equals(""))
                Picasso.with(getApplicationContext()).load(currentTrack.getMetadata().getCoverUrl()).into(target);
            else {
                Bitmap icon = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.ic_music_note_white_48dp);
                buildNotification(currentTrack, action, icon);
            }
        }

    }

    private void buildNotification(Track currentTrack, android.support.v4.app.NotificationCompat.Action action, Bitmap largeImage) {
        final Intent intent = new Intent(getApplicationContext(), MediaPlayerService.class);
        intent.setAction(ACTION_STOP);
        final NotificationCompat.MediaStyle style = new NotificationCompat.MediaStyle();

        String artist = currentTrack.getMetadata().getArtist();
        if (artist == null || artist.equals("") || artist.equals("null"))
            artist = "";

        style.setMediaSession(m_objMediaSession.getSessionToken());
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_music_note_white_36dp)
                .setLargeIcon(largeImage)
                .setContentTitle(currentTrack.getFormattedName())
                .setContentText(artist)
                .setDeleteIntent(pendingIntent)
                .setStyle(style);

        if (playlist.canGoPrev())
            builder.addAction(generateAction(android.R.drawable.ic_media_previous, "Previous", ACTION_PREVIOUS));
        builder.addAction(action);
        if (playlist.canGoNext())
            builder.addAction(generateAction(android.R.drawable.ic_media_next, "Next", ACTION_NEXT));


        //final TransportControls controls = m_objMediaSession.getController().getTransportControls();
        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }


    private android.support.v4.app.NotificationCompat.Action generateAction(int icon, String title, String intentAction) {
        Intent intent = new Intent(getApplicationContext(), MediaPlayerService.class);
        intent.setAction(intentAction);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        return new android.support.v4.app.NotificationCompat.Action.Builder(icon, title, pendingIntent).build();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        playlist = MyApplication.streamManager.getCurrentPlaylist();
        if (m_objMediaSessionManager == null) {
            initMediaSessions();
        }
        handleIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void initMediaSessions() {
        m_objMediaSessionManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = m_objMediaSessionManager.requestAudioFocus(this,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result != AudioManager.AUDIOFOCUS_GAIN) {
            return; //Failed to gain audio focus
        }
        m_objMediaSession = new MediaSessionCompat(getApplicationContext(), "sample session");
        try {
            m_objMediaController = new MediaControllerCompat(getApplicationContext(), m_objMediaSession.getSessionToken());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        m_objMediaSession.setActive(true);
        m_objMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        m_objMediaSession.setCallback(new android.support.v4.media.session.MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
                Log.e(LOG_TAG, "onPlay");
                //send(Message.obtain(null, MediaSessionManager.MSG_PLAY, MediaSessionManager.MSG_PLAY, -1));

                Track currentTrack = playlist.currentTrack();

                buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE));
                if (MyApplication.streamManager.getCurrentPlaylist() != null)
                    MyApplication.streamManager.unpause();
            }

            @Override
            public void onPause() {
                super.onPause();
                Log.e(LOG_TAG, "onPause");
                //send(Message.obtain(null, MediaSessionManager.MSG_PAUSE, MediaSessionManager.MSG_PAUSE, -1));

                buildNotification(generateAction(android.R.drawable.ic_media_play, "Play", ACTION_PLAY));
                if (MyApplication.streamManager.getCurrentPlaylist() != null)
                    MyApplication.streamManager.pause();
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                //send(Message.obtain(null, MediaSessionManager.MSG_NEXT, MediaSessionManager.MSG_NEXT, -1));

                buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE));
                if (MyApplication.streamManager.getCurrentPlaylist() != null)
                    MyApplication.streamManager.nextTrack();
                if (target != null)
                    Picasso.with(getApplicationContext()).cancelRequest(target);
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                Log.e(LOG_TAG, "onSkipToPrevious");
                //send(Message.obtain(null, MediaSessionManager.MSG_PREV, MediaSessionManager.MSG_PREV, -1));

                buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE));
                if (MyApplication.streamManager.getCurrentPlaylist() != null)
                    MyApplication.streamManager.prevTrack();
                if (target != null)
                    Picasso.with(getApplicationContext()).cancelRequest(target);
            }

            @Override
            public void onStop() {
                super.onStop();
                Log.e(LOG_TAG, "onStop");
                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(1);
                Intent intent = new Intent(getApplicationContext(), MediaPlayerService.class);
                stopService(intent);
                MyApplication.streamManager.flush();
                MyApplication.streamManager.setCurrentPlaylist(null);
                MyApplication.streamManager.setCurrentTrack(null);
                MyApplication.streamManager.stopService(true);
                MyApplication.streamManager.release();
                if (target != null)
                    Picasso.with(getApplicationContext()).cancelRequest(target);
            }

            @Override
            public void onSeekTo(long pos) {
                super.onSeekTo(pos);
            }
        });

    }

    @Override
    public boolean onUnbind(Intent intent) {
        m_objMediaSession.release();
        return super.onUnbind(intent);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {

    }
}