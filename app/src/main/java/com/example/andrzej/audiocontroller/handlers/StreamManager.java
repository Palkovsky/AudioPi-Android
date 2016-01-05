package com.example.andrzej.audiocontroller.handlers;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.config.Codes;
import com.example.andrzej.audiocontroller.config.Endpoints;
import com.example.andrzej.audiocontroller.config.PlaybackMethods;
import com.example.andrzej.audiocontroller.interfaces.MediaCallback;
import com.example.andrzej.audiocontroller.interfaces.StreamListener;
import com.example.andrzej.audiocontroller.models.Metadata;
import com.example.andrzej.audiocontroller.models.Playlist;
import com.example.andrzej.audiocontroller.models.Track;
import com.example.andrzej.audiocontroller.services.StreamService;
import com.example.andrzej.audiocontroller.services.ServiceManager;
import com.example.andrzej.audiocontroller.utils.network.Network;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StreamManager extends MediaSessionCompat.Callback implements StreamListener {

    private Track currentTrack;
    private Playlist currentPlaylist;
    private StreamRequester streamRequester;
    private Context context;
    private ServiceManager serviceManager;
    private MediaCallback mediaCallback;
    private PowerManager powerManager;
    private MediaSessionManager mediaSessionManager;
    PowerManager.WakeLock wakeLock;

    private int trackPlaybackMethod;
    private int playlistPlaybackMethod;

    public StreamManager(final Context context) {
        this.context = context;
        applyPlaylistPlaybackMethod(PlaybackMethods.PLAYLIST_NORMAL);
        applyTrackPlaybackMethod(PlaybackMethods.TRACK_NORMAL);

        powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        streamRequester = new StreamRequester();
        streamRequester.registerStreamListener(this);

        serviceManager = new ServiceManager(context, StreamService.class, new Handler() {
            @Override
            public void handleMessage(Message msg) {

                switch (msg.what) {
                    case StreamService.MSG_VALUE:
                        Toast.makeText(context, "Koniec", Toast.LENGTH_SHORT).show();
                        stopService(false);
                        handleTrackEnd();
                        Log.e("andrzej", "handle track end!");
                        break;
                    case StreamService.SERVER_ERROR:
                        if (Network.isNetworkAvailable(context)) {
                            currentPlaylist = null;
                            currentTrack = null;
                            release();
                            if (mediaCallback != null)
                                mediaCallback.onMediaStop();
                        }
                        break;
                    case StreamService.MSG_OTHER_TRACK:
                        if (Network.isNetworkAvailable(context)) {
                            Toast.makeText(context, "Other track", Toast.LENGTH_SHORT).show();
                            currentTrack = null;
                            currentPlaylist = null;
                            release();
                            if (mediaCallback != null)
                                mediaCallback.onMediaStop();
                            findTrack();
                        }
                        break;

                    default:
                        if (currentTrack != null) {
                            currentTrack.setMilliPosSecs(msg.arg1);
                            if (mediaCallback != null)
                                mediaCallback.onMediaUpdate();
                        }
                        break;
                }
            }
        });

        mediaSessionManager = new MediaSessionManager(context, new MediaSessionCallback() {
            @Override
            public void onPause() {
                if (currentTrack != null)
                    pause();
            }

            @Override
            public void onPlay() {
                if (currentTrack != null) {
                    unpause();
                }
            }

            @Override
            public void onNext() {
                if (currentPlaylist != null)
                    nextTrack();
            }

            @Override
            public void onPrev() {
                if (currentPlaylist != null)
                    prevTrack();
            }

            @Override
            public void onFlush() {
                currentPlaylist = null;
                currentTrack = null;
                release();
                if (mediaCallback != null)
                    mediaCallback.onMediaStop();
                flush();
            }
        });
    }

    @Override
    public void onStreamStart(Track track, JSONObject response) {
        try {

            int code = response.getInt("code");
            switch (code) {
                default:
                case Codes.SUCCESFULL:
                    if (currentTrack != null) {
                        currentTrack.setOffline(false);
                        currentTrack.setPlaying(true);
                        currentTrack.setPaused(false);
                        currentTrack.setMilliPosSecs(0);

                        float total = response.getInt("total");
                        currentTrack.setMilliTotalSecs(total);

                        stopService(false);
                        startService();

                        if (mediaCallback != null)
                            mediaCallback.onMediaStart();
                    }
                    break;
                case Codes.INVALID_PATH:
                case Codes.FILE_NOT_EXSISTS:
                    currentTrack.setOffline(true);
                    if (currentPlaylist != null && currentPlaylist.canGoNext())
                        nextTrack();
                    else {
                        Toast.makeText(context, R.string.unable_to_find_track, Toast.LENGTH_SHORT).show();
                        currentPlaylist = null;
                        currentTrack = null;
                        release();
                        if (mediaCallback != null)
                            mediaCallback.onMediaStop();
                        flush();
                    }
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStreamStop(JSONObject response) {
        currentPlaylist = null;
        currentTrack = null;
        stopService(true);
        release();
        if (mediaCallback != null)
            mediaCallback.onMediaStop();
    }

    @Override
    public void onStreamPause(JSONObject response) {
        if (currentTrack != null) {
            currentTrack.setPaused(true);
            stopService(false);
            if (mediaCallback != null)
                mediaCallback.onMediaPause();
        }
    }

    @Override
    public void onStreamUnpause(JSONObject response) {
        if (currentTrack != null) {
            currentTrack.setPaused(false);
            startService();
            if (mediaCallback != null)
                mediaCallback.onMediaUnpause();
        }
    }

    @Override
    public void onStreamRewind(JSONObject response) {
        try {
            int pos = response.getInt("newPosition");
            currentTrack.setMilliPosSecs(pos);
            if (mediaCallback != null)
                mediaCallback.onMediaRewind(pos);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onStreamResume(JSONObject response) {
        try {
            if (response.getInt("code") == Codes.SUCCESFULL) {
                Track track = new Track();
                Metadata metadata = new Metadata();
                JSONObject playback = response.getJSONObject("playback");
                JSONObject info = response.getJSONObject("info");
                track.setPath(playback.getString("path"));
                track.setMilliTotalSecs(playback.getJSONObject("total").getInt("millis"));
                track.setPaused(playback.getBoolean("paused"));
                track.setName(info.getString("name"));
                metadata.setAlbum(info.getString("album"));
                metadata.setArtist(info.getString("artist"));
                metadata.setCoverUrl(Endpoints.getFileUrl(info.getString("cover")));
                metadata.setFilesize(info.getDouble("filesize"));
                metadata.setGenre(info.getString("genre"));
                metadata.setLength(info.getInt("length"));
                track.setMetadata(metadata);

                Playlist playlist = new Playlist();
                List<Track> tracks = new ArrayList<>();
                tracks.add(track);
                playlist.setTracks(tracks);
                playlist.setName(track.getName());

                setCurrentPlaylist(playlist, 0);
                stopService(false);
                startService();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onQueryError(int type, VolleyError error) {
        if (!Network.isNetworkAvailable(context)) {
            Toast.makeText(context, R.string.no_internet_error, Toast.LENGTH_SHORT).show();
            if (currentTrack != null && !currentTrack.isPlaying()) {
                currentPlaylist = null;
                currentTrack = null;
                stopService(true);
                release();
                if (mediaCallback != null)
                    mediaCallback.onMediaStop();
            }
        }
    }

    //This method checks if any track is currently playing
    public void findTrack() {
        if (currentTrack == null)
            streamRequester.findStream();
    }

    private void handleTrackEnd() {
        //This is the place to specify different reproduction methods, like:
        //repeat, shuffle, end etc.
        if (currentPlaylist != null)
            switch (playlistPlaybackMethod) {
                default:
                case PlaybackMethods.PLAYLIST_NORMAL:
                    if (currentPlaylist.canGoNext())
                        nextTrack();
                    else {
                        currentPlaylist = null;
                        currentTrack = null;
                        release();
                    }
                    break;
                case PlaybackMethods.PLAYLIST_REPEAT:
                    if (currentPlaylist.getTracks().size() == 0) {
                        currentPlaylist = null;
                        currentTrack = null;
                        release();
                    } else if (currentPlaylist.canGoNext())
                        nextTrack();
                    else if (currentPlaylist.getTracks().size() > 0)
                        setPosition(0);
                    else {
                        currentPlaylist = null;
                        currentTrack = null;
                        release();
                    }
                    break;
                case PlaybackMethods.PLAYLIST_TRACK_REPEAT:
                    if (currentPlaylist.getTracks().size() < currentPlaylist.position() && currentPlaylist.getTracks().size() > 0)
                        setPosition(currentPlaylist.position());
                    else {
                        currentPlaylist = null;
                        currentTrack = null;
                        release();
                    }
                    break;
                case PlaybackMethods.PLAYLIST_SHUFFLE:
                    shuffle();
                    break;
            }
        else if (currentTrack != null) {
            switch (trackPlaybackMethod) {
                default:
                case PlaybackMethods.TRACK_NORMAL:
                    currentPlaylist = null;
                    currentTrack = null;
                    release();
                    break;
                case PlaybackMethods.TRACK_REPEAT:
                    start(true);
                    break;
            }

        } else {
            currentPlaylist = null;
            currentTrack = null;
            release();
        }
        if (mediaCallback != null)
            mediaCallback.onMediaStop();
    }

    public void shuffle() {
        if (currentPlaylist.getTracks().size() > 1) {
            Random r = new Random();
            int randomPos = r.nextInt(currentPlaylist.getTracks().size());
            if (randomPos == currentPlaylist.position()) {
                if (randomPos == 0)
                    randomPos++;
                else
                    randomPos--;
            }
            if (randomPos >= 0 && randomPos < currentPlaylist.getTracks().size())
                setPosition(randomPos);
            else {
                currentPlaylist = null;
                currentTrack = null;
                release();
            }
        } else if (currentPlaylist.getTracks().size() == 1)
            setPosition(0);
        else {
            currentPlaylist = null;
            currentTrack = null;
            release();
        }
    }


    public void start(boolean terminate) {
        streamRequester.startStream(currentTrack, terminate);
    }

    public void start(Track track, boolean terminate) {
        streamRequester.startStream(track, terminate);
    }


    public void pause() {
        streamRequester.pauseStream();
    }

    public void unpause() {
        streamRequester.unpauseStream();
    }

    public void flush() {
        streamRequester.stopStream();
        release();
    }

    public void rewind(int seconds, boolean unpause) {
        streamRequester.rewindStream(seconds, unpause);
    }

    public void nextTrack() {
        restartPlaylistState();
        if (currentPlaylist != null && currentPlaylist.canGoNext()) {
            currentPlaylist.next();
            currentTrack = currentPlaylist.getTracks().get(currentPlaylist.position());
            currentTrack.setMilliPosSecs(0);
            currentTrack.setPauseStartTime(System.currentTimeMillis());
            start(true);
        }
    }

    public void prevTrack() {
        restartPlaylistState();

        if (currentPlaylist != null && currentPlaylist.position() >= currentPlaylist.getTracks().size()) {
            currentPlaylist.setPosition(currentPlaylist.getPosition() - 1);
            prevTrack();
        } else if (currentPlaylist != null && currentPlaylist.canGoPrev()) {
            currentPlaylist.prev();
            currentTrack = currentPlaylist.getTracks().get(currentPlaylist.position());
            currentTrack.setMilliPosSecs(0);
            currentTrack.setPauseStartTime(System.currentTimeMillis());
            start(true);
        }
    }

    public void setPosition(int position) {
        restartPlaylistState();
        if (currentPlaylist != null) {
            currentPlaylist.setPosition(position);
            currentTrack = currentPlaylist.getTracks().get(currentPlaylist.position());
            start(true);
        }
    }

    //Getters & Setters
    public Track getCurrentTrack() {
        return currentTrack;
    }

    public void setCurrentTrack(Track currentTrack) {
        this.currentTrack = currentTrack;
        this.currentPlaylist = null;
    }

    public Playlist getCurrentPlaylist() {
        return currentPlaylist;
    }

    public void setCurrentPlaylist(Playlist currentPlaylist) {
        this.currentPlaylist = currentPlaylist;
    }

    public void setCurrentPlaylist(Playlist currentPlaylist, int position) {
        restartPlaylistState();
        this.currentPlaylist = currentPlaylist;
        this.currentPlaylist.setPosition(position);
        this.currentTrack = currentPlaylist.getTracks().get(position);
    }

    public void registerMediaListener(MediaCallback mediaCallback) {
        this.mediaCallback = mediaCallback;
    }

    private void restartPlaylistState() {
        if (currentPlaylist != null) {
            for (Track track : currentPlaylist.getTracks())
                track.setPlaying(false);
        }
    }

    public int getTrackPlaybackMethod() {
        return trackPlaybackMethod;
    }

    public void applyTrackPlaybackMethod(int trackPlaybackMethod) {
        this.trackPlaybackMethod = trackPlaybackMethod;
    }

    public int getPlaylistPlaybackMethod() {
        return playlistPlaybackMethod;
    }

    public void applyPlaylistPlaybackMethod(int playlistPlaybackMethod) {
        this.playlistPlaybackMethod = playlistPlaybackMethod;
    }

    public void startService() {
        if (!wakeLock.isHeld()) {
            wakeLock.acquire();
            Log.e("andrzej", "wake lock acquired");
        }
        serviceManager.start();
        if (currentPlaylist != null)
            mediaSessionManager.start();
    }

    public void stopService(boolean flush) {
        serviceManager.stop();
        mediaSessionManager.stop(flush);
        //if (wakeLock.isHeld())
        //    wakeLock.release();
    }

    public void release() {
        if (wakeLock.isHeld()) {
            Log.e("andrzej", "wake lock released");
            wakeLock.release();
        }
    }

    public MediaSessionManager getMediaSessionManager() {
        return mediaSessionManager;
    }

    public interface MediaSessionCallback {
        void onPause();

        void onPlay();

        void onNext();

        void onPrev();

        void onFlush();
    }
}
