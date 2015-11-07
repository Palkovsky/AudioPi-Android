package com.example.andrzej.audiocontroller.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.andrzej.audiocontroller.R;
import com.example.andrzej.audiocontroller.adapters.ExploreRecyclerAdapter;
import com.example.andrzej.audiocontroller.interfaces.OnItemClickListener;
import com.example.andrzej.audiocontroller.models.ExploreItem;
import com.example.andrzej.audiocontroller.utils.Image;
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
public class ExploreFragment extends Fragment implements OnItemClickListener, View.OnClickListener {

    //Objects
    private RecyclerView.LayoutManager manager;
    private ExploreRecyclerAdapter mAdapter;

    private List<ExploreItem> mDataset;

    //Vitals
    private boolean isGrid = true;

    //View bindings
    @Bind(R.id.exploreRecyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.expand_fab)
    FloatingActionsMenu parentFabBtn;
    @Bind(R.id.refreshBtn)
    FloatingActionButton refreshBtn;
    @Bind(R.id.changeViewBtn)
    FloatingActionButton changeViewBtn;

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

        mDataset = generateDataset();

        //Configure recycler view
        mRecyclerView.setHasFixedSize(true);
        setRecyclerType(true);


        //Listeners
        refreshBtn.setOnClickListener(this);
        changeViewBtn.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onItemClick(View v, int position) {
        Toast.makeText(getActivity(), mAdapter.getItem(position).getName(), Toast.LENGTH_SHORT).show();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refreshBtn:
                Toast.makeText(getActivity(), R.string.refresh, Toast.LENGTH_SHORT).show();
                break;
            case R.id.changeViewBtn:
                setRecyclerType(!isGrid);
                parentFabBtn.collapse();
                break;
        }
    }

}
