package com.example.andrzej.audiocontroller.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.andrzej.audiocontroller.fragments.ExploreFragment;
import com.example.andrzej.audiocontroller.fragments.MainFragment;
import com.example.andrzej.audiocontroller.fragments.MediaFragment;
import com.example.andrzej.audiocontroller.views.BackHandledFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private static final int ITEM_COUNT = 3;

    private BackHandledFragment mainFragment;
    private BackHandledFragment exploreFragment;
    private BackHandledFragment mediaFragment;

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);

        mainFragment = new MainFragment();
        exploreFragment = new ExploreFragment();
        mediaFragment = new MediaFragment();
    }

    @Override
    public BackHandledFragment getItem(int position) {
        switch (position) {
            default:
            case 0:
                return mainFragment;
            case 1:
                return exploreFragment;
            case 2:
                return mediaFragment;
        }
    }

    @Override
    public int getCount() {
        return ITEM_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }
}