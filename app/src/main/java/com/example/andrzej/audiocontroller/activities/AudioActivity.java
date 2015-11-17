package com.example.andrzej.audiocontroller.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.andrzej.audiocontroller.MyApplication;
import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.interfaces.MediaCallback;
import com.example.andrzej.audiocontroller.models.Metadata;
import com.example.andrzej.audiocontroller.models.Playlist;
import com.example.andrzej.audiocontroller.models.Track;
import com.example.andrzej.audiocontroller.utils.Converter;
import com.example.andrzej.audiocontroller.utils.Image;
import com.example.andrzej.audiocontroller.views.BlankingImageButton;
import com.r0adkll.slidr.Slidr;
import com.squareup.picasso.Picasso;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AudioActivity extends AppCompatActivity implements MediaCallback, DiscreteSeekBar.OnProgressChangeListener, View.OnClickListener {

    //Data
    private boolean tracking;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        ButterKnife.bind(this);
        Slidr.attach(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(null);
        toolbarTitle.setSelected(true);

        //Listeners
        mainSeekBar.setOnProgressChangeListener(this);
        prevBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        playPauseBtn.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.streamManager.registerMediaListener(this);
        updateUI();
    }


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

    @Override
    public void onClick(View v) {
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
                Track currentTrack = MyApplication.streamManager.getCurrentTrack();
                if (currentTrack != null) {
                    if (currentTrack.isPaused())
                        MyApplication.streamManager.unpause();
                    else
                        MyApplication.streamManager.pause();
                }
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

    private void updateUI() {
        Track currentTrack = MyApplication.streamManager.getCurrentTrack();
        if (currentTrack == null) {
            mainSeekBar.setMin(0);
            mainSeekBar.setMax(0);
            mainSeekBar.setProgress(0);
            toolbarTitle.setText("...");
            artistTv.setText("...");
            totalTimeTv.setText("00:00");
            currentTimeTv.setText("00:00");
            Image.setSourceDrawable(this, coverIv, R.drawable.default_big_cover);
        } else {
            Metadata metadata = currentTrack.getMetadata();
            String artist = getString(R.string.unknown_simple);
            if (metadata.getArtist() != null && !metadata.getArtist().equals("") && !metadata.getArtist().equals("null"))
                artist = metadata.getArtist();
            mainSeekBar.setMin(0);
            mainSeekBar.setMax(Math.round(currentTrack.getMilliTotalSecs()));
            mainSeekBar.setProgress(Math.round(currentTrack.getMilliPosSecs()));
            toolbarTitle.setText(currentTrack.getFormattedName());
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
        if(currentTrack == null){
            Image.setSourceDrawable(this, playPauseBtn, R.drawable.ic_pause_black_48dp);
            playPauseBtn.setEnabled(false);
            prevBtn.setEnabled(false);
            nextBtn.setEnabled(false);
        }

        if(currentPlaylist == null){
            prevBtn.setEnabled(false);
            nextBtn.setEnabled(false);
        }else{
            if(!currentPlaylist.canGoNext())
                nextBtn.setEnabled(false);
            else
                nextBtn.setEnabled(true);
            if(!currentPlaylist.canGoPrev())
                prevBtn.setEnabled(false);
            else
                prevBtn.setEnabled(true);
        }

        if(currentTrack != null){
            playPauseBtn.setEnabled(true);
            if(currentTrack.isPaused())
                Image.setSourceDrawable(this, playPauseBtn, R.drawable.ic_play_arrow_black_48dp);
            else
                Image.setSourceDrawable(this, playPauseBtn, R.drawable.ic_pause_black_48dp);
        }
    }
}
