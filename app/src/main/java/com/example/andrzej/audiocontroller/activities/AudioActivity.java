package com.example.andrzej.audiocontroller.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrzej.audiocontroller.MyApplication;
import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.config.PlaybackMethods;
import com.example.andrzej.audiocontroller.config.PrefKeys;
import com.example.andrzej.audiocontroller.fragments.PlaylistDrawerFragment;
import com.example.andrzej.audiocontroller.interfaces.MediaCallback;
import com.example.andrzej.audiocontroller.interfaces.OnItemClickListener;
import com.example.andrzej.audiocontroller.models.Metadata;
import com.example.andrzej.audiocontroller.models.Playlist;
import com.example.andrzej.audiocontroller.models.Track;
import com.example.andrzej.audiocontroller.utils.Converter;
import com.example.andrzej.audiocontroller.utils.Image;
import com.example.andrzej.audiocontroller.utils.PlaybackUtils;
import com.example.andrzej.audiocontroller.utils.SettingsContentObserver;
import com.example.andrzej.audiocontroller.utils.listeners.OnSwipeTouchListener;
import com.example.andrzej.audiocontroller.views.BlankingImageButton;
import com.squareup.picasso.Picasso;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AudioActivity extends UnifiedActivity implements
        MediaCallback, DiscreteSeekBar.OnProgressChangeListener,
        View.OnClickListener, OnItemClickListener, DrawerLayout.
        DrawerListener, View.OnLongClickListener {

    private SharedPreferences prefs;
    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity


    @Bind(R.id.cover_container)
    RelativeLayout swipeArea;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.artist_text_view)
    TextView artistTv;
    @Bind(R.id.bigCover_imageView)
    ImageView coverIv;
    @Bind(R.id.mainCurrentTimeTv)
    TextView currentTimeTv;
    @Bind(R.id.totalTimeTv)
    TextView totalTimeTv;
    @Bind(R.id.mainSeekBar)
    DiscreteSeekBar mainSeekBar;
    @Bind(R.id.prevTrackBtn)
    BlankingImageButton prevBtn;
    @Bind(R.id.nextTrackBtn)
    BlankingImageButton nextBtn;
    @Bind(R.id.mainPlayBtn)
    BlankingImageButton playPauseBtn;
    @Bind(R.id.playbackModeBtn)
    ImageButton playbackModeBtn;

    PlaylistDrawerFragment drawerFragment;

    private final SensorEventListener mSensorListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent se) {
            if (MyApplication.streamManager.getCurrentPlaylist() != null && prefs.getBoolean(
                    getString(R.string.shuffle_on_wave_key), false)) {
                float x = se.values[0];
                float y = se.values[1];
                float z = se.values[2];
                mAccelLast = mAccelCurrent;
                mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
                float delta = mAccelCurrent - mAccelLast;
                mAccel = mAccel * 0.9f + delta; // perform low-cut filter
                if (mAccel > 10)
                    MyApplication.streamManager.shuffle();
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        ButterKnife.bind(this);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(null);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        drawerFragment = new PlaylistDrawerFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.drawerLayoutContainer, drawerFragment, PlaylistDrawerFragment.TAG)
                .commit();

        //Listeners
        mDrawerLayout.setDrawerListener(this);
        mainSeekBar.setOnProgressChangeListener(this);
        prevBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        playPauseBtn.setOnClickListener(this);
        drawerFragment.setOnClickListener(this);
        playbackModeBtn.setOnClickListener(this);
        toolbarTitle.setOnLongClickListener(this);
        swipeArea.setOnTouchListener(new OnSwipeTouchListener(this) {

            //Next track
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                if (MyApplication.streamManager.getCurrentPlaylist() != null
                        && prefs.getBoolean(getApplication().getString(R.string.gesture_navigation), true)) {
                    if (MyApplication.streamManager.getCurrentPlaylist().canGoNext())
                        MyApplication.streamManager.nextTrack();
                    else
                        MyApplication.streamManager.setPosition(0);
                }
            }

            //Prev track
            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                if (MyApplication.streamManager.getCurrentPlaylist() != null
                        && prefs.getBoolean(getApplication().getString(R.string.gesture_navigation), true))
                    if (MyApplication.streamManager.getCurrentPlaylist().canGoPrev())
                        MyApplication.streamManager.prevTrack();
                    else
                        MyApplication.streamManager.setPosition(MyApplication.streamManager.getCurrentPlaylist().getTracks().size() - 1);
            }

            //Restart current track
            @Override
            public void onSwipeDown() {
                super.onSwipeDown();
                if (MyApplication.streamManager.getCurrentPlaylist() != null
                        && prefs.getBoolean(getApplication().getString(R.string.gesture_navigation), true))
                    MyApplication.streamManager.setPosition(MyApplication.streamManager.getCurrentPlaylist().getPosition());
            }

            //Shuffle playlist
            @Override
            public void onSwipeUp() {
                super.onSwipeUp();
                if (MyApplication.streamManager.getCurrentPlaylist() != null
                        && prefs.getBoolean(getApplication().getString(R.string.gesture_navigation), true))
                    MyApplication.streamManager.shuffle();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.streamManager.applyPlaylistPlaybackMethod(prefs.getInt(PrefKeys.KEY_PLAYBACK_PLAYLIST, PlaybackMethods.PLAYLIST_NORMAL));
        MyApplication.streamManager.applyTrackPlaybackMethod(prefs.getInt(PrefKeys.KEY_PLAYBACK_TRACK, PlaybackMethods.TRACK_NORMAL));
        drawerFragment.setCurrentPlaylist(MyApplication.streamManager.getCurrentPlaylist());
        updateUI();
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    @Override
    public void onMediaStart() {
        super.onMediaStart();
        updateUI();
        drawerFragment.updateUI();
    }

    @Override
    public void onMediaRewind(float position) {
        super.onMediaRewind(position);
        updateUiLight();
    }

    @Override
    public void onMediaPause() {
        super.onMediaPause();
        updateUiLight();
        drawerFragment.updateUI();
    }

    @Override
    public void onMediaUnpause() {
        super.onMediaUnpause();
        updateUiLight();
        drawerFragment.updateUI();
    }

    @Override
    public void onMediaStop() {
        super.onMediaStop();
        drawerFragment.setCurrentPlaylist(MyApplication.streamManager.getCurrentPlaylist());
        drawerFragment.updateUI();
        updateUI();
    }

    @Override
    public void onMediaUpdate() {
        super.onMediaUpdate();
        updateUiLight();
    }

    //Just to have some way to flush current stream
    @Override
    public boolean onLongClick(View v) {
        MyApplication.streamManager.flush();
        MyApplication.streamManager.setCurrentPlaylist(null);
        MyApplication.streamManager.setCurrentTrack(null);
        MyApplication.streamManager.stopService(true);
        MyApplication.streamManager.release();
        updateUI();
        return false;
    }

    @Override
    public void onClick(View v) {
        Track currentTrack = MyApplication.streamManager.getCurrentTrack();
        switch (v.getId()) {
            case R.id.nextTrackBtn:
                if (MyApplication.streamManager.getCurrentPlaylist() != null
                        && MyApplication.streamManager.getCurrentPlaylist().canGoNext())
                    MyApplication.streamManager.nextTrack();
                break;
            case R.id.prevTrackBtn:
                if (MyApplication.streamManager.getCurrentPlaylist() != null
                        && MyApplication.streamManager.getCurrentPlaylist().canGoPrev())
                    MyApplication.streamManager.prevTrack();
                break;
            case R.id.mainPlayBtn:
                if (currentTrack != null) {
                    if (currentTrack.isPaused())
                        MyApplication.streamManager.unpause();
                    else
                        MyApplication.streamManager.pause();
                }
                break;
            case R.id.playbackModeBtn:
                int currentPlaylistPlaybackMode = MyApplication.streamManager.getPlaylistPlaybackMethod();
                int currentTrackPlaybackMode = MyApplication.streamManager.getTrackPlaybackMethod();
                Playlist currentPlaylist = MyApplication.streamManager.getCurrentPlaylist();
                if (currentPlaylist != null) {
                    switch (currentPlaylistPlaybackMode) {
                        case PlaybackMethods.PLAYLIST_NORMAL:
                            MyApplication.streamManager.applyPlaylistPlaybackMethod(PlaybackMethods.PLAYLIST_REPEAT);
                            prefs.edit().putInt(PrefKeys.KEY_PLAYBACK_PLAYLIST, PlaybackMethods.PLAYLIST_REPEAT).apply();
                            Toast.makeText(this, R.string.to_playlist_repeat, Toast.LENGTH_SHORT).show();
                            break;
                        case PlaybackMethods.PLAYLIST_REPEAT:
                            MyApplication.streamManager.applyPlaylistPlaybackMethod(PlaybackMethods.PLAYLIST_TRACK_REPEAT);
                            prefs.edit().putInt(PrefKeys.KEY_PLAYBACK_PLAYLIST, PlaybackMethods.PLAYLIST_TRACK_REPEAT).apply();
                            Toast.makeText(this, R.string.to_playlist_track_repeat, Toast.LENGTH_SHORT).show();
                            break;
                        case PlaybackMethods.PLAYLIST_TRACK_REPEAT:
                            MyApplication.streamManager.applyPlaylistPlaybackMethod(PlaybackMethods.PLAYLIST_SHUFFLE);
                            prefs.edit().putInt(PrefKeys.KEY_PLAYBACK_PLAYLIST, PlaybackMethods.PLAYLIST_SHUFFLE).apply();
                            Toast.makeText(this, R.string.to_playlist_shuffle, Toast.LENGTH_SHORT).show();
                            break;
                        case PlaybackMethods.PLAYLIST_SHUFFLE:
                            MyApplication.streamManager.applyPlaylistPlaybackMethod(PlaybackMethods.PLAYLIST_NORMAL);
                            prefs.edit().putInt(PrefKeys.KEY_PLAYBACK_PLAYLIST, PlaybackMethods.PLAYLIST_NORMAL).apply();
                            Toast.makeText(this, R.string.to_playlist_normal, Toast.LENGTH_SHORT).show();
                            break;
                    }
                } else if (currentTrack != null) {
                    switch (currentTrackPlaybackMode) {
                        case PlaybackMethods.TRACK_NORMAL:
                            MyApplication.streamManager.applyTrackPlaybackMethod(PlaybackMethods.TRACK_REPEAT);
                            prefs.edit().putInt(PrefKeys.KEY_PLAYBACK_TRACK, PlaybackMethods.TRACK_REPEAT).apply();
                            Toast.makeText(this, R.string.to_track_repeat, Toast.LENGTH_SHORT).show();
                            break;
                        case PlaybackMethods.TRACK_REPEAT:
                            MyApplication.streamManager.applyTrackPlaybackMethod(PlaybackMethods.TRACK_NORMAL);
                            prefs.edit().putInt(PrefKeys.KEY_PLAYBACK_TRACK, PlaybackMethods.TRACK_NORMAL).apply();
                            Toast.makeText(this, R.string.to_track_normal, Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
                updateButtons();
                break;
        }
    }

    @Override
    public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
        if (MyApplication.streamManager.getCurrentTrack() != null)
            MyApplication.streamManager.rewind(Converter.millisToSeconds(seekBar.getProgress()), true);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END))
            mDrawerLayout.closeDrawer(GravityCompat.END);
        else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_audio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_playlist:
                if (mDrawerLayout.isDrawerOpen(GravityCompat.END))
                    mDrawerLayout.closeDrawer(GravityCompat.END);
                else
                    mDrawerLayout.openDrawer(GravityCompat.END);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //This handles drawer item clicks
    @Override
    public void onItemClick(View v, int position) {
        MyApplication.streamManager.setPosition(position);
    }

    private void updateUI() {
        updateUiLight();
        Track currentTrack = MyApplication.streamManager.getCurrentTrack();
        if (currentTrack != null) {
            toolbarTitle.setText(currentTrack.getFormattedName());
            toolbarTitle.setSelected(true);
        }
    }

    private void updateUiLight() {
        Track currentTrack = MyApplication.streamManager.getCurrentTrack();
        if (currentTrack == null) {
            mainSeekBar.setMin(0);
            mainSeekBar.setMax(0);
            mainSeekBar.setProgress(0);
            mainSeekBar.setEnabled(false);
            toolbarTitle.setText("...");
            artistTv.setText("...");
            totalTimeTv.setText(R.string.zero_formatted_time);
            currentTimeTv.setText(R.string.zero_formatted_time);
            Image.setSourceDrawable(this, coverIv, R.drawable.default_big_cover);
        } else {
            Metadata metadata = currentTrack.getMetadata();
            String artist = getString(R.string.unknown_simple);
            if (metadata.getArtist() != null && !metadata.getArtist().equals("") && !metadata.getArtist().equals("null"))
                artist = metadata.getArtist();
            mainSeekBar.setMin(0);
            mainSeekBar.setMax(Math.round(currentTrack.getMilliTotalSecs()));
            mainSeekBar.setProgress(Math.round(currentTrack.getMilliPosSecs()));
            mainSeekBar.setEnabled(true);
            artistTv.setText(artist);
            Picasso.with(this).load(currentTrack.getMetadata().getCoverUrl())
                    .placeholder(R.drawable.default_big_cover)
                    .error(R.drawable.default_big_cover).into(coverIv);

            totalTimeTv.setText(Converter.secsToFormattedTime(Converter.millisToSeconds((int) currentTrack.getMilliTotalSecs())));
            currentTimeTv.setText(Converter.secsToFormattedTime(Converter.millisToSeconds((int) currentTrack.getMilliPosSecs())));
        }
        updateButtons();
    }

    private void updateButtons() {
        Track currentTrack = MyApplication.streamManager.getCurrentTrack();
        Playlist currentPlaylist = MyApplication.streamManager.getCurrentPlaylist();
        if (currentTrack == null) {
            Image.setSourceDrawable(this, playPauseBtn, R.drawable.ic_pause_black_48dp);
            playPauseBtn.setEnabled(false);
            prevBtn.setEnabled(false);
            nextBtn.setEnabled(false);
        }

        if (currentPlaylist == null) {
            prevBtn.setEnabled(false);
            nextBtn.setEnabled(false);
        } else {
            if (!currentPlaylist.canGoNext())
                nextBtn.setEnabled(false);
            else
                nextBtn.setEnabled(true);
            if (!currentPlaylist.canGoPrev())
                prevBtn.setEnabled(false);
            else
                prevBtn.setEnabled(true);
        }

        if (currentTrack != null) {
            playPauseBtn.setEnabled(true);
            if (currentTrack.isPaused())
                Image.setSourceDrawable(this, playPauseBtn, R.drawable.ic_play_arrow_black_48dp);
            else
                Image.setSourceDrawable(this, playPauseBtn, R.drawable.ic_pause_black_48dp);
        }

        if (currentPlaylist != null) {
            playbackModeBtn.setVisibility(View.VISIBLE);
            int playlistPlaybackMode = MyApplication.streamManager.getPlaylistPlaybackMethod();
            switch (playlistPlaybackMode) {
                case PlaybackMethods.PLAYLIST_NORMAL:
                    Image.setSourceDrawable(this, playbackModeBtn, R.drawable.ic_repeat_one_black_36dp);
                    break;
                case PlaybackMethods.PLAYLIST_TRACK_REPEAT:
                    Image.setSourceDrawable(this, playbackModeBtn, R.drawable.ic_repeat_black_36dp);
                    break;
                case PlaybackMethods.PLAYLIST_REPEAT:
                    Image.setSourceDrawable(this, playbackModeBtn, R.drawable.ic_reorder_black_36dp);
                    break;
                case PlaybackMethods.PLAYLIST_SHUFFLE:
                    Image.setSourceDrawable(this, playbackModeBtn, R.drawable.ic_shuffle_black_36dp);
                    break;
            }
        } else if (currentTrack != null) {
            playbackModeBtn.setVisibility(View.VISIBLE);
            int trackPlaybackMode = MyApplication.streamManager.getTrackPlaybackMethod();
            switch (trackPlaybackMode) {
                case PlaybackMethods.TRACK_NORMAL:
                    Image.setSourceDrawable(this, playbackModeBtn, R.drawable.ic_repeat_one_black_36dp);
                    break;
                case PlaybackMethods.TRACK_REPEAT:
                    Image.setSourceDrawable(this, playbackModeBtn, R.drawable.ic_repeat_black_36dp);
                    break;
            }
        } else
            playbackModeBtn.setVisibility(View.GONE);
    }


    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
    }

    @Override
    public void onDrawerOpened(View drawerView) {
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        toolbarTitle.setSelected(true);
    }

    @Override
    public void onDrawerStateChanged(int newState) {
    }

}
