package com.example.andrzej.audiocontroller.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.baoyz.widget.PullRefreshLayout;
import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.adapters.MediaRecyclerAdapter;
import com.example.andrzej.audiocontroller.config.Codes;
import com.example.andrzej.audiocontroller.config.Endpoints;
import com.example.andrzej.audiocontroller.config.Sort;
import com.example.andrzej.audiocontroller.interfaces.OnChildItemClickListener;
import com.example.andrzej.audiocontroller.interfaces.OnChildItemLongClickListener;
import com.example.andrzej.audiocontroller.interfaces.OnMoreChildItemClickListener;
import com.example.andrzej.audiocontroller.models.ExploreItem;
import com.example.andrzej.audiocontroller.models.Playlist;
import com.example.andrzej.audiocontroller.utils.network.VolleySingleton;
import com.example.andrzej.audiocontroller.views.BackHandledFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Media fragment contains list of mPlaylists in current dir, tracks
 * and content of mPlaylists(tracks). This last functionality might be
 * outsorced somewhere else.
 */
public class MediaFragment extends BackHandledFragment implements PullRefreshLayout.OnRefreshListener {

    public static final String TAG = "MEDIA_FRAGMENT";

    //Objects
    private MediaRecyclerAdapter mAdapter;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;

    //Lists
    private List<Playlist> mPlaylists;

    //Data
    private boolean isLoading;
    private String currentPath = "";

    //View bindings
    @Bind(R.id.mediaRecyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.swipeRefreshLayout)
    PullRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.mediaProgressBar)
    SmoothProgressBar mProgressBar;
    @Bind(R.id.errorContainer)
    LinearLayout mErrorContainer;
    @Bind(R.id.errorTextView)
    TextView mErrorTextView;
    @Bind(R.id.errorImageView)
    ImageView mErrorImageView;

    public MediaFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_media, container, false);
        ButterKnife.bind(this, rootView);

        //Array inits
        mPlaylists = new ArrayList<>();

        //Objects init
        volleySingleton = VolleySingleton.getsInstance();
        requestQueue = volleySingleton.getRequestQueue();

        //Listener
        mSwipeRefreshLayout.setOnRefreshListener(this);

        //Recycler config
        reInitRecycler();

        setUpNormalLayout();

        return rootView;
    }

    @Override
    public String getTagText() {
        return TAG;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    private void setUpErrorLayout(int code) {
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorContainer.setVisibility(View.VISIBLE);
    }

    private void setUpNormalLayout() {
        mProgressBar.setVisibility(View.GONE);
        mErrorContainer.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void setUpLoadingLayout() {
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void reInitRecycler(){
        mAdapter = new MediaRecyclerAdapter(getActivity(), mPlaylists);
        mRecyclerView.setAdapter(null);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.swapAdapter(mAdapter, true);
        mAdapter.setOnTrackClickListener(new OnChildItemClickListener() {
            @Override
            public void onChildItemClick(View v, int position, Object obj) {
                ExploreItem item = (ExploreItem) obj;
                Toast.makeText(getActivity(), "POS: " + position + " - " + item.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        mAdapter.setOnTrackLongClickListener(new OnChildItemLongClickListener() {
            @Override
            public void onChildLongClickListener(View v, int position, Object obj) {
                ExploreItem item = (ExploreItem) obj;
                Toast.makeText(getActivity(), "POKAZ MENIU | POS: " + position + " - " + item.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        mAdapter.setOnMoreTrackItemClickListener(new OnMoreChildItemClickListener() {
            @Override
            public void onMoreChildItemClick(View v, int position, Object obj) {
                ExploreItem item = (ExploreItem) obj;
                Toast.makeText(getActivity(), "POKAZ MENIU | POS: " + position + " - " + item.getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onRefresh() {
        if(!isLoading)
            queryPath(currentPath);
    }

    public void queryPath(String path) {
        if (!isLoading) {

            currentPath = path;
            isLoading = true;
            setUpLoadingLayout();
            String queryUrl = Endpoints.getPlaylistsUrl(path, true, Sort.NONE);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, queryUrl, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    int code = Codes.SUCCESFULL;
                    mPlaylists.clear();

                    try {
                        code = response.getInt("code");

                        JSONArray playlists = response.getJSONArray("playlists");
                        for(int i = 0; i < playlists.length(); i++){

                            JSONObject playlist = playlists.getJSONObject(i);
                            Playlist item = new Playlist();
                            item.setCoverUrl(Endpoints.getFileUrl(playlist.getString("cover")));
                            item.setName(playlist.getString("name"));

                            JSONArray jsonTracks = playlist.getJSONArray("tracks");
                            List<ExploreItem> tracks = new ArrayList<>();

                            for(int j = 0; j < jsonTracks.length(); j++){
                                JSONObject track = jsonTracks.getJSONObject(j);
                                ExploreItem exploreItem = new ExploreItem();
                                exploreItem.setDirectory(false);
                                exploreItem.setName(track.getString("basename"));
                                exploreItem.setPath(track.getString("full"));
                                exploreItem.setJSONMetadata(track);
                                tracks.add(exploreItem);
                            }

                            item.setTracks(tracks);
                            mPlaylists.add(item);
                        }

                        reInitRecycler();
                        setUpNormalLayout();
                        mSwipeRefreshLayout.setRefreshing(false);
                        isLoading = false;

                    } catch (JSONException e) {
                        e.printStackTrace();
                        setUpErrorLayout(code);
                        mSwipeRefreshLayout.setRefreshing(false);
                        isLoading = false;
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mPlaylists.clear();
                    mAdapter.notifyDataSetChanged();
                    setUpErrorLayout(Codes.SUCCESFULL);
                    mSwipeRefreshLayout.setRefreshing(false);
                    isLoading = false;
                }
            });

            request.setTag(TAG);
            requestQueue.add(request);
        }
    }
}