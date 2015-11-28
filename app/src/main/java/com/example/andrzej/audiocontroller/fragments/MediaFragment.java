package com.example.andrzej.audiocontroller.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.baoyz.widget.PullRefreshLayout;
import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.activities.DetalisActivity;
import com.example.andrzej.audiocontroller.adapters.MediaRecyclerAdapter;
import com.example.andrzej.audiocontroller.config.Codes;
import com.example.andrzej.audiocontroller.config.Endpoints;
import com.example.andrzej.audiocontroller.config.Filters;
import com.example.andrzej.audiocontroller.config.PrefKeys;
import com.example.andrzej.audiocontroller.config.Sort;
import com.example.andrzej.audiocontroller.handlers.MediaManager;
import com.example.andrzej.audiocontroller.interfaces.MediaCommunicator;
import com.example.andrzej.audiocontroller.interfaces.OnChildItemClickListener;
import com.example.andrzej.audiocontroller.interfaces.OnChildItemLongClickListener;
import com.example.andrzej.audiocontroller.interfaces.OnLongItemClickListener;
import com.example.andrzej.audiocontroller.interfaces.OnMoreChildItemClickListener;
import com.example.andrzej.audiocontroller.interfaces.OnRemove;
import com.example.andrzej.audiocontroller.interfaces.OnSuccess;
import com.example.andrzej.audiocontroller.models.Playlist;
import com.example.andrzej.audiocontroller.models.Track;
import com.example.andrzej.audiocontroller.models.dbmodels.PlaylistDb;
import com.example.andrzej.audiocontroller.models.dbmodels.TrackDb;
import com.example.andrzej.audiocontroller.utils.Converter;
import com.example.andrzej.audiocontroller.utils.DatabaseUtils;
import com.example.andrzej.audiocontroller.utils.Dialog;
import com.example.andrzej.audiocontroller.utils.network.Network;
import com.example.andrzej.audiocontroller.utils.network.VolleySingleton;
import com.example.andrzej.audiocontroller.views.BackHandledFragment;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

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
public class MediaFragment extends BackHandledFragment implements PullRefreshLayout.OnRefreshListener, View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    public static final String TAG = "MEDIA_FRAGMENT";
    public final static String SER_KEY = "com.example.andrzej.audiocontroller.fragments.MediaFragment";

    //Objects
    private MediaRecyclerAdapter mAdapter;
    private RequestQueue requestQueue;
    private SharedPreferences prefs;
    private MediaManager mediaManager;

    //Lists
    private List<Playlist> orginalPlaylists;
    private List<Playlist> mPlaylists;

    //Data
    private boolean isLoading;
    private String currentPath = "";
    private int filter;
    private int lastEditedPlaylistPosition = -1;

    //Interfaces
    private MediaCommunicator mediaCommunicator;

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
    @Bind(R.id.expand_fab)
    FloatingActionsMenu fabRoot;
    @Bind(R.id.filterBtn)
    FloatingActionButton filterBtn;
    @Bind(R.id.addPlaylistBtn)
    FloatingActionButton addPlaylistBtn;

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
        orginalPlaylists = new ArrayList<>();
        mPlaylists = new ArrayList<>();

        //Objects init
        requestQueue = VolleySingleton.getsInstance().getRequestQueue();
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mediaManager = new MediaManager();

        //Data
        filter = prefs.getInt(PrefKeys.KEY_MEDIA_FILTER, Filters.ALL);

        //Listener
        mSwipeRefreshLayout.setOnRefreshListener(this);
        filterBtn.setOnClickListener(this);
        addPlaylistBtn.setOnClickListener(this);

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
        if (fabRoot.isExpanded()) {
            fabRoot.collapse();
            return true;
        }
        return false;
    }

    public void setUpErrorLayout(int errorCode) {
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorContainer.setVisibility(View.VISIBLE);

        if (errorCode != Codes.SUCCESFULL) {
            switch (errorCode) {
                case Codes.INVALID_PATH:
                    mPlaylists.clear();
                    reInitRecycler();
                    mErrorTextView.setText(R.string.invalid_path_error);
                    break;
                case Codes.EMPTY_DATASET:
                    if (filter == Filters.LOCAL_PLAYLISTS && mPlaylists.size() > 0)
                        setUpNormalLayout();
                    else
                        mErrorTextView.setText(R.string.no_playlists_error);
                    break;
                case Codes.NO_ALBUMS:
                    mErrorTextView.setText(R.string.no_albums_error);
                    break;
                case Codes.NO_ARTISTS:
                    mErrorTextView.setText(R.string.no_artists_error);
                    break;
                case Codes.NO_GENRES:
                    mErrorTextView.setText(R.string.no_genres_error);
                    break;
                case Codes.NO_LOCAL_PLAYLISTS:
                    mErrorTextView.setText(R.string.no_local_playlist_error);
                    break;
            }
        } else {
            mPlaylists.clear();
            if (filter != Filters.LOCAL_PLAYLISTS) {
                reInitRecycler();
                if (!Network.isNetworkAvailable(getActivity()))
                    mErrorTextView.setText(R.string.no_internet_error);
                else
                    mErrorTextView.setText(R.string.server_error);
            } else {
                filterDataset(filter);
                setUpNormalLayout();
            }
        }
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

    private void reInitRecycler() {
        mAdapter = new MediaRecyclerAdapter(getActivity(), mPlaylists);
        mRecyclerView.setAdapter(null);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.swapAdapter(mAdapter, true);
        mAdapter.setOnLongItemClickListener(new OnLongItemClickListener() {
            @Override
            public void onLongItemClick(View v, int position) {
                lastEditedPlaylistPosition = position;
                Intent intent = new Intent(getActivity(), DetalisActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(SER_KEY, mPlaylists.get(position));
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
            }
        });
        mAdapter.setOnTrackClickListener(new OnChildItemClickListener() {
            @Override
            public void onChildItemClick(View v, int position, int internalPos, Object obj) {
                if (mediaCommunicator != null && !isLoading)
                    mediaCommunicator.onPlaylistStart((Playlist) obj, internalPos);
            }
        });
        mAdapter.setOnTrackLongClickListener(new OnChildItemLongClickListener() {
            @Override
            public void onChildLongClickListener(View v, int position, final int parentPos, final int internalPos, Object obj) {


                Track item = (Track) obj;
                Dialog.showTrackMediaDialog(getActivity(), item, new OnSuccess() {
                    @Override
                    public void onSuccess() {
                        //filterDataset(filter);
                    }
                }, new OnRemove() {
                    @Override
                    public void onRemove() {
                        mPlaylists.get(parentPos).getTracks().remove(internalPos);
                        mAdapter.notifyChildItemRemoved(parentPos, internalPos);
                        mAdapter.notifyParentItemChanged(parentPos);
                    }
                });

            }
        });
        mAdapter.setOnMoreTrackItemClickListener(new OnMoreChildItemClickListener() {
            @Override
            public void onMoreChildItemClick(View v, int position, final int parentPos, final int internalPos, Object obj) {
                Track item = (Track) obj;
                Dialog.showTrackMediaDialog(getActivity(), item, new OnSuccess() {
                    @Override
                    public void onSuccess() {
                        //filterDataset(filter);
                    }
                }, new OnRemove() {
                    @Override
                    public void onRemove() {
                        mPlaylists.get(parentPos).getTracks().remove(internalPos);
                        mAdapter.notifyChildItemRemoved(parentPos, internalPos);
                        mAdapter.notifyParentItemChanged(parentPos);
                    }
                });
            }
        });
    }


    @Override
    public void onRefresh() {
        if (!isLoading)
            queryPath(currentPath);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.filterBtn:
                showFilterContextMenu(v);
                break;
            case R.id.addPlaylistBtn:
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.add_new_playlist_dialog_title)
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input(R.string.input_playlist_name_hint, R.string.input_prefill, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                PlaylistDb newPlaylist = new PlaylistDb(input.toString());
                                newPlaylist.save();
                                prefs.edit().putInt(PrefKeys.KEY_MEDIA_FILTER, Filters.LOCAL_PLAYLISTS).apply();
                                if (filter != Filters.LOCAL_PLAYLISTS) {
                                    filter = Filters.LOCAL_PLAYLISTS;
                                    filterDataset(filter);
                                } else {
                                    mPlaylists.add(Converter.dbToStandard(newPlaylist));
                                    mAdapter.notifyParentItemInserted(mPlaylists.size() - 1);
                                    setUpNormalLayout();
                                }
                                fabRoot.collapse();
                            }
                        }).show();
                break;
        }
    }

    public void queryPath(String path) {

        requestQueue.cancelAll(TAG);

        currentPath = path;
        isLoading = true;
        setUpLoadingLayout();
        String queryUrl = Endpoints.getPlaylistsUrl(path, true, Sort.NONE);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, queryUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                int code = Codes.SUCCESFULL;
                orginalPlaylists.clear();
                mPlaylists.clear();

                try {
                    code = response.getInt("code");

                    JSONArray playlists = response.getJSONArray("playlists");
                    for (int i = 0; i < playlists.length(); i++) {

                        JSONObject playlist = playlists.getJSONObject(i);
                        Playlist item = new Playlist();
                        item.setCoverUrl(Endpoints.getFileUrl(playlist.getString("cover")));
                        item.setName(playlist.getString("name"));
                        item.setType(playlist.getString("type"));

                        JSONArray jsonTracks = playlist.getJSONArray("tracks");
                        List<Track> tracks = new ArrayList<>();

                        for (int j = 0; j < jsonTracks.length(); j++) {
                            JSONObject track = jsonTracks.getJSONObject(j);
                            Track exploreItem = new Track();
                            exploreItem.setName(track.getString("basename"));
                            exploreItem.setPath(track.getString("full"));
                            exploreItem.setJSONMetadata(track);
                            tracks.add(exploreItem);
                        }

                        item.setTracks(tracks);
                        orginalPlaylists.add(item);
                    }

                    if (orginalPlaylists.size() == 0)
                        setUpErrorLayout(Codes.EMPTY_DATASET);
                    else
                        setUpNormalLayout();

                    filterDataset(filter);

                    reInitRecycler();
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

    public void registerMediaCommunicator(MediaCommunicator mediaCommunicator) {
        this.mediaCommunicator = mediaCommunicator;
    }

    private void showFilterContextMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.inflate(R.menu.filter_popup);
        Menu menu = popupMenu.getMenu();

        switch (filter) {
            case Filters.ALL:
                disableOptions(menu, R.id.allAllowed);
                break;
            case Filters.ALBUMS:
                disableOptions(menu, R.id.albumsOnly);
                break;
            case Filters.ARTISTS:
                disableOptions(menu, R.id.artistsOnly);
                break;
            case Filters.GENRES:
                disableOptions(menu, R.id.genresOnly);
                break;
            case Filters.LOCAL_PLAYLISTS:
                disableOptions(menu, R.id.playlistsOnly);
                break;
        }

        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    private void disableOptions(Menu menu, int disableResId) {
        menu.findItem(R.id.allAllowed).setEnabled(true);
        menu.findItem(R.id.artistsOnly).setEnabled(true);
        menu.findItem(R.id.albumsOnly).setEnabled(true);
        menu.findItem(R.id.genresOnly).setEnabled(true);
        menu.findItem(R.id.playlistsOnly).setEnabled(true);
        menu.findItem(disableResId).setEnabled(false);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        SharedPreferences.Editor editor = prefs.edit();

        switch (item.getItemId()) {
            case R.id.allAllowed:
                editor.putInt(PrefKeys.KEY_MEDIA_FILTER, Filters.ALL);
                filter = Filters.ALL;
                break;
            case R.id.albumsOnly:
                editor.putInt(PrefKeys.KEY_MEDIA_FILTER, Filters.ALBUMS);
                filter = Filters.ALBUMS;
                break;
            case R.id.artistsOnly:
                editor.putInt(PrefKeys.KEY_MEDIA_FILTER, Filters.ARTISTS);
                filter = Filters.ARTISTS;
                break;
            case R.id.genresOnly:
                editor.putInt(PrefKeys.KEY_MEDIA_FILTER, Filters.GENRES);
                filter = Filters.GENRES;
                break;
            case R.id.playlistsOnly:
                editor.putInt(PrefKeys.KEY_MEDIA_FILTER, Filters.LOCAL_PLAYLISTS);
                filter = Filters.LOCAL_PLAYLISTS;
                break;
        }

        filterDataset(filter);
        editor.apply();
        fabRoot.collapse();
        return false;
    }

    public void filterDataset(int filt) {
        prefs.edit().putInt(PrefKeys.KEY_MEDIA_FILTER, filt).apply();
        filter = filt;
        List<Playlist> filteredPlaylist = mediaManager.applyFilter(filt, orginalPlaylists);
        mPlaylists.clear();
        mPlaylists.addAll(filteredPlaylist);
        if (mPlaylists.size() == 0) {
            switch (filt) {
                default:
                case Filters.ALL:
                    setUpErrorLayout(Codes.EMPTY_DATASET);
                    break;
                case Filters.GENRES:
                    setUpErrorLayout(Codes.NO_GENRES);
                    break;
                case Filters.ARTISTS:
                    setUpErrorLayout(Codes.NO_ARTISTS);
                    break;
                case Filters.ALBUMS:
                    setUpErrorLayout(Codes.NO_ALBUMS);
                    break;
                case Filters.LOCAL_PLAYLISTS:
                    for (PlaylistDb playlistDb : PlaylistDb.getAll())
                        mPlaylists.add(Converter.dbToStandard(playlistDb));
                    if (mPlaylists.size() > 0)
                        setUpNormalLayout();
                    else
                        setUpErrorLayout(Codes.NO_LOCAL_PLAYLISTS);
                    break;
            }
        } else
            setUpNormalLayout();
        reInitRecycler();
    }

    public void handlePositionChange(Integer[] positions) {
        if (filter == Filters.LOCAL_PLAYLISTS) {

            mPlaylists.clear();
            for (PlaylistDb playlistDb : PlaylistDb.getAll())
                mPlaylists.add(Converter.dbToStandard(playlistDb));

            for (int i = 0; i < mPlaylists.size(); i++) {
                for (int j = 0; j < mPlaylists.get(i).getTracks().size(); j++)
                    mAdapter.notifyChildItemChanged(i, j);
                mAdapter.notifyParentItemChanged(i);
            }

            mAdapter.notifyChildItemChanged(lastEditedPlaylistPosition, positions[0]);
            mAdapter.notifyChildItemChanged(lastEditedPlaylistPosition, positions[1]);
            mAdapter.notifyParentItemChanged(lastEditedPlaylistPosition);
        }
    }

    public void handleTrackDelete(Integer pos) {

        List<Track> tracks = mPlaylists.get(lastEditedPlaylistPosition).getTracks();

        if (filter == Filters.LOCAL_PLAYLISTS && lastEditedPlaylistPosition != -1
                && tracks.size() > 0) {

            tracks.remove(pos.intValue());
            mAdapter.notifyChildItemRemoved(lastEditedPlaylistPosition, pos);
            mAdapter.notifyParentItemChanged(lastEditedPlaylistPosition);
        }
    }

    public void handlePlaylistDelete() {
        if (filter == Filters.LOCAL_PLAYLISTS) {
            mPlaylists.remove(lastEditedPlaylistPosition);
            mAdapter.notifyParentItemRemoved(lastEditedPlaylistPosition);

            if (mPlaylists.size() == 0)
                setUpErrorLayout(Codes.NO_LOCAL_PLAYLISTS);
        }
    }

    public void handleTrackAppend(Integer position, Track track) {
        if (filter == Filters.LOCAL_PLAYLISTS) {
            mPlaylists.get(lastEditedPlaylistPosition).getTracks().add(position, track);
            mAdapter.notifyChildItemInserted(lastEditedPlaylistPosition, position);
        }
    }

    public int getFilter() {
        return filter;
    }
}