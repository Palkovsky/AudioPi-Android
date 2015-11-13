package com.example.andrzej.audiocontroller.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.adapters.MediaRecyclerAdapter;
import com.example.andrzej.audiocontroller.interfaces.OnChildItemClickListener;
import com.example.andrzej.audiocontroller.interfaces.OnChildItemLongClickListener;
import com.example.andrzej.audiocontroller.interfaces.OnItemClickListener;
import com.example.andrzej.audiocontroller.interfaces.OnLongItemClickListener;
import com.example.andrzej.audiocontroller.interfaces.OnMoreChildItemClickListener;
import com.example.andrzej.audiocontroller.interfaces.OnMoreItemClickListener;
import com.example.andrzej.audiocontroller.models.ExploreItem;
import com.example.andrzej.audiocontroller.models.Playlist;
import com.example.andrzej.audiocontroller.views.BackHandledFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Media fragment contains list of playlists in current dir, tracks
 * and content of playlists(tracks). This last functionality might be
 * outsorced somewhere else.
 */
public class MediaFragment extends BackHandledFragment implements PullRefreshLayout.OnRefreshListener {

    public static final String TAG = "MEDIA_FRAGMENT";

    //Objects
    private MediaRecyclerAdapter mAdapter;

    //View bindings
    @Bind(R.id.mediaRecyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.swipeRefreshLayout)
    PullRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.mediaProgressBar)
    SmoothProgressBar mProgressBar;
    @Bind(R.id.errorContainer)
    LinearLayout mErrorContainer;
    @Bind(R.id.errorTextView)
    TextView mErrorTextView;
    @Bind(R.id.errorImageView)
    ImageView mErrorImageView;

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


        //Listener
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mAdapter.setOnTrackClickListener(new OnChildItemClickListener() {
            @Override
            public void onChildItemClick(View v, int position, Object obj) {
                ExploreItem item = (ExploreItem) obj;
                Toast.makeText(getActivity(), "POS: " + position + " - " + item.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        mAdapter.setOnTrackLongClickListener(new OnChildItemLongClickListener() {
            @Override
            public void onChildLongClickListener(View v, int position, Object obj) {
                ExploreItem item = (ExploreItem) obj;
                Toast.makeText(getActivity(), "POKAZ MENIU | POS: " + position + " - " + item.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        mAdapter.setOnMoreTrackItemClickListener(new OnMoreChildItemClickListener() {
            @Override
            public void onMoreChildItemClick(View v, int position, Object obj) {
                ExploreItem item = (ExploreItem) obj;
                Toast.makeText(getActivity(), "POKAZ MENIU | POS: " + position + " - " + item.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        //Recycler config
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setUpNormalLayout();

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

    private void setUpErrorLayout(){
        mProgressBar.setVisibility(View.GONE);
        mErrorContainer.setVisibility(View.VISIBLE);
    }

    private void setUpNormalLayout(){
        mProgressBar.setVisibility(View.GONE);
        mErrorContainer.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void setUpLoadingLayout(){mProgressBar.setVisibility(View.VISIBLE);}

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
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
}