package com.example.andrzej.audiocontroller.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.adapters.PlaylistDrawerRecyclerAdapter;
import com.example.andrzej.audiocontroller.models.Playlist;
import com.example.andrzej.audiocontroller.models.Track;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class PlaylistDrawerFragment extends Fragment {

    public static final String TAG = "PLAYIST_DRAWER_FRAGMENT";

    private PlaylistDrawerRecyclerAdapter mAdapter;
    private List<Track> mDataset;
    private Playlist currentPlaylist;

    @Bind(R.id.playlistRecyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.albumTv)
    TextView albumTv;

    public PlaylistDrawerFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_playlist_drawer, container, false);
        ButterKnife.bind(this, rootView);


        mDataset = new ArrayList<>();
        mAdapter = new PlaylistDrawerRecyclerAdapter(getActivity(), generateDataset());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    public void updateUI(){
        albumTv.setText("Def Leppard");
    }

    private List<Track> generateDataset(){
        List<Track> tracks = new ArrayList<>();

        String[] names = {"01. Let's Go.mp3", "02. Dangerous.mp3", "03. Man Enough.mp3", "04. We Belong.mp3",
        "05. Invincible.mp3", "06. Sea of Love.mp3", "07. Energized.mp3", "08. All Time High.mp3",
        "09. Battle Of My Own.mp3", "10. Broken 'N' Brokenhearted.mp3", "11. Forever Young.mp3",
        "12. Last Dance.mp3", "13. Wings Of An Anglel.mp3", "14. Blind Faith.mp3", "15. Piosenka o " +
                "bardzo dugim tytule.mp3"};

        for(int i = 0; i < names.length; i++){
            Track track = new Track();
            track.setName(names[i]);
            if(i==5)
                track.setPlaying(true);
            tracks.add(track);
        }

        return tracks;
    }

    public void setCurrentPlaylist(Playlist currentPlaylist) {
        this.currentPlaylist = currentPlaylist;
    }
}
