package com.example.andrzej.audiocontroller.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.andrzej.audiocontroller.R;
import com.r0adkll.slidr.Slidr;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AudioActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.mainSeekBar)
    DiscreteSeekBar mainSeekBar;

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

    }

}
