package com.example.andrzej.audiocontroller.fragments;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.activities.DetalisActivity;
import com.example.andrzej.audiocontroller.models.Playlist;
import com.example.andrzej.audiocontroller.models.Track;
import com.example.andrzej.audiocontroller.views.BackHandledFragment;


public class TrackFragment extends BackHandledFragment {

    public static final String TAG = "TRACK_FRAGMENT";

    //Objects
    private Track track;

    public TrackFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        track = (Track) bundle.getSerializable(DetalisActivity.TRACK_SER_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_track, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(track.getFormattedName());
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
