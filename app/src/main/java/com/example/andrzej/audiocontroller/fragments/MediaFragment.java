package com.example.andrzej.audiocontroller.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.adapters.MediaRecyclerAdapter;
import com.example.andrzej.audiocontroller.models.ExploreItem;
import com.example.andrzej.audiocontroller.models.Playlist;
import com.example.andrzej.audiocontroller.views.BackHandledFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Media fragment contains list of playlists in current dir, tracks
 * and content of playlists(tracks). This last functionality might be
 * outsorced somewhere else.
 */
public class MediaFragment extends BackHandledFragment {

    public static final String TAG = "MEDIA_FRAGMENT";

    //Objects
    private MediaRecyclerAdapter mAdapter;

    //View bindings
    @Bind(R.id.mediaRecyclerView)
    RecyclerView mRecyclerView;

    public MediaFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_media, container, false);
        ButterKnife.bind(this, rootView);

        //Objects init
        mAdapter = new MediaRecyclerAdapter(getActivity(), generateDataset());

        //Recycler config
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return rootView;
    }

    private List<Playlist> generateDataset() {
        List<Playlist> list = new ArrayList<>();
        List<ExploreItem> tracks = new ArrayList<>();

        String[] songNames = {"Betwen.mp3", "321sda.mp3", "fadga.flac", "srak.flac"};

        for(int i = 0; i<songNames.length; i++){
            ExploreItem item = new ExploreItem();
            item.setDirectory(false);
            item.setName(songNames[i]);
            tracks.add(item);
        }

        String[] playlistNames = {"Rock", "The Who", "The Beatles", "Unknown"};
        String[] playlisCovers = {null , "https://pbs.twimg.com/profile_images/451041962011807744/yJg4Nq8V.jpeg" , "http://www.stereocapri.net/images/slideshow/05.jpg",null};

        for(int i = 0; i<playlistNames.length; i++){
            Playlist item = new Playlist();
            item.setName(playlistNames[i]);
            item.setCoverUrl(playlisCovers[i]);
            item.setTracks(tracks);
            list.add(item);
        }

        return list;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAdapter.onSaveInstanceState(outState);
    }

    @Override
    public String getTagText() {
        return TAG;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}