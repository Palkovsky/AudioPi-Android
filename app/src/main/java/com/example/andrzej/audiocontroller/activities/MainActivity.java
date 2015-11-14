package com.example.andrzej.audiocontroller.activities;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.adapters.SectionsPagerAdapter;
import com.example.andrzej.audiocontroller.interfaces.ExploreFragmentCommunicator;
import com.example.andrzej.audiocontroller.interfaces.MediaCommunicator;
import com.example.andrzej.audiocontroller.models.ExploreItem;
import com.example.andrzej.audiocontroller.models.Playlist;
import com.example.andrzej.audiocontroller.views.BackHandledFragment;

import org.json.JSONObject;
import org.w3c.dom.Text;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, BackHandledFragment.BackHandlerInterface, ViewPager.OnPageChangeListener, MediaCommunicator {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private BackHandledFragment selectedFragment;

    @Bind(R.id.mainTabsPager)
    ViewPager mViewPager;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.bottom_toolbar)
    LinearLayout bottomToolbar;
    @Bind(R.id.tabsIndicator)
    CircleIndicator mCircleIndicator;
    @Bind(R.id.miniTrackTitle)
    TextView miniTrackTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(null);

        miniTrackTitle.setSelected(true);

        //Create section adapter
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        //Adapter listeners(actions from fragments)
        mSectionsPagerAdapter.registerMediaCommunicator(this);

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(mSectionsPagerAdapter.getCount());
        mViewPager.addOnPageChangeListener(this);
        mCircleIndicator.setViewPager(mViewPager);

        bottomToolbar.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottom_toolbar:
                Intent intent = new Intent(this, AudioActivity.class);
                startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (selectedFragment == null || !selectedFragment.onBackPressed()) {
            if (mViewPager.getCurrentItem() == 2)
                mViewPager.setCurrentItem(1, true);
            else if (mViewPager.getCurrentItem() == 1)
                mViewPager.setCurrentItem(0, true);
            else
                super.onBackPressed();
        }

    }

    @Override
    public void setSelectedFragment(BackHandledFragment backHandledFragment) {
        this.selectedFragment = mSectionsPagerAdapter.getItem(mViewPager.getCurrentItem());
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPageSelected(int position) {
        setSelectedFragment(null);
    }

    @Override
    public void onPlaylistStart(Playlist playlist, int position) {
        Toast.makeText(this, "Startujemy: " + playlist.getName() + " POS: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTrackStart(ExploreItem track) {
        Toast.makeText(this, "Uruchomiono: " + track.getName(), Toast.LENGTH_SHORT).show();
    }
}
