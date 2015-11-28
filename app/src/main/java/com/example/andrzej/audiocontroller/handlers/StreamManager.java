package com.example.andrzej.audiocontroller.handlers;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.config.Codes;
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

import java.util.Random;

public class StreamManager extends MediaSessionCompat.Callback implements StreamListener {

    private Track currentTrack;
    private Playlist currentPlaylist;
    private StreamRequester streamRequester;
    private Context context;
    private ServiceManager serviceManager;
    private MediaCallback mediaCallback;

    private int trackPlaybackMethod;
    private int playlistPlaybackMethod;

    public StreamManager(final Context context) {
        this.context = context;
        applyPlaylistPlaybackMethod(PlaybackMethods.PLAYLIST_NORMAL);
        applyTrackPlaybackMethod(PlaybackMethods.TRACK_NORMAL);

        streamRequester = new StreamRequester();
        streamRequester.registerStreamListener(this);

        serviceManager = new ServiceManager(context, StreamService.class, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == StreamService.MSG_VALUE) {
                    Toast.makeText(context, "Koniec", Toast.LENGTH_SHORT).show();
                    serviceManager.stop();
                    handleTrackEnd();
                } else if (msg.what == StreamService.SERVER_ERROR) {
                    if (Network.isNetworkAvailable(context)) {
                        currentPlaylist = null;
                        currentTrack = null;
                        if (mediaCallback != null)
                            mediaCallback.onMediaStop();
                    }
                } else {
                    if (currentTrack != null) {
                        currentTrack.setMilliPosSecs(msg.arg1);
                        if (mediaCallback != null)
                            mediaCallback.onMediaUpdate();
                    }
                }
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

                        serviceManager.stop();
                        serviceManager.start();

                        if (mediaCallback != null)
                            mediaCallback.onMediaStart();
                    }
                    break;
                case Codes.INVALID_PATH:
                case Codes.FILE_NOT_EXSISTS:
                    currentTrack.setOffline(true);
                    if (currentPlaylist.canGoNext())
                        nextTrack();
                    else {
                        Toast.makeText(context, R.string.unable_to_find_track, Toast.LENGTH_SHORT).show();
                        currentPlaylist = null;
                        currentTrack = null;
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
        serviceManager.stop();
        if (mediaCallback != null)
            mediaCallback.onMediaStop();
    }

    @Override
    public void onStreamPause(JSONObject response) {
        currentTrack.setPaused(true);
        serviceManager.stop();
        if (mediaCallback != null)
            mediaCallback.onMediaPause();
    }

    @Override
    public void onStreamUnpause(JSONObject response) {
        currentTrack.setPaused(false);
        serviceManager.start();
        if (mediaCallback != null)
            mediaCallback.onMediaUnpause();
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
    public void onQueryError(int type, VolleyError error) {
        if (!Network.isNetworkAvailable(context)) {
            Toast.makeText(context, R.string.no_internet_error, Toast.LENGTH_SHORT).show();
            if (currentTrack != null && !currentTrack.isPlaying()) {
                currentPlaylist = null;
                currentTrack = null;
                if (mediaCallback != null)
                    mediaCallback.onMediaStop();
            }
        }
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
                    }
                    break;
                case PlaybackMethods.PLAYLIST_REPEAT:
                    if (currentPlaylist.getTracks().size() == 0) {
                        currentPlaylist = null;
                        currentTrack = null;
                    } else if (currentPlaylist.canGoNext())
                        nextTrack();
                    else if (currentPlaylist.getTracks().size() > 0)
                        setPosition(0);
                    else {
                        currentPlaylist = null;
                        currentTrack = null;
                    }
                    break;
                case PlaybackMethods.PLAYLIST_TRACK_REPEAT:
                    if (currentPlaylist.getTracks().size() < currentPlaylist.position() && currentPlaylist.getTracks().size() > 0)
                        setPosition(currentPlaylist.position());
                    else {
                        currentPlaylist = null;
                        currentTrack = null;
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
                    break;
                case PlaybackMethods.TRACK_REPEAT:
                    start(true);
                    break;
            }

        } else {
            currentPlaylist = null;
            currentTrack = null;
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
            }
        } else if (currentPlaylist.getTracks().size() == 1)
            setPosition(0);
        else {
            currentPlaylist = null;
            currentTrack = null;
        }
    }


    public void start(boolean terminate) {
        streamRequester.startStream(currentTrack, terminate);
    }

    public void start(Track track, boolean terminate) {
        streamRequester.startStream(track, terminate);
    }

    public void smartpause() {
        streamRequester.smartpauseStream();
    }

    public void pause() {
        streamRequester.pauseStream();
    }

    public void unpause() {
        streamRequester.unpauseStream();
    }

    public void flush() {
        streamRequester.stopStream();
    }

    public void rewind(int seconds, boolean unpause) {
        streamRequester.rewindStream(seconds, unpause);
    }

    public void nextTrack() {
        restartPlaylistState();
        if (currentPlaylist != null && currentPlaylist.canGoNext()) {
            currentPlaylist.next();
            currentTrack = currentPlaylist.getTracks().get(currentPlaylist.position());
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

    private MediaMetadataCompat grabMetadata() {
        if (currentTrack != null) {
            Metadata metadata = currentTrack.getMetadata();
            return new MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, metadata.getAlbum())
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, metadata.getCoverUrl())
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, metadata.getArtist())
                    .putString(MediaMetadataCompat.METADATA_KEY_GENRE, metadata.getGenre())
                    .build();

        }
        return null;
    }

    private PlaybackStateCompat getStateCompat() {
        return new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT)
                .build();
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
}
