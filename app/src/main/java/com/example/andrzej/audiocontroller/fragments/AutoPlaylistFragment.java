package com.example.andrzej.audiocontroller.fragments;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.activities.DetalisActivity;
import com.example.andrzej.audiocontroller.models.Playlist;
import com.example.andrzej.audiocontroller.models.dbmodels.PlaylistDb;
import com.example.andrzej.audiocontroller.utils.Converter;
import com.example.andrzej.audiocontroller.views.BackHandledFragment;
import com.getbase.floatingactionbutton.FloatingActionButton;

import butterknife.Bind;
import butterknife.ButterKnife;


public class AutoPlaylistFragment extends BackHandledFragment implements View.OnClickListener {

    public static final String TAG = "AUTO_PLAYLIST_FRAGMENT";

    //Objects
    private Playlist playlist;

    @Bind(R.id.saveBtn)
    FloatingActionButton saveBtn;

    public AutoPlaylistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        playlist = (Playlist) bundle.getSerializable(DetalisActivity.PLAYLIST_SER_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_auto_playlist, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(playlist.getName());

        //Listeners
        saveBtn.setOnClickListener(this);
    }

    @Override
    public String getTagText() {
        return TAG;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveBtn:
                PlaylistDb playlistDb = Converter.standardToDb(playlist); //it saves automatically

                if(fragmentCallback != null){
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(DetalisActivity.PLAYLIST_SER_KEY, Converter.dbToStandard(playlistDb));
                    LocalPlaylistFragment localPlaylistFragment = new LocalPlaylistFragment();
                    localPlaylistFragment.setArguments(bundle);
                    fragmentCallback.onNewFragmentStart(localPlaylistFragment);
                }
                break;
        }
    }
}
