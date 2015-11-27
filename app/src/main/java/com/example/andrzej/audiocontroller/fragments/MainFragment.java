package com.example.andrzej.audiocontroller.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.config.Endpoints;
import com.example.andrzej.audiocontroller.config.PrefKeys;
import com.example.andrzej.audiocontroller.views.BackHandledFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A main fragment containing status informations
 */
public class MainFragment extends BackHandledFragment implements View.OnClickListener, TextView.OnEditorActionListener {

    public static final String TAG = "MAIN_FRAGMENT";

    //Objects
    private SharedPreferences prefs;
    private StatusCallback statusCallback;

    //UI Elements
    @Bind(R.id.ipEditText)
    EditText ipEditText;
    @Bind(R.id.portEditText)
    EditText portEditText;
    @Bind(R.id.connectBtn)
    Button connectBtn;

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpUI();

        //Set up listeners
        connectBtn.setOnClickListener(this);
        portEditText.setOnEditorActionListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connectBtn:
                connect();
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        connect();
        return false;
    }

    //This methods grabs data from et's and tries to connect
    private void connect() {
        String ip = ipEditText.getText().toString();
        String port = portEditText.getText().toString();
        prefs.edit().putString(PrefKeys.KEY_IP, ip).apply();
        prefs.edit().putString(PrefKeys.KEY_PORT, port).apply();
        Endpoints.reInit(ip, port);
        if(statusCallback != null) {
            statusCallback.onConnect();
            Toast.makeText(getActivity(), "Connected to " + ip + ":" + port, Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpUI() {
        String ip = prefs.getString(PrefKeys.KEY_IP, "");
        String port = prefs.getString(PrefKeys.KEY_PORT, "");
        ipEditText.setText(ip);
        portEditText.setText(port);
    }

    @Override
    public String getTagText() {
        return TAG;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    public void registerStatusCallback(StatusCallback statusCallback) {
        this.statusCallback = statusCallback;
    }

    public interface StatusCallback{
        void onConnect();
        void onError();
    }
}