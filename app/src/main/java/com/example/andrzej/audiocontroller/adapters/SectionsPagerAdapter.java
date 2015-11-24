package com.example.andrzej.audiocontroller.adapters;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.andrzej.audiocontroller.config.Filters;
import com.example.andrzej.audiocontroller.fragments.ExploreFragment;
import com.example.andrzej.audiocontroller.fragments.MainFragment;
import com.example.andrzej.audiocontroller.fragments.MediaFragment;
import com.example.andrzej.audiocontroller.interfaces.ExploreFragmentCommunicator;
import com.example.andrzej.audiocontroller.interfaces.MediaCommunicator;
import com.example.andrzej.audiocontroller.models.Playlist;
import com.example.andrzej.audiocontroller.models.Track;
import com.example.andrzej.audiocontroller.utils.Communicator;
import com.example.andrzej.audiocontroller.views.BackHandledFragment;

import org.json.JSONObject;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private static final int ITEM_COUNT = 3;

    private MainFragment mainFragment;
    private ExploreFragment exploreFragment;
    private MediaFragment mediaFragment;

    //Interfaces which must be wired with activity
    private MediaCommunicator mediaCommunicator;

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);

        mainFragment = new MainFragment();
        exploreFragment = new ExploreFragment();
        mediaFragment = new MediaFragment();

        Communicator.getInstance().setListener(new Communicator.OnCustomStateListener() {
            @Override
            public void onMessage() {
                switch (Communicator.getInstance().getMessage()){


                    case Communicator.LOCAL_PLAYLIST_REMOVED:
                        mediaFragment.handlePlaylistDelete();
                        break;
                }
            }

            @Override
            public void onMessage(Object data) {
                switch (Communicator.getInstance().getMessage()){

                    case Communicator.LOCAL_PLAYLIST_POSITION_CHANGED:
                        mediaFragment.handlePositionChange((Integer[]) data);
                        break;

                    case Communicator.LOCAL_PLAYLIST_ITEM_REMOVED:
                        Integer pos = (Integer) data;
                        mediaFragment.handleTrackDelete(pos);
                        break;
                }
            }
        });

        exploreFragment.registerCommunicator(new ExploreFragmentCommunicator() {
            @Override
            public void onQueryStart(String url, String path) {

            }
            @Override
            public void onQuerySuccess(String url, String path, JSONObject response) {
                mediaFragment.queryPath(path);
            }
            @Override
            public void onQueryError(String url, int code) {
                mediaFragment.setUpErrorLayout(code);
            }

            @Override
            public void onCustomPlaylistTrackAppend() {
                mediaFragment.filterDataset(Filters.LOCAL_PLAYLISTS);
            }
        });

        exploreFragment.registerMediaCommunicator(new MediaCommunicator() {
            @Override
            public void onPlaylistStart(Playlist playlist, int position) {
                if (mediaCommunicator != null)
                    mediaCommunicator.onPlaylistStart(playlist, position);
            }

            @Override
            public void onTrackStart(Track track) {
                if (mediaCommunicator != null)
                    mediaCommunicator.onTrackStart(track);
            }
        });

        mediaFragment.registerMediaCommunicator(new MediaCommunicator() {
            @Override
            public void onPlaylistStart(Playlist playlist, int position) {
                if(mediaCommunicator != null)
                    mediaCommunicator.onPlaylistStart(playlist, position);
            }

            @Override
            public void onTrackStart(Track track) {
                if(mediaCommunicator != null)
                    mediaCommunicator.onTrackStart(track);
            }
        });
    }

    @Override
    public BackHandledFragment getItem(int position) {
        switch (position) {
            default:
            case 0:
                return mainFragment;
            case 1:
                return exploreFragment;
            case 2:
                return mediaFragment;
        }
    }

    @Override
    public int getCount() {
        return ITEM_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }

    public void registerMediaCommunicator(MediaCommunicator mediaCommunicator) {
        this.mediaCommunicator = mediaCommunicator;
    }
}