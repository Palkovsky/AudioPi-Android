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
import com.example.andrzej.audiocontroller.interfaces.OnItemClickListener;
import com.example.andrzej.audiocontroller.models.Playlist;
import com.example.andrzej.audiocontroller.models.Track;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class PlaylistDrawerFragment extends Fragment implements OnItemClickListener {

    public static final String TAG = "PLAYIST_DRAWER_FRAGMENT";

    private PlaylistDrawerRecyclerAdapter mAdapter;
    private List<Track> mDataset;
    private Playlist currentPlaylist;
    private OnItemClickListener clickListener;

    @Bind(R.id.playlistRecyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.albumTv)
    TextView albumTv;
    @Bind(R.id.noPlaylistTv)
    TextView noPlaylistTv;

    public PlaylistDrawerFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_playlist_drawer, container, false);
        ButterKnife.bind(this, rootView);


        mDataset = new ArrayList<>();
        mAdapter = new PlaylistDrawerRecyclerAdapter(getActivity(), mDataset);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    public void updateUI(){
        if(currentPlaylist == null){
            albumTv.setText("");
            mDataset.clear();
            mAdapter.notifyDataSetChanged();
            albumTv.setVisibility(View.GONE);
            noPlaylistTv.setVisibility(View.VISIBLE);
        }else{
            albumTv.setVisibility(View.VISIBLE);
            noPlaylistTv.setVisibility(View.GONE);
            albumTv.setText(currentPlaylist.getName());
            mDataset.clear();
            mDataset.addAll(currentPlaylist.getTracks());
            mAdapter.notifyDataSetChanged();
            mRecyclerView.scrollToPosition(mAdapter.getPlayingPosition());
        }
    }

    public void setCurrentPlaylist(Playlist currentPlaylist) {
        this.currentPlaylist = currentPlaylist;
    }

    public void setOnClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void onItemClick(View v, int position) {
        if(clickListener != null)
            clickListener.onItemClick(v, position);
    }
}
