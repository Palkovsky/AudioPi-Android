package com.example.andrzej.audiocontroller.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.adapters.ExploreRecyclerAdapter;
import com.example.andrzej.audiocontroller.config.Defaults;
import com.example.andrzej.audiocontroller.handlers.ExploreManager;
import com.example.andrzej.audiocontroller.interfaces.ExploreListener;
import com.example.andrzej.audiocontroller.interfaces.OnItemClickListener;
import com.example.andrzej.audiocontroller.models.ExploreItem;
import com.example.andrzej.audiocontroller.utils.Image;
import com.example.andrzej.audiocontroller.views.BackHandledFragment;
import com.example.andrzej.audiocontroller.views.BlankingImageButton;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Explore fragment contains list in filesystem and it
 * lets you to explore it and import playlists and tracks etc.
 */
public class ExploreFragment extends BackHandledFragment implements OnItemClickListener, View.OnClickListener, ExploreListener {

    public static final String TAG = "EXPLORE_FRAGMENT";

    //Objects
    private RecyclerView.LayoutManager manager;
    private ExploreRecyclerAdapter mAdapter;
    private ExploreManager exploreManager;

    //Datasets
    private List<ExploreItem> mDataset;

    //Vitals
    private boolean isGrid = true;

    //View bindings
    @Bind(R.id.exploreRecyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.expand_fab)
    FloatingActionsMenu parentFabBtn;
    @Bind(R.id.rootBtn)
    FloatingActionButton rootBtn;
    @Bind(R.id.changeViewBtn)
    FloatingActionButton changeViewBtn;
    @Bind(R.id.backBtn)
    BlankingImageButton backBtn;
    @Bind(R.id.currentPathHorizontalScrollView)
    HorizontalScrollView currentPathContainer;
    @Bind(R.id.currentPathTv)
    TextView currentPathTv;

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
        mDataset = generateDataset();

        //Config views
        updatePathToolbar();

        //Configure recycler view
        mRecyclerView.setHasFixedSize(true);
        setRecyclerType(true);


        //Listeners
        rootBtn.setOnClickListener(this);
        changeViewBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        exploreManager.setExploreListener(this);

        return rootView;
    }

    @Override
    public void onItemClick(View v, int position) {
        ExploreItem item = mDataset.get(position);

        if (item.isDirectory())
            exploreManager.goTo(exploreManager.currentPath() + item.getName() + "/");

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
            case R.id.backBtn:
                exploreManager.goUp();
                break;
        }
    }

    @Override
    public void onDirectoryUp(String oldPath, String newPath) {
        updatePathToolbar();
    }

    @Override
    public void onDirectoryDown(String oldPath, String newPath) {
        updatePathToolbar();
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

    private List<ExploreItem> generateDataset() {
        List<ExploreItem> list = new ArrayList<>();

        String[] name = {"Folder", "Folder 2", "Folder 3", "audio", "audio2", "Audio21", "geje", "cos", "sraka", "draka", "maka", "paka", "tini", "wini", "lala", "po", "a", "b", "c", "d", "e", "adgsgasdfgasrfgadsuignadiufndasufcnaydnfryufcdanyfcnaydncfyadncfydacfndascfindycfndiycfdinacifndycdfins", "inidie cidny"};
        String[] cover = {null, null, null, "http://neonlimelight.com/wp-content/uploads/2012/04/Maroon-5-Overexposed-Cover.jpg", "http://neonlimelight.com/wp-content/uploads/2012/04/Maroon-5-Overexposed-Cover.jpg", "http://www.smashingmagazine.com/images/music-cd-covers/27.jpg", null, null, "http://i.kinja-img.com/gawker-media/image/upload/a0gctnljoglp7mrx80us.gif", null, "http://ak-hdl.buzzfed.com/static/2013-10/enhanced/webdr06/3/17/anigif_enhanced-buzz-30164-1380836157-24.gif", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null};
        boolean[] directory = {true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};

        for (int i = 0; i < name.length; i++) {
            ExploreItem item = new ExploreItem();
            item.setName(name[i]);
            item.setDirectory(directory[i]);
            item.setCoverUrl(cover[i]);
            list.add(item);
        }

        return list;
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
        return false;
    }
}
