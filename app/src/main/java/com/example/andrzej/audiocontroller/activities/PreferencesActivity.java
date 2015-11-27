package com.example.andrzej.audiocontroller.activities;

import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.widget.Toolbar;

import com.example.andrzej.audiocontroller.R;


public class PreferencesActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferencesFragment()).commit();
    }

    public static class PreferencesFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
