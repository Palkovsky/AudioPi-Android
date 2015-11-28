package com.example.andrzej.audiocontroller.activities;

import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.andrzej.audiocontroller.MyApplication;
import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.fragments.AutoPlaylistFragment;
import com.example.andrzej.audiocontroller.fragments.MediaFragment;
import com.example.andrzej.audiocontroller.fragments.LocalPlaylistFragment;
import com.example.andrzej.audiocontroller.interfaces.FragmentCallback;
import com.example.andrzej.audiocontroller.interfaces.MediaCallback;
import com.example.andrzej.audiocontroller.models.Playlist;
import com.example.andrzej.audiocontroller.utils.Converter;
import com.example.andrzej.audiocontroller.utils.PlaybackUtils;
import com.example.andrzej.audiocontroller.utils.SettingsContentObserver;
import com.example.andrzej.audiocontroller.views.BackHandledFragment;

public class DetalisActivity extends AppCompatActivity implements BackHandledFragment.BackHandlerInterface, FragmentCallback, MediaCallback {

    public static final String PLAYLIST_SER_KEY = "PLAYLIST_SERIALIZABLE";
    public static final String TRACK_SER_KEY = "TRACK_SERIALIZABLE";

    FragmentManager fragmentManager;
    private BackHandledFragment selectedFragment;
    private SettingsContentObserver mSettingsContentObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalis);

        //Read extras
        Playlist playlist = (Playlist) getIntent().getSerializableExtra(MediaFragment.SER_KEY);
        Toast.makeText(this, playlist.getName() + " | " + playlist.getTracks().size(), Toast.LENGTH_SHORT).show();

        //Volume buttons observer
        mSettingsContentObserver = new SettingsContentObserver(this, new Handler(), new SettingsContentObserver.VolumeCallback() {
            @Override
            public void onVolumeChange(int volume) {
                MyApplication.volumeManager.setVolume(Converter.androidVolumeToStandard(volume));
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragmentManager = getSupportFragmentManager();

        BackHandledFragment fragment;
        if (playlist.isLocal())
            fragment = new LocalPlaylistFragment();
        else
            fragment = new AutoPlaylistFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(PLAYLIST_SER_KEY, playlist);
        fragment.setArguments(bundle);
        putFragment(fragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.streamManager.registerMediaListener(this);
        getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, mSettingsContentObserver);
        if(PlaybackUtils.useMediaVolumeStream())
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
        else
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getContentResolver().unregisterContentObserver(mSettingsContentObserver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (fragmentManager.getBackStackEntryCount() > 1)
                    fragmentManager.popBackStack();
                else
                    finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void putFragment(BackHandledFragment fragment) {
        fragment.registerFragmentCallback(this);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(fragment.getTagText());
        transaction.commit();
    }

    @Override
    public void setSelectedFragment(BackHandledFragment backHandledFragment) {
        selectedFragment = backHandledFragment;
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 1)
            super.onBackPressed();
        else {
            finish();
        }
    }

    @Override
    public void onNewFragmentStart(BackHandledFragment fragment) {
        putFragment(fragment);
    }

    @Override
    public void onMediaStart() {
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onMediaRewind(float position) {}

    @Override
    public void onMediaPause() {
        setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
    }

    @Override
    public void onMediaUnpause() {
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onMediaStop() {
        setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
    }

    @Override
    public void onMediaUpdate() {}
}
