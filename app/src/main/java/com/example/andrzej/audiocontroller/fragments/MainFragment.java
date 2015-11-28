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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.andrzej.audiocontroller.MyApplication;
import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.config.Codes;
import com.example.andrzej.audiocontroller.config.Endpoints;
import com.example.andrzej.audiocontroller.config.PrefKeys;
import com.example.andrzej.audiocontroller.utils.network.Network;
import com.example.andrzej.audiocontroller.utils.network.VolleySingleton;
import com.example.andrzej.audiocontroller.views.BackHandledFragment;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A main fragment containing status informations
 */
public class MainFragment extends BackHandledFragment implements View.OnClickListener, TextView.OnEditorActionListener, DiscreteSeekBar.OnProgressChangeListener {

    public static final String TAG = "MAIN_FRAGMENT";

    //Objects
    private SharedPreferences prefs;
    private StatusCallback statusCallback;
    private RequestQueue requestQueue;


    //UI Elements
    @Bind(R.id.ipEditText)
    EditText ipEditText;
    @Bind(R.id.portEditText)
    EditText portEditText;
    @Bind(R.id.connectBtn)
    Button connectBtn;
    @Bind(R.id.volumeTv)
    TextView volumeTv;
    @Bind(R.id.volumeSeekBar)
    DiscreteSeekBar volumeSeekBar;

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        requestQueue = VolleySingleton.getsInstance().getRequestQueue();

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
        volumeSeekBar.setOnProgressChangeListener(this);
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

        final String ip = ipEditText.getText().toString();
        final String port = portEditText.getText().toString();

        String queryUrl = Endpoints.getTestUrl(ip, port);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, queryUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt("code") == Codes.SUCCESFULL) {
                        prefs.edit().putString(PrefKeys.KEY_IP, ip).apply();
                        prefs.edit().putString(PrefKeys.KEY_PORT, port).apply();
                        Endpoints.reInit(ip, port);
                        if (statusCallback != null) {
                            statusCallback.onConnect();
                            Toast.makeText(getActivity(), "Connected to " + ip + ":" + port, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Connection to " + ip + ":" + port + " failed", Toast.LENGTH_SHORT).show();
                    if (statusCallback != null)
                        statusCallback.onError();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (Network.isNetworkAvailable(getActivity()))
                    Toast.makeText(getActivity(), "Connection to " + ip + ":" + port + " failed", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getActivity(), R.string.no_internet_error, Toast.LENGTH_SHORT).show();
                if (statusCallback != null)
                    statusCallback.onError();
            }
        });

        request.setTag(TAG);
        requestQueue.add(request);
    }

    private void setUpUI() {
        String ip = prefs.getString(PrefKeys.KEY_IP, "");
        String port = prefs.getString(PrefKeys.KEY_PORT, "");
        ipEditText.setText(ip);
        portEditText.setText(port);
        volumeTv.setText(String.format(getString(R.string.volume_format), String.valueOf(MyApplication.volumeManager.getVolume())));
        volumeSeekBar.setProgress(MyApplication.volumeManager.getVolume());
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

    @Override
    public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
        volumeTv.setText(String.format(getString(R.string.volume_format), String.valueOf(value)));
    }

    @Override
    public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
        MyApplication.volumeManager.setVolume(seekBar.getProgress());
    }

    public interface StatusCallback {
        void onConnect();

        void onError();
    }
}