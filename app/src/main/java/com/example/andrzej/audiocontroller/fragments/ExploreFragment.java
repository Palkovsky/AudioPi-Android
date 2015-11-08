package com.example.andrzej.audiocontroller.fragments;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
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
import com.example.andrzej.audiocontroller.adapters.ExploreRecyclerAdapter;
import com.example.andrzej.audiocontroller.config.Codes;
import com.example.andrzej.audiocontroller.config.Defaults;
import com.example.andrzej.audiocontroller.config.Endpoints;
import com.example.andrzej.audiocontroller.handlers.ExploreManager;
import com.example.andrzej.audiocontroller.interfaces.ExploreListener;
import com.example.andrzej.audiocontroller.interfaces.OnItemClickListener;
import com.example.andrzej.audiocontroller.models.ExploreItem;
import com.example.andrzej.audiocontroller.utils.Image;
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
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Explore fragment contains list in filesystem and it
 * lets you to explore it and import playlists and tracks etc.
 */
public class ExploreFragment extends BackHandledFragment implements OnItemClickListener, View.OnClickListener, ExploreListener, PullRefreshLayout.OnRefreshListener {

    public static final String TAG = "EXPLORE_FRAGMENT";

    //Objects
    private RecyclerView.LayoutManager manager;
    private ExploreRecyclerAdapter mAdapter;
    private ExploreManager exploreManager;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;

    //Datasets
    private List<ExploreItem> mDataset;

    //Vitals
    private boolean isGrid = true;
    private boolean isLoading = false;

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
    @Bind(R.id.backBtn)
    BlankingImageButton backBtn;
    @Bind(R.id.currentPathHorizontalScrollView)
    HorizontalScrollView currentPathContainer;
    @Bind(R.id.currentPathTv)
    TextView currentPathTv;

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

        mDataset = new ArrayList<>();

        //Config views
        updatePathToolbar();

        //Configure recycler view
        mRecyclerView.setHasFixedSize(true);
        setRecyclerType(true);

        queryPath(exploreManager.currentPath());

        //Listeners
        rootBtn.setOnClickListener(this);
        changeViewBtn.setOnClickListener(this);
        sortBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        exploreManager.setExploreListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);

        return rootView;
    }

    @Override
    public void onItemClick(View v, int position) {
        ExploreItem item = mDataset.get(position);

        if (item.isDirectory() && !isLoading) {
            exploreManager.goTo(exploreManager.currentPath() + item.getName() + "/");
            mRecyclerView.scrollToPosition(0);
        }

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
                Toast.makeText(getActivity(), "Sorting activities", Toast.LENGTH_SHORT).show();
                break;
            case R.id.backBtn:
                exploreManager.goUp();
                break;
        }
    }

    @Override
    public void onDirectoryUp(String oldPath, String newPath) {
        if (!isLoading) {
            mRecyclerView.scrollToPosition(0);
            queryPath(newPath);
        }
    }

    @Override
    public void onDirectoryDown(String oldPath, String newPath) {
        if (!isLoading) {
            mRecyclerView.scrollToPosition(0);
            queryPath(newPath);
        }
    }

    private void updatePathToolbar() {
        if (exploreManager.canGoUp())
            backBtn.setEnabled(true);
        else
            backBtn.setEnabled(false);

        currentPathTv.setText(exploreManager.currentPath());
        currentPathContainer.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentPathContainer.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }, 100);

        swipeRefreshLayout.setRefreshing(false);
    }

    private void setRecyclerType(boolean grid) {

        mRecyclerView.swapAdapter(null, true);

        if (grid) {
            manager = new GridLayoutManager(getActivity(), 3);
            mAdapter = new ExploreRecyclerAdapter(getActivity(), mDataset, R.layout.explore_item_grid);
            mAdapter.setOnItemClickListener(this);
            changeViewBtn.setIconDrawable(Image.getDrawable(getActivity(), R.drawable.ic_grid_off_white_36dp));
        } else {
            manager = new LinearLayoutManager(getActivity());
            mAdapter = new ExploreRecyclerAdapter(getActivity(), mDataset, R.layout.explore_item_list);
            mAdapter.setOnItemClickListener(this);
            changeViewBtn.setIconDrawable(Image.getDrawable(getActivity(), R.drawable.ic_grid_on_white_36dp));
        }

        mRecyclerView.setLayoutManager(manager);
        if (mRecyclerView.getAdapter() == null)
            mRecyclerView.setAdapter(mAdapter);
        else {
            mRecyclerView.swapAdapter(mAdapter, true);
        }

        isGrid = grid;
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
            String queryUrl = Endpoints.getDataUrl(path, true);

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
                        if (mDataset.size() == 0)
                            setUpErrorLayout(Codes.EMPTY_DATASET);
                        else
                            setNormalLayout();
                        updatePathToolbar();

                        isLoading = false;

                    } catch (JSONException e) {
                        e.printStackTrace();
                        setUpErrorLayout(code);
                        updatePathToolbar();
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
                    isLoading = false;
                }
            });

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
        queryPath(exploreManager.currentPath());
    }
}
