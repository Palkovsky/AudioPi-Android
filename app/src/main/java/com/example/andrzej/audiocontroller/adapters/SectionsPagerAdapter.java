package com.example.andrzej.audiocontroller.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.andrzej.audiocontroller.fragments.ExploreFragment;
import com.example.andrzej.audiocontroller.fragments.MainFragment;
import com.example.andrzej.audiocontroller.fragments.MediaFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private static final int ITEM_COUNT = 3;

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            default:
            case 0:
                return new MainFragment();
            case 1:
                return new ExploreFragment();
            case 2:
                return new MediaFragment();
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