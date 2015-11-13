package com.example.andrzej.audiocontroller.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
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
import com.example.andrzej.audiocontroller.adapters.ExploreRecyclerAdapter;
import com.example.andrzej.audiocontroller.adapters.NavigationRecyclerView;
import com.example.andrzej.audiocontroller.config.Codes;
import com.example.andrzej.audiocontroller.config.Defaults;
import com.example.andrzej.audiocontroller.config.Endpoints;
import com.example.andrzej.audiocontroller.config.PrefKeys;
import com.example.andrzej.audiocontroller.config.Sort;
import com.example.andrzej.audiocontroller.handlers.ExploreManager;
import com.example.andrzej.audiocontroller.interfaces.ExploreFragmentCommunicator;
import com.example.andrzej.audiocontroller.interfaces.ExploreListener;
import com.example.andrzej.audiocontroller.interfaces.OnItemClickListener;
import com.example.andrzej.audiocontroller.interfaces.OnLongItemClickListener;
import com.example.andrzej.audiocontroller.interfaces.OnMoreItemClickListener;
import com.example.andrzej.audiocontroller.models.ExploreItem;
import com.example.andrzej.audiocontroller.models.Metadata;
import com.example.andrzej.audiocontroller.utils.Dialog;
import com.example.andrzej.audiocontroller.utils.Image;
import com.example.andrzej.audiocontroller.utils.network.Downloader;
import com.example.andrzej.audiocontroller.utils.network.Network;
import com.example.andrzej.audiocontroller.utils.network.VolleySingleton;
import com.example.andrzej.audiocontroller.views.BackHandledFragment;
import com.example.andrzej.audiocontroller.views.BlankingImageButton;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Explore fragment contains list in filesystem and it
 * lets you to explore it and import playlists and tracks etc.
 */
public class ExploreFragment extends BackHandledFragment implements OnItemClickListener, View.OnClickListener, ExploreListener, PullRefreshLayout.OnRefreshListener, PopupMenu.OnMenuItemClickListener, OnLongItemClickListener, OnMoreItemClickListener {

    public static final String TAG = "EXPLORE_FRAGMENT";

    //Objects
    private RecyclerView.LayoutManager manager;
    private ExploreRecyclerAdapter mAdapter;
    private NavigationRecyclerView navigationAdapter;
    private ExploreManager exploreManager;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private SharedPreferences prefs;

    //Communicator interface
    private ExploreFragmentCommunicator communicator;

    //Datasets
    private List<ExploreItem> mDataset;
    private List<String> navigationDataset;

    //Vitals
    private boolean isGrid = true;
    private boolean isLoading = false;
    private int sortingMethod;

    //View bindings
    @Bind(R.id.swipeRefreshLayout)
    PullRefreshLayout swipeRefreshLayout;
    @Bind(R.id.exploreRecyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.exploreProgressBar)
    SmoothProgressBar progressBar;
    @Bind(R.id.expand_fab)
    FloatingActionsMenu parentFabBtn;
    @Bind(R.id.rootBtn)
    FloatingActionButton rootBtn;
    @Bind(R.id.sortBtn)
    FloatingActionButton sortBtn;
    @Bind(R.id.changeViewBtn)
    FloatingActionButton changeViewBtn;
    @Bind(R.id.directoryNavigationRecycler)
    RecyclerView navigationRecyclerView;

    @Bind(R.id.errorContainer)
    LinearLayout errorContainer;
    @Bind(R.id.errorImageView)
    ImageView errorImageView;
    @Bind(R.id.errorTextView)
    TextView errorTextView;

    public ExploreFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_explore, container, false);
        ButterKnife.bind(this, rootView);

        exploreManager = new ExploreManager(Defaults.PATH);
        volleySingleton = VolleySingleton.getsInstance();
        requestQueue = volleySingleton.getRequestQueue();
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        isGrid = prefs.getBoolean(PrefKeys.KEY_EXPLORE_VIEW, true);
        sortingMethod = prefs.getInt(PrefKeys.KEY_EXPLORE_SORT, Sort.NONE);

        mDataset = new ArrayList<>();
        navigationDataset = new ArrayList<>();

        //Configure navigation recycler view
        navigationRecyclerView.setHasFixedSize(true);
        navigationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        navigationAdapter = new NavigationRecyclerView(navigationDataset);
        navigationAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                int currentPos = exploreManager.getDepth();

                while (currentPos - 1 > position && exploreManager.canGoUp()) {
                    exploreManager.goUp(false);
                    currentPos = exploreManager.getDepth();
                }

                if (!isLoading)
                    queryPath(exploreManager.currentPath());
            }
        });
        navigationRecyclerView.setAdapter(navigationAdapter);
        updatePathToolbar();

        //Configure recycler view
        mRecyclerView.setHasFixedSize(true);
        setRecyclerType(isGrid);

        queryPath(exploreManager.currentPath());

        //Listeners
        rootBtn.setOnClickListener(this);
        changeViewBtn.setOnClickListener(this);
        sortBtn.setOnClickListener(this);
        exploreManager.setExploreListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);

        return rootView;
    }

    @Override
    public void onItemClick(View v, int position) {
        ExploreItem item = mDataset.get(position);

        if (item.isDirectory() && !isLoading) {
            exploreManager.currentDirectory().setSavedState(mRecyclerView.getLayoutManager().onSaveInstanceState());
            exploreManager.currentDirectory().setItems(mDataset);
            exploreManager.goTo(exploreManager.currentPath() + item.getName() + "/");
        }
    }

    @Override
    public void onLongItemClick(View v, int position) {
        ExploreItem item = mDataset.get(position);

        if (!item.isDirectory())
            showFileDialog(item);
    }

    @Override
    public void onMoreClick(View v, int position) {
        ExploreItem item = mDataset.get(position);
        if (!item.isDirectory())
            showFileDialog(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rootBtn:
                exploreManager.goToRoot();
                parentFabBtn.collapse();
                break;
            case R.id.changeViewBtn:
                setRecyclerType(!isGrid);
                parentFabBtn.collapse();
                break;
            case R.id.sortBtn:
                PopupMenu popupMenu = new PopupMenu(getActivity(), v);
                popupMenu.inflate(R.menu.sort_popup);
                Menu menu = popupMenu.getMenu();

                //Remove current sorting method from popup menu
                switch (prefs.getInt(PrefKeys.KEY_EXPLORE_SORT, Sort.NONE)) {

                    case Sort.NONE:
                        menu.findItem(R.id.item_noSort).setEnabled(false);
                        menu.findItem(R.id.item_AZ).setEnabled(true);
                        menu.findItem(R.id.item_ZA).setEnabled(true);
                        break;
                    case Sort.ALPHABETICALLY_ASC:
                        menu.findItem(R.id.item_noSort).setEnabled(true);
                        menu.findItem(R.id.item_AZ).setEnabled(false);
                        menu.findItem(R.id.item_ZA).setEnabled(true);
                        break;
                    case Sort.ALPHABETICALLY_DESC:
                        menu.findItem(R.id.item_noSort).setEnabled(true);
                        menu.findItem(R.id.item_AZ).setEnabled(true);
                        menu.findItem(R.id.item_ZA).setEnabled(false);
                        break;
                }

                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.show();
                break;
        }
    }

    @Override
    public void onDirectoryUp(String oldPath, String newPath) {
        if (!isLoading)
            queryPath(newPath);

    }

    @Override
    public void onDirectoryDown(String oldPath, String newPath) {
        if (!isLoading) {
            queryPath(newPath);
        }
    }

    private void updatePathToolbar() {

        String currentPath = exploreManager.currentPath();
        List<String> parts = new LinkedList<>(Arrays.asList(currentPath.substring(1).split("/")));

        parts.remove(0);
        parts.remove(0);
        parts.add(0, getActivity().getString(R.string.root));


        for (int i = 0; i < parts.size(); i++)
            parts.set(i, parts.get(i).toUpperCase());


        navigationDataset.clear();
        navigationDataset.addAll(parts);
        navigationAdapter.notifyDataSetChanged();
        navigationRecyclerView.scrollToPosition(parts.size() - 1);
    }


    private void setRecyclerType(boolean grid) {

        mRecyclerView.swapAdapter(null, true);

        if (grid) {
            manager = new GridLayoutManager(getActivity(), 3);
            mAdapter = new ExploreRecyclerAdapter(getActivity(), mDataset, R.layout.explore_item_grid);
            mAdapter.setOnItemClickListener(this);
            mAdapter.setOnLongItemClickListener(this);
            changeViewBtn.setIconDrawable(Image.getDrawable(getActivity(), R.drawable.ic_grid_off_white_36dp));
        } else {
            manager = new LinearLayoutManager(getActivity());
            mAdapter = new ExploreRecyclerAdapter(getActivity(), mDataset, R.layout.explore_item_list);
            mAdapter.setOnItemClickListener(this);
            mAdapter.setOnLongItemClickListener(this);
            mAdapter.setOnMoreItemClickListener(this);
            changeViewBtn.setIconDrawable(Image.getDrawable(getActivity(), R.drawable.ic_grid_on_white_36dp));
        }

        mRecyclerView.setLayoutManager(manager);
        if (mRecyclerView.getAdapter() == null)
            mRecyclerView.setAdapter(mAdapter);
        else {
            mRecyclerView.swapAdapter(mAdapter, true);
        }

        isGrid = grid;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(PrefKeys.KEY_EXPLORE_VIEW, isGrid);
        editor.apply();
    }

    private void setLoadingLayout() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void setUpErrorLayout(int errorCode) {
        progressBar.setVisibility(View.GONE);
        errorContainer.setVisibility(View.VISIBLE);

        if (errorCode != Codes.SUCCESFULL) {
            switch (errorCode) {
                case Codes.INVALID_PATH:
                    mDataset.clear();
                    mAdapter.notifyDataSetChanged();
                    errorTextView.setText(R.string.invalid_path_error);
                    break;
                case Codes.EMPTY_DATASET:
                    errorTextView.setText(R.string.dataset_empty_error);
                    break;
            }
        } else {

            if (!Network.isNetworkAvailable(getActivity()))
                errorTextView.setText(R.string.no_internet_error);
            else
                errorTextView.setText(R.string.server_error);


        }

    }

    private void setNormalLayout() {
        progressBar.setVisibility(View.GONE);
        errorContainer.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void queryPath(String path) {

        if (!isLoading) {
            isLoading = true;
            requestQueue.cancelAll(TAG);

            setLoadingLayout();
            final String queryUrl = Endpoints.getDataUrl(path, true, sortingMethod);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, queryUrl, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    int code = Codes.SUCCESFULL;

                    try {
                        code = response.getInt("code");

                        List<ExploreItem> dataset = new ArrayList<>();
                        JSONObject directory = response.getJSONObject("directory");
                        JSONArray directories = directory.getJSONArray("directories");
                        JSONArray tracks = directory.getJSONArray("tracks");

                        for (int i = 0; i < directories.length(); i++) {
                            JSONObject dir = directories.getJSONObject(i);
                            ExploreItem item = new ExploreItem();
                            item.setName(dir.getString("relative"));
                            item.setPath(dir.getString("full"));
                            item.setDirectory(true);
                            dataset.add(item);
                        }

                        for (int i = 0; i < tracks.length(); i++) {
                            JSONObject track = tracks.getJSONObject(i);
                            JSONObject metadata = track.getJSONObject("metadata");
                            ExploreItem item = new ExploreItem();
                            item.setName(track.getString("relative"));
                            item.setPath(track.getString("full"));
                            item.setJSONMetadata(metadata);
                            item.setDirectory(false);
                            dataset.add(item);
                        }

                        mDataset.clear();
                        mDataset.addAll(dataset);
                        mAdapter.notifyDataSetChanged();
                        if (mDataset.size() == 0) {
                            setUpErrorLayout(Codes.EMPTY_DATASET);
                            if (communicator != null)
                                communicator.onQueryError(queryUrl, Codes.EMPTY_DATASET);
                        } else {
                            exploreManager.currentDirectory().setItems(mDataset);
                            setNormalLayout();
                            if (communicator != null)
                                communicator.onQuerySuccess(queryUrl, response);
                        }
                        updatePathToolbar();

                        swipeRefreshLayout.setRefreshing(false);

                        mRecyclerView.scrollToPosition(0);
                        if (exploreManager.currentDirectory().getSavedState() != null)
                            mRecyclerView.getLayoutManager().onRestoreInstanceState(exploreManager.currentDirectory().getSavedState());

                        isLoading = false;

                    } catch (JSONException e) {
                        e.printStackTrace();
                        setUpErrorLayout(code);
                        if (communicator != null)
                            communicator.onQueryError(queryUrl, code);
                        updatePathToolbar();
                        swipeRefreshLayout.setRefreshing(false);
                        isLoading = false;
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mDataset.clear();
                    mAdapter.notifyDataSetChanged();
                    setUpErrorLayout(Codes.SUCCESFULL);
                    updatePathToolbar();
                    if (communicator != null)
                        communicator.onQueryError(queryUrl, Codes.SUCCESFULL);
                    swipeRefreshLayout.setRefreshing(false);
                    isLoading = false;
                }
            });

            if (communicator != null)
                communicator.onQueryStart(queryUrl);

            request.setTag(TAG);
            requestQueue.add(request);
        }
    }

    @Override
    public String getTagText() {
        return TAG;
    }

    @Override
    public boolean onBackPressed() {
        if (parentFabBtn.isExpanded()) {
            parentFabBtn.collapse();
            return true;
        }
        if (exploreManager.canGoUp()) {
            if (!isLoading)
                exploreManager.goUp();
            return true;
        }
        return false;
    }

    @Override
    public void onRefresh() {
        exploreManager.currentDirectory().setSavedState(null);
        queryPath(exploreManager.currentPath());
    }

    //This handles every popup menu in this fragment(currently and probably finally)
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        SharedPreferences.Editor editor = prefs.edit();
        switch (item.getItemId()) {
            case R.id.item_noSort:
                editor.putInt(PrefKeys.KEY_EXPLORE_SORT, Sort.NONE);
                sortingMethod = Sort.NONE;
                parentFabBtn.collapse();
                break;
            case R.id.item_AZ:
                editor.putInt(PrefKeys.KEY_EXPLORE_SORT, Sort.ALPHABETICALLY_ASC);
                sortingMethod = Sort.ALPHABETICALLY_ASC;
                parentFabBtn.collapse();
                break;
            case R.id.item_ZA:
                editor.putInt(PrefKeys.KEY_EXPLORE_SORT, Sort.ALPHABETICALLY_DESC);
                sortingMethod = Sort.ALPHABETICALLY_DESC;
                parentFabBtn.collapse();
                break;
        }

        if (!isLoading && Network.isNetworkAvailable(getActivity()))
            queryPath(exploreManager.currentPath());

        editor.apply();
        return false;
    }

    public void registerCommunicator(ExploreFragmentCommunicator communicator) {
        this.communicator = communicator;
    }

    private void showFileDialog(final ExploreItem item) {
        new MaterialDialog.Builder(getActivity())
                .title(item.getName())
                .items(R.array.explore_file_dialog_items)
                .negativeText(R.string.back)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {

                        switch (i) {
                            case 0: //Download
                                Downloader.downloadFile(getActivity(), item);
                                break; //Add to playlist
                            case 1:
                                //Here I'll show current local playlists list
                                break;
                            case 2: //Add to metadata
                                Dialog.showMetadataDialog(getActivity(), item);
                                break;
                        }
                    }
                }).show();
    }


}
