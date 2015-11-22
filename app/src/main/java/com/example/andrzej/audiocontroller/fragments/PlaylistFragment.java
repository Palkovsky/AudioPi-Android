package com.example.andrzej.audiocontroller.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.activities.DetalisActivity;
import com.example.andrzej.audiocontroller.models.Playlist;
import com.example.andrzej.audiocontroller.views.BackHandledFragment;

import butterknife.ButterKnife;


public class PlaylistFragment extends BackHandledFragment {

    public static final String TAG = "PLAYLIST_FRAGMENT";

    private Playlist playlist;

    public PlaylistFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        playlist = (Playlist) bundle.getSerializable(DetalisActivity.PLAYLIST_SER_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_playlist, container, false);
        ButterKnife.bind(this, rootView);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(playlist.getName());

        return rootView;
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
