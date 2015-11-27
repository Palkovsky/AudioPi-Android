package com.example.andrzej.audiocontroller.activities;


import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrzej.audiocontroller.MyApplication;
import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.adapters.SectionsPagerAdapter;
import com.example.andrzej.audiocontroller.handlers.StreamManager;
import com.example.andrzej.audiocontroller.interfaces.MediaCallback;
import com.example.andrzej.audiocontroller.interfaces.MediaCommunicator;
import com.example.andrzej.audiocontroller.models.Playlist;
import com.example.andrzej.audiocontroller.models.Track;
import com.example.andrzej.audiocontroller.models.dbmodels.PlaylistDb;
import com.example.andrzej.audiocontroller.models.dbmodels.TrackDb;
import com.example.andrzej.audiocontroller.utils.Image;
import com.example.andrzej.audiocontroller.utils.SettingsContentObserver;
import com.example.andrzej.audiocontroller.views.BackHandledFragment;
import com.example.andrzej.audiocontroller.views.BlankingImageButton;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, BackHandledFragment.BackHandlerInterface, ViewPager.OnPageChangeListener, MediaCommunicator, MediaCallback, View.OnLongClickListener {

    //UI
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private BackHandledFragment selectedFragment;
    private SettingsContentObserver mSettingsContentObserver;

    @Bind(R.id.mainTabsPager)
    ViewPager mViewPager;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.bottom_toolbar)
    LinearLayout bottomToolbar;
    @Bind(R.id.tabsIndicator)
    CircleIndicator mCircleIndicator;
    @Bind(R.id.miniArtistName)
    TextView miniArtistName;
    @Bind(R.id.miniTrackTitle)
    TextView miniTrackTitle;
    @Bind(R.id.miniCoverIv)
    ImageView miniCoverIv;
    @Bind(R.id.miniPlayBtn)
    BlankingImageButton playPauseBtn;
    @Bind(R.id.miniNextTrackBtn)
    BlankingImageButton nextTrackBtn;
    @Bind(R.id.miniPrevTrackBtn)
    BlankingImageButton prevTrackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(null);

        miniTrackTitle.setSelected(true);

        //Create section adapter
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        //Volume buttons observer
        mSettingsContentObserver = new SettingsContentObserver(this, new Handler(), new SettingsContentObserver.VolumeCallback() {
            @Override
            public void onVolumeChange(int volume) {
                Toast.makeText(getApplicationContext(), "Vol: " + volume, Toast.LENGTH_SHORT).show();
            }
        });

        //Adapter listeners(actions from fragments)
        mSectionsPagerAdapter.registerMediaCommunicator(this);

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(mSectionsPagerAdapter.getCount());
        mViewPager.addOnPageChangeListener(this);
        mCircleIndicator.setViewPager(mViewPager);

        //Listeners
        playPauseBtn.setOnClickListener(this);
        prevTrackBtn.setOnClickListener(this);
        nextTrackBtn.setOnClickListener(this);
        bottomToolbar.setOnClickListener(this);
        bottomToolbar.setOnLongClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.streamManager.registerMediaListener(this);
        getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, mSettingsContentObserver);
        updateUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getContentResolver().unregisterContentObserver(mSettingsContentObserver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.miniPlayBtn:
                Track currentTrack = MyApplication.streamManager.getCurrentTrack();
                if(currentTrack != null) {
                    if(currentTrack.isPaused())
                        MyApplication.streamManager.unpause();
                    else
                        MyApplication.streamManager.pause();
                }
                break;
            case R.id.miniPrevTrackBtn:
                if(MyApplication.streamManager.getCurrentPlaylist() != null && MyApplication.streamManager.getCurrentPlaylist()
                        .canGoPrev())
                    MyApplication.streamManager.prevTrack();
                break;
            case R.id.miniNextTrackBtn:
                if(MyApplication.streamManager.getCurrentPlaylist() != null && MyApplication.streamManager.getCurrentPlaylist()
                        .canGoNext())
                    MyApplication.streamManager.nextTrack();
                break;
            case R.id.bottom_toolbar:
                Intent intent = new Intent(this, AudioActivity.class);
                startActivity(intent);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()){
            case R.id.bottom_toolbar:
                MyApplication.streamManager.flush();
                MyApplication.streamManager.setCurrentPlaylist(null);
                MyApplication.streamManager.setCurrentTrack(null);
                updateUI();
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (selectedFragment == null || !selectedFragment.onBackPressed()) {
            if (mViewPager.getCurrentItem() != 0)
                mViewPager.setCurrentItem(0, true);
            else
                super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(this, PreferencesActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void setSelectedFragment(BackHandledFragment backHandledFragment) {
        this.selectedFragment = mSectionsPagerAdapter.getItem(mViewPager.getCurrentItem());
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        hideKeyboard(this);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPageSelected(int position) {
        setSelectedFragment(null);
    }

    //These two methods are fired when some track/playlist is clicked
    @Override
    public void onPlaylistStart(Playlist playlist, int position) {
        MyApplication.streamManager.setCurrentPlaylist(playlist, position);
        MyApplication.streamManager.start(true);
        updateUI();
        //startActivity(new Intent(this, AudioActivity.class)); - Not sure about this
    }

    @Override
    public void onTrackStart(Track track) {
        MyApplication.streamManager.setCurrentTrack(track);
        MyApplication.streamManager.start(true);
        updateUI();
        //startActivity(new Intent(this, AudioActivity.class)); - Not sure
    }

    //These ones are fired after successful request
    @Override
    public void onMediaStart() {
        updateUI();
    }

    @Override
    public void onMediaRewind(float position) {
        updateUI();
    }

    @Override
    public void onMediaPause() {
        updateUI();
    }

    @Override
    public void onMediaUnpause() {
        updateUI();
    }

    @Override
    public void onMediaStop() {
       updateUI();
    }

    @Override
    public void onMediaUpdate() {
        updateUI();
    }

    private void updateUI(){
        Track currentTrack = MyApplication.streamManager.getCurrentTrack();
        if(currentTrack == null){
            Image.setSourceDrawable(this, miniCoverIv, R.drawable.ic_music_note_black_36dp);
            miniArtistName.setText("- - - - - -");
            miniTrackTitle.setText("- - - - - -");
        }else{
            String artist = currentTrack.getMetadata().getArtist();
            String coverUrl = currentTrack.getMetadata().getCoverUrl();
            if(artist == null || artist.equals("") || artist.equals("null"))
                artist = getString(R.string.unknown_simple);

            miniArtistName.setText(artist);
            miniTrackTitle.setText(currentTrack.getFormattedName());
            if(coverUrl == null || coverUrl.equals("") || coverUrl.equals("null"))
                Image.setSourceDrawable(this, miniCoverIv, R.drawable.ic_music_note_black_36dp);
            else
                Picasso.with(this)
                .load(coverUrl)
                .error(R.drawable.ic_music_note_black_36dp)
                .placeholder(R.drawable.ic_music_note_black_36dp)
                .into(miniCoverIv);
        }
        setUpButtons();
    }

    private void setUpButtons(){
        Track currentTrack = MyApplication.streamManager.getCurrentTrack();
        Playlist currentPlaylist = MyApplication.streamManager.getCurrentPlaylist();
        if(currentTrack == null){
            Image.setSourceDrawable(this, playPauseBtn, R.drawable.ic_pause_black_48dp);
            playPauseBtn.setEnabled(false);
            prevTrackBtn.setEnabled(false);
            nextTrackBtn.setEnabled(false);
        }

        if(currentPlaylist == null){
            prevTrackBtn.setEnabled(false);
            nextTrackBtn.setEnabled(false);
        }else{
            if(!currentPlaylist.canGoNext())
                nextTrackBtn.setEnabled(false);
            else
                nextTrackBtn.setEnabled(true);
            if(!currentPlaylist.canGoPrev())
                prevTrackBtn.setEnabled(false);
            else
                prevTrackBtn.setEnabled(true);
        }

        if(currentTrack != null){
            playPauseBtn.setEnabled(true);
            if(currentTrack.isPaused())
                Image.setSourceDrawable(this, playPauseBtn, R.drawable.ic_play_arrow_black_48dp);
            else
                Image.setSourceDrawable(this, playPauseBtn, R.drawable.ic_pause_black_48dp);
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if(view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
