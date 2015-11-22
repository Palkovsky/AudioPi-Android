package com.example.andrzej.audiocontroller.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.fragments.MediaFragment;
import com.example.andrzej.audiocontroller.fragments.PlaylistFragment;
import com.example.andrzej.audiocontroller.models.Playlist;
import com.example.andrzej.audiocontroller.views.BackHandledFragment;

public class DetalisActivity extends AppCompatActivity implements BackHandledFragment.BackHandlerInterface {

    public static final String PLAYLIST_SER_KEY = "Playlist_Serializable_KEY";

    FragmentManager fragmentManager;
    private BackHandledFragment selectedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalis);

        //Read extras
        Playlist playlist = (Playlist) getIntent().getSerializableExtra(MediaFragment.SER_KEY);
        Toast.makeText(this, playlist.getName() + " | " + playlist.getTracks().size() + " | " + playlist.getTracks().get(0).isPlaying(), Toast.LENGTH_SHORT).show();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragmentManager = getSupportFragmentManager();

        PlaylistFragment playlistFragment = new PlaylistFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(PLAYLIST_SER_KEY, playlist);
        playlistFragment.setArguments(bundle);
        putFragment(playlistFragment);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (fragmentManager.getBackStackEntryCount() > 1)
                    fragmentManager.popBackStack();
                else
                    finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void putFragment(BackHandledFragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(fragment.getTagText());
        transaction.commit();
    }

    @Override
    public void setSelectedFragment(BackHandledFragment backHandledFragment) {
        selectedFragment = backHandledFragment;
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() > 1)
            super.onBackPressed();
        else {
            finish();
        }
    }
}
