package com.example.andrzej.audiocontroller.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.andrzej.audiocontroller.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A main fragment containing most navigation icons
 * like playlists, tracks, exploring filesystem etc.
 */
public class MainFragment extends Fragment {

    public MainFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);


        return rootView;
    }
}