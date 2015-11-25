package com.example.andrzej.audiocontroller.views;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.example.andrzej.audiocontroller.interfaces.FragmentCallback;

public abstract class BackHandledFragment extends Fragment {

    //Interfaces
    protected BackHandlerInterface backHandlerInterface;
    protected FragmentCallback fragmentCallback;

    public abstract String getTagText();

    public abstract boolean onBackPressed();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!(getActivity() instanceof BackHandlerInterface)) {
            throw new ClassCastException("Hosting activity must implement BackHandlerInterface");
        } else {
            backHandlerInterface = (BackHandlerInterface) getActivity();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // Mark this fragment as the selected Fragment.
        backHandlerInterface.setSelectedFragment(this);
    }

    public interface BackHandlerInterface {
        void setSelectedFragment(BackHandledFragment backHandledFragment);
    }

    public void registerFragmentCallback(FragmentCallback fragmentCallback) {
        this.fragmentCallback = fragmentCallback;
    }
}