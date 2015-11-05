package com.example.andrzej.audiocontroller.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.andrzej.audiocontroller.R;
import butterknife.ButterKnife;

/**
 * Explore fragment contains list in filesystem and it
 * lets you to explore it and import playlists and tracks etc.
 */
public class ExploreFragment extends Fragment {

    public ExploreFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_explore, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }
}
