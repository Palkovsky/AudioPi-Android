package com.example.andrzej.audiocontroller.activities;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.adapters.SectionsPagerAdapter;

import org.w3c.dom.Text;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;

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

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);
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
}
