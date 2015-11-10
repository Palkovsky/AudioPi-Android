package com.example.andrzej.audiocontroller.adapters;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.andrzej.audiocontroller.fragments.ExploreFragment;
import com.example.andrzej.audiocontroller.fragments.MainFragment;
import com.example.andrzej.audiocontroller.fragments.MediaFragment;
import com.example.andrzej.audiocontroller.interfaces.ExploreFragmentCommunicator;
import com.example.andrzej.audiocontroller.views.BackHandledFragment;

import org.json.JSONObject;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private static final int ITEM_COUNT = 3;

    private MainFragment mainFragment;
    private ExploreFragment exploreFragment;
    private MediaFragment mediaFragment;

    private ExploreFragmentCommunicator exploreCommunicator;

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);

        mainFragment = new MainFragment();
        exploreFragment = new ExploreFragment();
        mediaFragment = new MediaFragment();

        exploreFragment.registerCommunicator(new ExploreFragmentCommunicator() {
            @Override
            public void onQueryStart(String url) {
                if(exploreFragment!=null)
                    exploreCommunicator.onQueryStart(url);
            }
            @Override
            public void onQuerySuccess(String url, JSONObject response) {
                if(exploreFragment!=null)
                    exploreCommunicator.onQuerySuccess(url, response);
            }
            @Override
            public void onQueryError(String url, int code) {
                if(exploreFragment!=null)
                    exploreCommunicator.onQueryError(url, code);
            }
        });
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

    public void registerExploreCommunicator(ExploreFragmentCommunicator exploreCommunicator) {
        this.exploreCommunicator = exploreCommunicator;
    }
}