package com.example.andrzej.audiocontroller.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.andrzej.audiocontroller.R;

import butterknife.ButterKnife;

/**
 * Media fragment contains list of playlists in current dir, tracks
 * and content of playlists(tracks). This last functionality maybe will be
 * outsorced somewhere else.
 */
public class MediaFragment extends Fragment {

    public MediaFragment() {}

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

        return rootView;
    }
}